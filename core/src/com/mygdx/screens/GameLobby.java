package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Game;
import com.mygdx.networking.PlayerClient;
import com.mygdx.utils.Assets;
import com.mygdx.utils.Constants;
import com.mygdx.utils.LanguageManager;
import com.mygdx.utils.ScreenTransition;
import com.mygdx.utils.ScreenTransitionSlice;
import com.mygdx.utils.ScreenTransitionSlide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameLobby extends DefaultScreen{

    private I18NBundle bundle;

    private Skin skin;
    private Stage stage;

    private List<String> players_list;
    private Array<PlayerClient> clients;
    private Socket socket;

    private float waitTime;
    private float time;
    private boolean waiting;

    private Window startingWindow;
    private Label timeIndicator;
    private TextButton cancelButton;

    private String name;

    public GameLobby(Game game, String name) {
        super(game);

        this.name = name;
        clients = new Array<PlayerClient>();
        waitTime = 5;
        time = 0;
        waiting = false;
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(Constants.VIEWPORT_WIDTH_GUI,
                Constants.VIEWPORT_HEIGHT_GUI));
        skin = Assets.getInstance().libgdxSkin;
        bundle = LanguageManager.getCurrentBundle();

        Gdx.input.setInputProcessor(stage);

        try {
            socket = IO.socket("http://localhost:3000");
            socket.connect();
            handleSocketEvents();
            rebuildStage();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void handleSocketEvents(){

        final Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("set_data", name);
            }
        }).on("get_players", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                clients = new Array<PlayerClient>();

                JSONArray array = (JSONArray)args[0];

                mapIndex = (Integer)args[1];

                try{
                    for(int i = 0; i < array.length(); i++){
                        String name = array.getJSONObject(i).getString("name");
                        boolean ready = array.getJSONObject(i).getBoolean("ready");
                        String id = array.getJSONObject(i).getString("id");
                        int session_id = array.getJSONObject(i).getInt("session_id");

                        clients.add(new PlayerClient(id, name, session_id, ready));
                        updatePlayersList();
                    }

                    for(int i = 0; i < clients.size; i++){
                        if(clients.get(i).id.equals(socket.id())){
                            players_list.setSelectedIndex(i);
                            break;
                        }
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }).on("new_player", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject player = (JSONObject)args[0];

                try {
                    int session_id = player.getInt("session_id");
                    String name = player.getString("name");
                    String id = player.getString("id");

                    if(clients.get(0).session_id == session_id){
                        clients.add(new PlayerClient(id, name, session_id, false));
                        updatePlayersList();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("player_message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                String id = (String)args[0];
                String msg = (String)args[1];
                int session_id = (Integer)args[2];

                if(clients.get(0).session_id != session_id)
                    return;

                for(PlayerClient pc: clients){
                    if(pc.id.equals(id)){
                        chatLabel.setText(chatLabel.getText() + "<"+pc.name+"> : " + msg);
                        break;
                    }
                }
            }
        }).on("player_disconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject player = (JSONObject)args[0];

                try {
                    int session_id = player.getInt("session_id");
                    String id = player.getString("id");

                    if(clients.get(0).session_id != session_id)
                        return;


                    for(int i = 0; i < clients.size; i++){
                        PlayerClient pc = clients.get(i);
                        if(pc.id.equals(id)){
                            clients.removeIndex(i);
                        }
                    }

                    updatePlayersList();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("player_toggle", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                int session_id = (Integer)args[1];
                String id = (String)args[0];

                if(clients.get(0).session_id != session_id)
                    return;

                for(int i = 0; i < clients.size; i++){
                    PlayerClient pc = clients.get(i);
                    if(pc.id.equals(id)){
                        pc.ready = !pc.ready;
                    }
                }

                updatePlayersList();

            }
        }).on("game_ready", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                waiting = true;
                startingWindow = new Window("Starting Game", skin);
                timeIndicator = new Label("Starting Game In: " + (5 - (int)time), skin, "title-plain");
                cancelButton = new TextButton("Cancel", skin);

                startingWindow.getTitleLabel().setAlignment(Align.center);
                startingWindow.add(timeIndicator).width(250f).height(50f).center().padLeft(10f);
                startingWindow.row();
                startingWindow.add(cancelButton);
                startingWindow.pack();
                startingWindow.setPosition(Constants.VIEWPORT_WIDTH_GUI/2 - startingWindow.getWidth()/2,
                        Constants.VIEWPORT_HEIGHT_GUI/2 - startingWindow.getHeight()/2);

                stage.addActor(startingWindow);

                cancelButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        toggle();
                    }
                });


            }
        }).on("game_canceled", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(waiting){
                    waiting = false;
                    time = 0;
                    startingWindow.addAction(Actions.removeActor());
                }
            }
        }).on("set_map", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                int session_id = (Integer)args[1];

                if(clients.get(0).session_id != session_id)
                    return;

                mapIndex = (Integer) args[0];

                map.setDrawable(new TextureRegionDrawable(maps.get(mapIndex)));

            }
        });

    }

    private TextButton ready_btn;
    private TextButton return_btn;
    private TextButton send_btn;
    private Button left_btn;
    private Button right_btn;

    private void rebuildStage(){
        Table gameInfoTable = buildGameInfoTable();
        Table chatTable = buildChatTable();
        Table mapTable = buildMapTable();
        Table rootTable = new Table();
        rootTable.setFillParent(true);

        ready_btn = new TextButton("Ready", skin, "toggle");
        return_btn = new TextButton("Return", skin);
        send_btn = new TextButton("Send", skin);
        left_btn = new Button(skin, "left");
        right_btn = new Button(skin, "right");

        rootTable.add(new Label("GAME LOBBY", skin, "title")).colspan(3);
        rootTable.add(new Label("CHAT ROOM", skin, "title")).colspan(3);
        rootTable.add(new Label("SELECT MAP", skin, "title")).colspan(3);
        rootTable.row();
        rootTable.add(gameInfoTable).colspan(3);
        rootTable.add(chatTable).colspan(3);
        rootTable.add(mapTable).colspan(3);
        rootTable.row();
        rootTable.add(ready_btn).colspan(3);
        rootTable.add(return_btn).colspan(1);
        rootTable.add(send_btn).colspan(2);
        rootTable.add(left_btn).colspan(1);
        rootTable.add(right_btn).colspan(3);

        ready_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggle();
            }
        });

        return_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                socket.disconnect();

                ScreenTransitionSlide slideTransition =  ScreenTransitionSlide.init(1.5f,
                        ScreenTransitionSlide.DOWN, false, Interpolation.bounceOut);

                game.setScreen(new MainMenuScreen(game), slideTransition);
            }
        });

        send_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!message_field.getText().isEmpty()){

                    socket.emit("player_message", message_field.getText() + "\n", clients.get(0).session_id);
                    message_field.setText("");
                }
            }
        });

        left_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mapIndex --;
                if(mapIndex < 0)
                    mapIndex = maps.size - 1;

                setMap(mapIndex);

            }
        });

        right_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mapIndex ++;
                if(mapIndex > maps.size - 1)
                    mapIndex = 0;

                setMap(mapIndex);
            }
        });

        stage.clear();
        stage.addActor(rootTable);
    }

    private void setMap(int index){

        if(!isHost())
            return;

        socket.emit("set_map", index);
    }

    private boolean isHost(){
        if(clients.size > 0)
            return socket.id().equals(clients.get(0).id);
        return false;
    }

    private void toggle(){
        socket.emit("player_toggle");
    }

    private Label chatLabel;
    private ScrollPane chat_scroll;
    private TextField message_field;

    private Table buildChatTable(){
        Table table = new Table();

        chatLabel = new Label("", skin);
        chatLabel.setWrap(true);
        chatLabel.setAlignment(Align.topLeft);

        chat_scroll = new ScrollPane(chatLabel, skin);
        chat_scroll.setFadeScrollBars(false);
        chat_scroll.pack();

        message_field = new TextField("", skin);
        message_field.setMessageText("Message");
        message_field.setColor(0.2f, 0.4f, 0.3f, 1f);
        message_field.getStyle().fontColor = Color.WHITE;

        message_field.addListener(new InputListener(){
            @Override
            public boolean keyUp(InputEvent event, int keycode) {

                if(keycode == Input.Keys.ENTER && !message_field.getText().isEmpty()){
                    socket.emit("player_message", message_field.getText() + "\n", clients.get(0).session_id);
                    message_field.setText("");
                }

                return true;
            }
        });

        table.add(chat_scroll).width(320).height(220);
        table.row();
        table.add(message_field).width(320).height(20);

        return table;
    }

    private Table buildGameInfoTable(){
        Table table = new Table();
        
        players_list = new List<String>(skin, "dimmed");

        updatePlayersList();

        Label titles = new Label(" Player                      State", skin);
        titles.setColor(Color.RED);

        table.add(new ScrollPane(titles, skin)).align(Align.left).width(220);
        table.row();
        table.add(new ScrollPane(players_list, skin)).width(220).height(210);

        return table;
    }

    private Array<TextureAtlas.AtlasRegion> maps;
    private int mapIndex;
    private Image map;

    private Table buildMapTable(){
        Table table = new Table();
        maps = Assets.getInstance().map_minies.getRegions();
        map = new Image(maps.get(mapIndex));

        table.add(map);

        return table;
    }

    private void updatePlayersList(){
        Array<String> temp = new Array<String>();

        for(PlayerClient p: clients){

            int length = 24 - p.name.length();
            String item = p.name;
            for(int i = 0; i < length; i++){
                item += " ";
            }
            item += (p.ready ? "Ready": "Not Ready");

            temp.add(item);
        }

        players_list.setItems(temp);

        if(map != null){
            map.setDrawable(new TextureRegionDrawable(maps.get(mapIndex)));
        }

    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(waiting){
            time += dt;
            if(time > 1){
                timeIndicator.setText("Starting Game In: " + (5 - (int)time));
            }

            if(time > waitTime){

                ScreenTransition sliceTransition = ScreenTransitionSlice.init(1f,
                        ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out);

                PlayScreen playScreen = new PlayScreen(game);

                game.setScreen(playScreen, sliceTransition);

                for(PlayerClient p: clients)
                    p.ready = false;

                socket.off();

                playScreen.start_multiplayer_game(socket, clients, mapIndex);

                waitTime = 0;
                waiting = false;

            }
        }

        stage.act(dt);
        stage.draw();
    }

    @Override
    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose () {
        stage.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }
}
