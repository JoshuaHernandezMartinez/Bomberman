package com.mygdx.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.mygdx.components.AnimationComponent;
import com.mygdx.components.CellComponent;
import com.mygdx.components.CreepComponent;
import com.mygdx.components.PhysicComponent;
import com.mygdx.components.PlayerComponent;
import com.mygdx.components.RegionComponent;
import com.mygdx.components.SpeedComponent;
import com.mygdx.components.StateComponent;
import com.mygdx.game.Game;
import com.mygdx.systems.CreepSystem;
import com.mygdx.utils.Assets;
import com.mygdx.utils.Constants;
import com.mygdx.utils.GamePreferences;
import com.mygdx.utils.InputHandler;
import com.mygdx.utils.Mappers;
import com.mygdx.utils.UserData;

public class SoloGame extends GameWorld{

    private int creepCount;

    private boolean portal;

    private CreepSystem creepSystem;

    private Entity player;

    public SoloGame(Game game, OrthographicCamera camera) {
        super(game, camera);

        creepSystem = new CreepSystem(this);
        engine.addSystem(creepSystem);

        portal = false;
        creepCount = 0;

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

        player = createPlayer(1,1);

        PlayerComponent playerComponent = new PlayerComponent(player_1_keys);
        InputHandler inputHandler = new InputHandler(this, player);
        inputMultiplexer.addProcessor(inputHandler);
        player.add(playerComponent);
        engine.addEntity(player);

        this.createMap(Constants.lava);

        // start game

        Family cellFamily = Family.all(CellComponent.class).get();

        ImmutableArray<Entity> cells= engine.getEntitiesFor(cellFamily);

        for(int i = 0; i < cells.size(); i++){
            Entity e = cells.get(i);

            CellComponent cellComponent = Mappers.cell.get(e);

            int col = (int)cellComponent.getCol();
            int row = (int)cellComponent.getRow();

            if(cellComponent.getType() == Constants.EMPTY_BLOCK){

                if(row == 1 && col == 1 || row == 2 && col == 1 || row == 1 && col == 2)
                    continue;

                if(MathUtils.random() < Constants.ENEMY_PROB) {
                    createCreep((int) cellComponent.getCol(), (int) cellComponent.getRow());
                }

            }

        }

    }

    @Override
    public void render(float dt) {
        super.render(dt);

        if(creepCount <= 0 && !portal){
            createPortal();
            portal = true;
        }


    }

    private void createCreep(int maze_x, int maze_y){
        Entity creep = new Entity();

        IntMap<Animation> animations = new IntMap<Animation>();

        Animation<TextureRegion> frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                Assets.getInstance().bomberman_atlas.findRegions("creepFront"), Animation.PlayMode.LOOP);

        animations.put(Constants.DOWN, frames);

        frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                Assets.getInstance().bomberman_atlas.findRegions("creepBack"), Animation.PlayMode.LOOP);

        animations.put(Constants.UP, frames);

        frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                Assets.getInstance().bomberman_atlas.findRegions("creepSide"), Animation.PlayMode.LOOP);

        for(TextureRegion t: frames.getKeyFrames())
            t.flip(true, false);

        animations.put(Constants.LEFT, frames);

        frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                Assets.getInstance().bomberman_atlas.findRegions("creepSide"), Animation.PlayMode.LOOP);

        animations.put(Constants.RIGHT, frames);

        Vector2 position = maze_to_world_coords(maze_x,
                maze_y);

        Body body = createBody(position.x, position.y,
                45 / Constants.PPM, true, true, (short)-Constants.CREEP);
        body.setUserData(new UserData(Constants.CREEP));

        creep.add(new CreepComponent());
        creep.add(new AnimationComponent(animations));
        creep.add(new StateComponent(Constants.DOWN));
        creep.add(new PhysicComponent(body, 1));
        creep.add(new RegionComponent());
        creep.add(new SpeedComponent(Constants.MIN_SPEED));

        engine.addEntity(creep);
        creepCount ++;
    }

    private void createPortal(){

        Family cellFamily = Family.all(CellComponent.class).get();
        ImmutableArray<Entity> cells = engine.getEntitiesFor(cellFamily);

        Array<CellComponent> emptyBlocks = new Array<CellComponent>();

        for(int i = 0; i < cells.size(); i++){

            Entity e = cells.get(i);
            CellComponent cellComponent = Mappers.cell.get(e);

            if(cellComponent.getType() == Constants.EMPTY_BLOCK)
                emptyBlocks.add(cellComponent);

        }

        // put the portal randomly

        CellComponent cell = emptyBlocks.get(MathUtils.random(emptyBlocks.size - 1));

        Entity portal = new Entity();

        Vector2 position = maze_to_world_coords(cell.getCol(),
                cell.getRow());

        Body body = createBody(position.x, position.y,
                45 / Constants.PPM, true, true, (short)0);
        body.setUserData(new UserData(Constants.PORTAL));

        portal.add(new RegionComponent(Assets.getInstance().bomberman_atlas.findRegion("Portal")));
        portal.add(new PhysicComponent(body, 0));

        engine.addEntity(portal);

    }

    @Override
    public void beginContact(Contact contact) {
        super.beginContact(contact);

        UserData dataA = (UserData) contact.getFixtureA().getBody().getUserData();
        UserData dataB = (UserData) contact.getFixtureB().getBody().getUserData();

        if(dataA == null || dataB == null)
            return;

        // Collision between player & creep ===============================================

        player_creep_collision(dataA, dataB, contact);

        // Collision between explosion & creep ============================================

        creep_explosion_collision(dataA, dataB, contact);

        // Collision between player & portal =============================================

        player_portal_collision(dataA, dataB, contact);

    }

    private void player_creep_collision(UserData dataA, UserData dataB, Contact contact){

        Family playerFamily;
        ImmutableArray<Entity> players;

        if(dataA.type == Constants.PLAYER && dataB.type == Constants.CREEP ||
                dataB.type == Constants.PLAYER && dataA.type == Constants.CREEP){

            playerFamily = Family.all(PlayerComponent.class).get();
            players = engine.getEntitiesFor(playerFamily);

            for(int i = 0; i < players.size(); i++){

                Entity e = players.get(i);

                PhysicComponent physicComponent = Mappers.physic.get(e);

                if(physicComponent.getBody().equals(contact.getFixtureA().getBody()) ||
                        physicComponent.getBody().equals(contact.getFixtureB().getBody())){
                    player_lose(e);
                    return;
                }

            }
        }
    }

    private void creep_explosion_collision(UserData dataA, UserData dataB, Contact contact){

        Family creepsFamily;
        ImmutableArray<Entity> creeps;

        if(dataA.type == Constants.EXPLOSION && dataB.type == Constants.CREEP ||
                dataB.type == Constants.EXPLOSION && dataA.type == Constants.CREEP){

            creepsFamily = Family.all(CreepComponent.class).get();
            creeps = engine.getEntitiesFor(creepsFamily);

            for(int i = 0; i < creeps.size(); i++){

                Entity e = creeps.get(i);

                PhysicComponent physicComponent = Mappers.physic.get(e);

                if(physicComponent.getBody().equals(contact.getFixtureA().getBody()) ||
                        physicComponent.getBody().equals(contact.getFixtureB().getBody())){

                    engine.removeEntity(e);

                    creepCount --;
                    return;
                }
            }
        }
    }

    private void player_portal_collision(UserData dataA, UserData dataB, Contact contact){

        Family playerFamily;
        ImmutableArray<Entity> players;

        if(dataA.type == Constants.PLAYER && dataB.type == Constants.PORTAL ||
                dataB.type == Constants.PLAYER && dataA.type == Constants.PORTAL){

            Entity player = null;

            playerFamily = Family.all(PlayerComponent.class).get();
            players = engine.getEntitiesFor(playerFamily);

            for(int i = 0; i < players.size(); i++){

                Entity e = players.get(i);

                PhysicComponent physicComponent = Mappers.physic.get(e);

                if(contact.getFixtureB().getBody().equals(physicComponent.getBody()) ||
                        contact.getFixtureA().getBody().equals(physicComponent.getBody())){
                    player = e;
                    break;
                }

            }

            player_win(player);
        }
    }

}
