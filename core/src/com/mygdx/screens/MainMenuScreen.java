package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Game;
import com.mygdx.networking.PlayerClient;
import com.mygdx.utils.Assets;
import com.mygdx.utils.Constants;
import com.mygdx.utils.GamePreferences;
import com.mygdx.utils.LanguageManager;
import com.mygdx.utils.Languages;
import com.mygdx.utils.ScreenTransition;
import com.mygdx.utils.ScreenTransitionSlice;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.touchable;

public class MainMenuScreen extends DefaultScreen{

    private I18NBundle bundle;

    // added code

    private Skin skinLibgdx;
    private Stage stage;

    private Image background;
    private Window options;
    private TextButton save;
    private TextButton options_cancel;
    private Button checkBoxSound;
    private Slider sliderSound;
    private Button checkBoxMusic;
    private Slider sliderMusic;
    private SelectBox<String> languages;
    private Window login;
    private TextField name_field;
    private TextButton join_btn;
    private TextButton login_cancel;
    private CheckBox showFps;

    private Label single_player_mode;
    private Label versus_mode;
    private Label multiplayer_mode;
    private Label options_btn;
    private Label credits_btn;
    private Label exit_btn;

    private InputMultiplexer inputMultiplexer;
    private PlayersInputHandler playersInputHandler;
    private SnapshotArray<TextButton> inputButtons;


