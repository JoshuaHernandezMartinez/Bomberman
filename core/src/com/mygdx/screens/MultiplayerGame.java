package com.mygdx.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.mygdx.components.AnimationComponent;
import com.mygdx.components.CellComponent;
import com.mygdx.components.IdleComponent;
import com.mygdx.components.OnlinePlayerComponent;
import com.mygdx.components.PhysicComponent;
import com.mygdx.components.PlayerComponent;
import com.mygdx.components.RegionComponent;
import com.mygdx.components.SpeedComponent;
import com.mygdx.components.StateComponent;
import com.mygdx.game.Game;
import com.mygdx.networking.PlayerClient;
import com.mygdx.utils.Assets;
import com.mygdx.utils.Constants;
import com.mygdx.utils.GamePreferences;
import com.mygdx.utils.InputHandler;
import com.mygdx.utils.Mappers;
import com.mygdx.utils.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MultiplayerGame extends GameWorld{

    private Socket socket;

    private final float UPDATE_TIME = 1/2f;

    private float time;

    private Entity playerEntity;

    public MultiplayerGame(Game game, Socket socket, Array<PlayerClient> clients, int mapIndex, OrthographicCamera camera) {
        super(game, camera);

        this.socket = socket;
        time = 0;

        // create player ==============================================

        int[] player_1_keys = new int[5];

        player_1_keys[Constants.LEFT] =
                GamePreferences.instance.player_keys[Constants.LEFT];
        player_1_keys[Constants.RIGHT] =
                GamePreferences.instance.player_keys[Constants.RIGHT];
        player_1_keys[Constants.UP] =
                GamePreferences.instance.player_keys[Constants.UP];
        player_1_keys[Constants.DOWN] =
                GamePreferences.instance.player_keys[Constants.DOWN];
        player_1_keys[Constants.BOMB] =
                GamePreferences.instance.player_keys[Constants.BOMB];

        Entity online_player = null;
        OnlinePlayerComponent opc = null;

        for(int i = 0; i < clients.size; i++){

            int maze_x = 1;
            int maze_y = 1;

            switch (i){
                case 0:
                    maze_x = 1;
                    maze_y = 1;
                    break;
                case 1:
                    maze_x = 1;
                    maze_y = Constants.MAZE_HEIGHT - 2;
                    break;
                case 2:
                    maze_x = Constants.MAZE_WIDTH - 2;
                    maze_y = Constants.MAZE_HEIGHT - 2;
                    break;
                case 3:
                    maze_x = Constants.MAZE_WIDTH - 2;
                    maze_y = 1;
                    break;
                default:
                    System.out.println("Error, there are more than 4 players");
                    break;
            }

            if(clients.get(i).id.equals(socket.id())){
                playerEntity = createPlayer(maze_x, maze_y);

                PlayerComponent playerComponent = new PlayerComponent(player_1_keys);
                InputHandler inputHandler = new InputHandler(this, playerEntity);
                opc = new OnlinePlayerComponent(socket.id());

                inputMultiplexer.addProcessor(inputHandler);
                playerEntity.add(playerComponent);
                playerEntity.add(opc);
                engine.addEntity(playerEntity);

            }else{
                // create online player

                online_player = createPlayer(maze_x, maze_y);
                opc = new OnlinePlayerComponent(clients.get(i).id);

                online_player.add(opc);
                engine.addEntity(online_player);
            }

        }

        // create map

        String map = "";

        switch (mapIndex){
            case 0:
                map = Constants.bricks;
                break;
            case 1:
                map = Constants.lava;
                break;
            case 2:
                map = Constants.normal;
        }

        this.createMap(map);

        setEventHandlers();

    }

    @Override
    public void render(float dt) {
        super.render(dt);

        time += dt;

        if(time > UPDATE_TIME){
            time = 0;
            JSONObject data = new JSONObject();

            try {

                Vector2 pos = Mappers.physic.get(playerEntity).getBody().getPosition();

                data.put("x", pos.x);
                data.put("y", pos.y);
                socket.emit("playerPos", data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void playerMoved(int dir){
        if(dir != -1)
            socket.emit("playerMoved", dir);
        else
            socket.emit("playerStopped");
    }

    public void emit_bomb(float x, float y, int power){

        JSONObject data = new JSONObject();

        try {
            data.put("x", x);
            data.put("y", y);
            data.put("power", power);
            socket.emit("bombDropped", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void emit_power_up(int type, CellComponent cell){

        // test if this is the host

        if(engine.getEntities().indexOf(playerEntity, true) == 0){

            createPowerUp(type, cell);

            JSONObject data = new JSONObject();
            try {
                data.put("col", cell.getCol());
                data.put("row", cell.getRow());
                data.put("type", type);
                socket.emit("powerUpDropped", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void update_client_speed(float speed){
        socket.emit("playerSpeed", (int)speed);
    }

    public void emit_player_dead(){
        socket.emit("playerDead");
        socket.disconnect();
    }

    public void power_up_taken(float col, float row){

        JSONObject data = new JSONObject();
        try {
            data.put("col", col);
            data.put("row", row);
            socket.emit("powerUpTaken", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setEventHandlers(){

        socket.on("playerMoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject data = (JSONObject)args[0];

                try {
                    String id = data.getString("id");
                    int dir = data.getInt("dir");

                    Family playerFamily = Family.all(OnlinePlayerComponent.class).get();
                    ImmutableArray<Entity> players_entities = engine.getEntitiesFor(playerFamily);

                    for(Entity p: players_entities) {

                        OnlinePlayerComponent opc = Mappers.online.get(p);

                        if (opc.getId().equals(id)) {
                            StateComponent sc = Mappers.state.get(p);
                            IdleComponent ic = Mappers.idle.get(p);

                            sc.setCurrentState(dir);
                            ic.setIdle(false);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("playerPos", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject data = (JSONObject)args[0];

                try {
                    String id = data.getString("id");
                    JSONObject position = data.getJSONObject("pos");

                    float x = (float)position.getDouble("x");
                    float y = (float)position.getDouble("y");

                    Family playerFamily = Family.all(OnlinePlayerComponent.class).get();
                    ImmutableArray<Entity> players_entities = engine.getEntitiesFor(playerFamily);

                    for(Entity p: players_entities){

                        OnlinePlayerComponent opc = Mappers.online.get(p);

                        if(opc.getId().equals(id)){

                            PhysicComponent pc = Mappers.physic.get(p);

                            Vector2 pos = pc.getBody().getPosition();

                            float diff_x = (pos.x - x);
                            float diff_y = (pos.y - y);

                            if(Math.abs(diff_x) > Constants.CELL_SIZE / 2 / Constants.PPM ||
                                    Math.abs(diff_y) > Constants.CELL_SIZE / 2 / Constants.PPM){
                                pc.getBody().setTransform(x, y, pc.getBody().getAngle());
                            }

                            break;

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("playerStopped", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                String id = (String)args[0];

                Family playerFamily = Family.all(OnlinePlayerComponent.class).get();
                ImmutableArray<Entity> players_entities = engine.getEntitiesFor(playerFamily);

                for(Entity p: players_entities) {

                    OnlinePlayerComponent opc = Mappers.online.get(p);

                    if (opc.getId().equals(id)) {
                        IdleComponent ic = Mappers.idle.get(p);
                        ic.setIdle(true);
                    }
                }

            }
        }).on("bombDropped", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject data = (JSONObject)args[0];

                try {
                    float x = (float) data.getDouble("x");
                    float y = (float)data.getDouble("y");
                    int power = data.getInt("power");

                    createBomb(x, y, power, null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("powerUpDropped", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject data = (JSONObject)args[0];

                try {
                    float col = (float) data.getDouble("col");
                    float row = (float)data.getDouble("row");
                    int type = data.getInt("type");

                    // get corresponding cell

                    Family cellsFamily = Family.all(CellComponent.class).get();

                    ImmutableArray<Entity> cells = engine.getEntitiesFor(cellsFamily);

                    for(Entity cell: cells){

                        CellComponent cellComponent = Mappers.cell.get(cell);

                        if(cellComponent.getRow() == row && cellComponent.getCol() == col){

                            // create power up

                            createPowerUp(type, cellComponent);

                            break;
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).on("powerUpTaken", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject data = (JSONObject)args[0];

                try {
                    float row = (float)data.getDouble("row");
                    float col = (float)data.getDouble("col");

                    // get corresponding cell

                    Family cellsFamily = Family.all(CellComponent.class).get();

                    ImmutableArray<Entity> cells = engine.getEntitiesFor(cellsFamily);

                    for(Entity cell: cells){

                        CellComponent cellComponent = Mappers.cell.get(cell);

                        if(cellComponent.getRow() == row && cellComponent.getCol() == col){

                            if(cellComponent.getPower_up() != null){
                                engine.removeEntity(cellComponent.getPower_up());
                            }
                            cellComponent.setPower_up(null);
                            break;
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).on("playerDead", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                String id = (String)args[0];

                // get the player by id

                Family playerFamily = Family.all(OnlinePlayerComponent.class).get();
                ImmutableArray<Entity> players = engine.getEntitiesFor(playerFamily);

                for(Entity e: players){

                    OnlinePlayerComponent opc = Mappers.online.get(e);

                    if(opc.getId().equals(id)){
                        engine.removeEntity(e);
                        break;
                    }
                }

                if(engine.getEntitiesFor(playerFamily).size() == 1)
                    player_win(playerEntity);

            }
        }).on("playerSpeed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                String id = (String)args[1];
                int speed = (Integer)args[0];

                Family playerFamily = Family.all(OnlinePlayerComponent.class).get();
                ImmutableArray<Entity> players = engine.getEntitiesFor(playerFamily);

                for(Entity e: players){

                    OnlinePlayerComponent opc = Mappers.online.get(e);

                    if(opc.getId().equals(id)){

                        SpeedComponent sc = Mappers.speed.get(e);

                        sc.setSpeed((float)speed);
                        break;
                    }
                }
            }
        });
    }

}