    public MainMenuScreen(final Game game){
        super(game);
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT));
        inputButtons = new SnapshotArray<TextButton>();
        playersInputHandler = new PlayersInputHandler();
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(playersInputHandler);
        inputMultiplexer.addProcessor(stage);
        rebuildStage();
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    private void rebuildStage(){
        skinLibgdx = Assets.getInstance().libgdxSkin;
        bundle = LanguageManager.getCurrentBundle();

        Table layerBackground = buildBackgroundLayer();
        Table layerControls = buildControlsLayer();
        Table layerOptionsWindow = buildOptionsWindowLayer();
        Table layerLoginWindow = buildLoginWindow();

        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControls);
        stage.addActor(layerOptionsWindow);
        stage.addActor(layerLoginWindow);

    }

    private Table buildBackgroundLayer(){
        Table layer = new Table();
        background =  new Image(Assets.getInstance().title_background);
        layer.add(background);
        return layer;
    }

    private Table buildControlsLayer(){
        Table layer = new Table();
        Label.LabelStyle labelStyle =  new Label.LabelStyle(Assets.getInstance().font, Color.WHITE);

        single_player_mode = new Label(bundle.get("single_player_mode"), labelStyle);
        versus_mode = new Label(bundle.get("versus_mode"), labelStyle);
        multiplayer_mode = new Label(bundle.get("multiplayer_mode"), labelStyle);
        options_btn = new Label(bundle.get("options"), labelStyle);
        credits_btn = new Label(bundle.get("credits"), labelStyle);
        exit_btn = new Label(bundle.get("exit"), labelStyle);

        layer.row();
        layer.add(single_player_mode).padBottom(25).padTop(150f);
        layer.row();
        layer.add(versus_mode).padBottom(25);
        layer.row();
        layer.add(multiplayer_mode).padBottom(25);
        layer.row();
        layer.add(options_btn).padBottom(25);
        layer.row();
        layer.add(credits_btn).padBottom(25);
        layer.row();
        layer.add(exit_btn);

        single_player_mode.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                single_player_mode.setColor(Color.GREEN);
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                single_player_mode.setColor(Color.WHITE);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onPlayClicked(Constants.ONE_PLAYER_MODE);
            }
        });

        versus_mode.addListener(new ClickListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                versus_mode.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                versus_mode.setColor(Color.WHITE);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onPlayClicked(Constants.TWO_PLAYER_MODE);
            }
        });

        multiplayer_mode.addListener(new ClickListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                multiplayer_mode.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                multiplayer_mode.setColor(Color.WHITE);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showLoginWindow();
            }
        });

        createOptionsListener();
        createCreditsListener();
        createExitListener();

        return layer;
    }

    private Table buildOptionsWindowLayer(){

        options = new Window("Options", skinLibgdx);
        options.getTitleLabel().setAlignment(Align.center);
        options.add(buildAudioSettings()).row();
        options.add(buildPlayerInputKeys()).row();
        options.add(buildLanguageSelection()).row();
        options.add(buildWindowButtons()).pad(10, 0, 10, 0);

        options.setColor(1, 1, 1, 0.8f);
        showWindow(false, false, options);
        options.pack();
        options.setPosition(Constants.VIEWPORT_WIDTH/2 - options.getWidth()/2,
                Constants.VIEWPORT_HEIGHT/2 - options.getHeight()/2);
        return options;
    }

    private Table buildPlayerInputKeys(){

        final GamePreferences gamePreferences = GamePreferences.instance;

        gamePreferences.player_keys = new int[10];
        gamePreferences.load();

        TextButton player_1_right = new TextButton(Input.Keys.toString(gamePreferences.player_keys[Constants.RIGHT]), skinLibgdx, "round");
        player_1_right.setName(Integer.toString(Constants.RIGHT)+Integer.toString(0));
        TextButton player_2_right = new TextButton(Input.Keys.toString(gamePreferences.player_keys[Constants.RIGHT + 5]), skinLibgdx, "round");
        player_2_right.setName(Integer.toString(Constants.RIGHT)+Integer.toString(5));

        TextButton player_1_left = new TextButton(Input.Keys.toString(gamePreferences.player_keys[Constants.LEFT]), skinLibgdx, "round");
        player_1_left.setName(Integer.toString(Constants.LEFT)+Integer.toString(0));
        TextButton player_2_left = new TextButton(Input.Keys.toString(gamePreferences.player_keys[Constants.LEFT + 5]), skinLibgdx, "round");
        player_2_left.setName(Integer.toString(Constants.LEFT)+Integer.toString(5));

        TextButton player_1_up = new TextButton(Input.Keys.toString(gamePreferences.player_keys[Constants.UP]), skinLibgdx, "round");
        player_1_up.setName(Integer.toString(Constants.UP)+Integer.toString(0));
        TextButton player_2_up = new TextButton(Input.Keys.toString(gamePreferences.player_keys[Constants.UP + 5]), skinLibgdx, "round");
        player_2_up.setName(Integer.toString(Constants.UP)+Integer.toString(5));

        TextButton player_1_down = new TextButton(Input.Keys.toString(gamePreferences.player_keys[Constants.DOWN]), skinLibgdx, "round");
        player_1_down.setName(Integer.toString(Constants.DOWN)+Integer.toString(0));
        TextButton player_2_down = new TextButton(Input.Keys.toString(gamePreferences.player_keys[Constants.DOWN + 5]), skinLibgdx, "round");
        player_2_down.setName(Integer.toString(Constants.DOWN)+Integer.toString(5));

        TextButton player_1_bomb = new TextButton(Input.Keys.toString(gamePreferences.player_keys[Constants.BOMB]), skinLibgdx, "round");
        player_1_bomb.setName(Integer.toString(Constants.BOMB)+Integer.toString(0));
        TextButton player_2_bomb = new TextButton(Input.Keys.toString(gamePreferences.player_keys[Constants.BOMB + 5]), skinLibgdx, "round");
        player_2_bomb.setName(Integer.toString(Constants.BOMB)+Integer.toString(5));

        Table table = new Table();
        table.defaults().expandY();

        table.add(new Label(bundle.get("controls"), skinLibgdx, "title-plain")).colspan(3).padLeft(270).padBottom(20);
        table.row().colspan(3);
        table.add(new Label(bundle.get("key"), skinLibgdx, "title-plain")).padRight(80);
        table.add(new Label(bundle.get("player_1"), skinLibgdx, "title-plain")).padRight(60);
        table.add(new Label(bundle.get("player_2"), skinLibgdx, "title-plain")).padLeft(60);
        table.row().colspan(3).padTop(20);
        table.add(new Label(bundle.get("right"), skinLibgdx, "title-plain")).padRight(80);
        table.add(player_1_right).padRight(60);
        table.add(player_2_right).padLeft(60);
        table.row().colspan(3);
        table.add(new Label(bundle.get("left"), skinLibgdx, "title-plain")).padRight(80);
        table.add(player_1_left).padRight(60);
        table.add(player_2_left).padLeft(60);
        table.row().colspan(3);
        table.add(new Label(bundle.get("up"), skinLibgdx, "title-plain")).padRight(80);
        table.add(player_1_up).padRight(60);
        table.add(player_2_up).padLeft(60);
        table.row().colspan(3);
        table.add(new Label(bundle.get("down"), skinLibgdx, "title-plain")).padRight(80);
        table.add(player_1_down).padRight(60);
        table.add(player_2_down).padLeft(60);
        table.row().colspan(3);
        table.add(new Label(bundle.get("bomb"), skinLibgdx, "title-plain")).padRight(80);
        table.add(player_1_bomb).padRight(60);
        table.add(player_2_bomb).padLeft(60);

        for(Actor a: table.getChildren()){
            if(a instanceof TextButton){
                inputButtons.add((TextButton) a);
            }
        }

        return table;
    }

    private Table buildWindowButtons(){
        Table table = new Table();
        Label lb = new Label("", skinLibgdx);
        lb.setColor(0.75f, 0.75f, 0.75f, 1);
        lb.setStyle(new Label.LabelStyle(lb.getStyle()));
        lb.getStyle().background = skinLibgdx.newDrawable("white");
        table.add(lb).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
        table.row();
        lb = new Label("", skinLibgdx);
        lb.setColor(0.5f, 0.5f, 0.5f, 1);
        lb.setStyle(new Label.LabelStyle(lb.getStyle()));
        lb.getStyle().background = skinLibgdx.newDrawable("white");
        table.add(lb).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
        table.row();

        save = new TextButton(bundle.get("save"), skinLibgdx, "round");
        table.add(save).padRight(30);
        save.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onSaveClicked();
            }
        });

        options_cancel = new TextButton(bundle.get("cancel"), skinLibgdx, "round");
        table.add(options_cancel);
        options_cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onOptionsCancelClicked();
            }
        });
        return table;
    }

    private Table buildLanguageSelection(){
        Table table = new Table();
        table.pad(10, 10, 0, 10);
        table.add(new Label(bundle.get("language"), skinLibgdx, "title-plain")).colspan(2);
        table.row();
        languages = new SelectBox<String>(skinLibgdx);

        Array<String> languagesList = new Array<String>();

        for(Languages l: Languages.values()){
            languagesList.add(bundle.get(l.name().toLowerCase()));
        }

        languages.setItems(languagesList);

        table.add(languages).width(120);
        return table;
    }

    private Table buildAudioSettings(){
        Table table = new Table();
        table.pad(10, 10, 0, 10);
        table.add(new Label(bundle.get("audio"), skinLibgdx, "title-plain")).colspan(3);
        table.row();
        table.columnDefaults(0).padRight(10);
        table.columnDefaults(1).padRight(10);
        // + Checkbox, "Sound" label, sound volume slider
        checkBoxSound = new Button(skinLibgdx, "sound");
        table.add(checkBoxSound);
        table.add(new Label(bundle.get("sound"), skinLibgdx, "title-plain"));
        sliderSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
        table.add(sliderSound);
        table.row();
        // + Checkbox, "Music" label, music volume slider
        checkBoxMusic = new Button(skinLibgdx, "music");
        table.add(checkBoxMusic);
        table.add(new Label(bundle.get("music"), skinLibgdx, "title-plain"));
        sliderMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
        table.add(sliderMusic);
        table.row();
        return table;
    }

    private Array<PlayerClient> clients;

    private Table buildLoginWindow(){
        login = new Window("Login", skinLibgdx);
        login.getTitleLabel().setAlignment(Align.center);

        name_field = new TextField("Joshua", skinLibgdx);
        name_field.setMessageText("Your Name");
        name_field.setColor(0.2f, 0.4f, 0.3f, 0.8f);
        name_field.getStyle().fontColor = Color.WHITE;

        join_btn = new TextButton("Join", skinLibgdx, "round");
        login_cancel = new TextButton("Cancel", skinLibgdx, "round");

        login.add(new Label("Name: ", skinLibgdx, "title-plain")).colspan(2).pad(10);
        login.add(name_field).colspan(2).pad(10, 0f, 10, 10f);
        login.row();
        login.add(join_btn).colspan(2).pad(0f, 15f, 10f, 0f);
        login.add(login_cancel).colspan(2).pad(0f, 15f, 10f, 0f);

        login.setColor(1, 1, 1, 1f);
        showWindow(false, false, login);
        login.pack();
        login.setPosition(Constants.VIEWPORT_WIDTH/2 - login.getWidth()/2,
                Constants.VIEWPORT_HEIGHT/2 - login.getHeight()/2);

        join_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                String name = name_field.getText();

                if(!name.isEmpty()){

                    game.setScreen(new GameLobby(game, name), ScreenTransitionSlice.init(1f,
                            ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out));
                }

            }
        });

        login_cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onLoginCancelClicked();
            }
        });

        return login;
    }

    private void onPlayClicked(int option){
        ScreenTransition sliceTransition = ScreenTransitionSlice.init(1f,
                ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out);

        PlayScreen playScreen = new PlayScreen(game);
        game.setScreen(playScreen, sliceTransition);

        if(option == Constants.ONE_PLAYER_MODE)
            playScreen.start_solo_game();
        else
            playScreen.start_versus_game();

    }

    private void onOptionsClicked(){
        loadSettings();
        showMenuButtons(false);
        showWindow(true, true, options);
    }

    private void onExitClicked(){
        Dialog dialog = new Dialog(
                bundle.get("exit_warning"), skinLibgdx, "dialog"){
            @Override
            protected void result(Object object) {
                super.result(object);
                boolean obj = (Boolean) object;
                if(obj)
                    Gdx.app.exit();

            }
        };
        dialog.setHeight(60);
        dialog.button(bundle.get("yes"), true).padLeft(40);
        dialog.button(bundle.get("no_"), false).padRight(40);
        dialog.show(stage);
        createExitListener();
    }

    private void onSaveClicked(){
        saveSettings();
        onOptionsCancelClicked();
    }

    private void onOptionsCancelClicked(){
        showMenuButtons(true);
        showWindow(false, true, options);
        createOptionsListener();

    }

    private void onLoginCancelClicked(){
        showMenuButtons(true);
        showWindow(false, true, login);
        createLoginListener();

    }


    private void loadSettings(){
        GamePreferences preferences = GamePreferences.instance;
        preferences.load();
        checkBoxSound.setChecked(preferences.sound);
        sliderSound.setValue(preferences.volSound);
        checkBoxMusic.setChecked(preferences.music);
        sliderMusic.setValue(preferences.volMusic);
        languages.setSelectedIndex(preferences.language);
    }

    private void saveSettings(){
        GamePreferences preferences = GamePreferences.instance;
        preferences.sound = checkBoxSound.isChecked();
        preferences.volSound = sliderSound.getValue();
        preferences.music = checkBoxMusic.isChecked();
        preferences.volMusic = sliderMusic.getValue();
        preferences.language = languages.getSelectedIndex();
        preferences.save();

        Dialog dialog = new Dialog(bundle.get("saved_message"), skinLibgdx, "dialog");
        dialog.button("Ok").pad(20,40,0,40);
        dialog.show(stage);

    }

    private void showWindow(boolean visible, boolean animated, Window window){
        float alphaTo = visible ? 0.8f: 0.0f;
        float duration = animated ? 1.0f: 0.0f;
        Touchable touchableEnabled = visible ?  Touchable.enabled : Touchable.disabled;
        window.addAction(sequence(touchable(touchableEnabled), alpha(alphaTo, duration)));
    }

    private void showLoginWindow(){
        showMenuButtons(false);
        showWindow(true,true, login);
    }

    private void showMenuButtons(boolean visible){
        float moveDuration = 1.0f;
        Interpolation moveEasing = Interpolation.swing;
        float delayButton = 0.2f;
        float moveX = (Constants.VIEWPORT_WIDTH / 2 + 300) * (visible ? -1 : 1);
        float moveY = 0;//(Constants.VIEWPORT_HEIGHT) * (visible ? -1 : 1);
        final Touchable touchableEnabled = visible ? Touchable.enabled: Touchable.disabled;
        single_player_mode.addAction(moveBy(moveX, moveY, moveDuration, moveEasing));
        versus_mode.addAction(sequence(delay(delayButton), moveBy(moveX, moveY, moveDuration, moveEasing)));
        multiplayer_mode.addAction(sequence(delay(delayButton*2), moveBy(moveX, moveY, moveDuration, moveEasing)));
        options_btn.addAction(sequence(delay(delayButton*3), moveBy(moveX, moveY, moveDuration, moveEasing)));
        credits_btn.addAction(sequence(delay(delayButton*4), moveBy(moveX, moveY, moveDuration, moveEasing)));
        exit_btn.addAction(sequence(delay(delayButton*5), moveBy(moveX, moveY, moveDuration, moveEasing)));

        SequenceAction seq = sequence();

        if(visible)
            seq.addAction(delay(delayButton + moveDuration));

        seq.addAction(run(new Runnable() {
            @Override
            public void run() {
                single_player_mode.setTouchable(touchableEnabled);
                versus_mode.setTouchable(touchableEnabled);
                multiplayer_mode.setTouchable(touchableEnabled);
            }
        }));
        stage.addAction(seq);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void pause() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public InputProcessor getInputProcessor() {
        return inputMultiplexer;
    }

    @Override
    public String toString() {
        return "Main Menu Screen";
    }
    // players input handler

    class PlayersInputHandler extends InputAdapter{
        @Override
        public boolean keyDown(int keycode) {

            for(TextButton t: inputButtons){
                if(t.isPressed()){
                    t.setText(Input.Keys.toString(keycode));
                    String name = t.getName();
                    int key = Integer.parseInt(name.substring(0, 1));
                    int player = Integer.parseInt(name.substring(1));

                    GamePreferences.instance.player_keys[key + player] = keycode;
                }
            }
            return false;
        }
    }

    // create listeners

    private void createOptionsListener(){
        options_btn.addListener(new ClickListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                options_btn.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                options_btn.setColor(Color.WHITE);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onOptionsClicked();
            }
        });
    }

    private void createLoginListener(){
        multiplayer_mode.addListener(new ClickListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                multiplayer_mode.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                multiplayer_mode.setColor(Color.WHITE);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showLoginWindow();
            }
        });
    }

    private void createCreditsListener(){
        credits_btn.addListener(new ClickListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                credits_btn.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                credits_btn.setColor(Color.WHITE);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // show credits
            }
        });
    }

    private void createExitListener(){
        exit_btn.addListener(new ClickListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                exit_btn.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                exit_btn.setColor(Color.WHITE);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onExitClicked();
            }
        });
    }

}
