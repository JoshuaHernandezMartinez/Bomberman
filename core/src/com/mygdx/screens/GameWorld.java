package com.mygdx.screens;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.mygdx.components.AnimationComponent;
import com.mygdx.components.BombComponent;
import com.mygdx.components.CellComponent;
import com.mygdx.components.IdleComponent;
import com.mygdx.components.PhysicComponent;
import com.mygdx.components.PlayerComponent;
import com.mygdx.components.PowerUpComponent;
import com.mygdx.components.RegionComponent;
import com.mygdx.components.SpeedComponent;
import com.mygdx.components.StateComponent;
import com.mygdx.components.TimerComponent;
import com.mygdx.game.Game;
import com.mygdx.systems.AnimationSystem;
import com.mygdx.systems.EntityRendererSystem;
import com.mygdx.systems.IdleSystem;
import com.mygdx.systems.MovementSystem;
import com.mygdx.systems.PlayerSystem;
import com.mygdx.systems.TimerSystem;
import com.mygdx.utils.Assets;
import com.mygdx.utils.Constants;
import com.mygdx.utils.InputHandler;
import com.mygdx.utils.Mappers;
import com.mygdx.utils.PowerUpAction;
import com.mygdx.utils.ScreenTransitionSlide;
import com.mygdx.utils.TimerAction;
import com.mygdx.utils.UserData;

public abstract class GameWorld implements EntityListener, ContactListener, Disposable{

    protected Game game;
    // maze blocks

    protected TextureRegion[] blocks;

    // Idle texture regions

    protected TextureRegion frontIdle;
    protected TextureRegion backIdle;
    protected TextureRegion rightIdle;
    protected TextureRegion leftIdle;

    protected Engine engine;

    // Systems

    protected EntityRendererSystem entityRendererSystem;
    protected AnimationSystem animationSystem;
    protected MovementSystem movementSystem;
    protected PlayerSystem playerSystem;
    protected IdleSystem idleSystem;
    protected TimerSystem timerSystem;

    protected World world;

    protected Box2DDebugRenderer box2DDebugRenderer;

    protected int currentState;
    protected float currentTime;
    protected int seconds;

    protected InputMultiplexer inputMultiplexer;

    protected boolean paused = false;

    protected TiledMap map;

    protected OrthographicCamera camera;

    public GameWorld(Game game, OrthographicCamera camera){
        this.game = game;
        this.camera = camera;

        inputMultiplexer = new InputMultiplexer();

        world = new World(new Vector2(), true);

        box2DDebugRenderer = new Box2DDebugRenderer();

        blocks = new TextureRegion[3];

        blocks[Constants.EMPTY_BLOCK] = Assets.getInstance().bomberman_atlas.findRegion("BackgroundTile");
        blocks[Constants.SOLID_BLOCK] = Assets.getInstance().bomberman_atlas.findRegion("SolidBlock");
        blocks[Constants.DESTRUCTIBLE_BLOCK] = Assets.getInstance().bomberman_atlas.findRegion("ExplodableBlock");

        engine = new Engine();

        entityRendererSystem = new EntityRendererSystem(game.batch);
        animationSystem = new AnimationSystem();
        movementSystem = new MovementSystem();
        playerSystem = new PlayerSystem(this);
        idleSystem = new IdleSystem();
        timerSystem = new TimerSystem();

        engine.addSystem(playerSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(animationSystem);
        engine.addSystem(idleSystem);
        engine.addSystem(entityRendererSystem);
        engine.addSystem(timerSystem);

        world.setContactListener(this);

        currentState = Constants.START_STATE;
        currentTime = 0;
        seconds = 0;

        movementSystem.setProcessing(false);
        playerSystem.setProcessing(false);

        engine.addEntityListener(this);

    }

    public void render(float dt){

        mapRenderer.render();

        engine.update(dt);

        if(currentState == Constants.START_STATE){

            if(!paused) currentTime += dt;

            if(currentTime > 1f){
                currentTime -= 1f;
                seconds ++;
                if(seconds == 1){
                    Assets.getInstance().number_3.play();
                }else if(seconds == 2){
                    Assets.getInstance().number_2.play();
                }else if(seconds == 3){
                    Assets.getInstance().number_1.play();
                }else if(seconds == 4){
                    Assets.getInstance().go.play();
                    currentState = Constants.PLAY_STATE;
                    movementSystem.setProcessing(true);
                    playerSystem.setProcessing(true);
                    seconds = 0;
                }
            }
        }else if(currentState == Constants.LOSE_STATE){

            currentTime += dt;

            if(currentTime > 1f){
                currentTime -= 1f;
                seconds ++;

                if(seconds == 2){
                    Assets.getInstance().game_over.play();
                }else if(seconds == 4){

                    ScreenTransitionSlide slideTransition =  ScreenTransitionSlide.init(1.5f,
                            ScreenTransitionSlide.DOWN, false, Interpolation.bounceOut);

                    game.setScreen(new MainMenuScreen(game), slideTransition);

                }
            }

        }else if(currentState == Constants.WIN_STATE){

            currentTime += dt;

            if(currentTime > 1f){
                currentTime -= 1f;
                seconds ++;
                if(seconds == 2){
                    Assets.getInstance().congratulations.play();
                }else if(seconds == 4){
                    ScreenTransitionSlide slideTransition =  ScreenTransitionSlide.init(1.5f,
                            ScreenTransitionSlide.DOWN, false, Interpolation.bounceOut);

                    game.setScreen(new MainMenuScreen(game), slideTransition);
                }

            }

        }

        //box2DDebugRenderer.render(world, camera.combined);

        world.step(1/60f, 6, 2);

        sweepDeadBodies();

        set_bombs_collidable();
    }

    private void set_bombs_collidable (){
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);

        for(Body b: bodies){

            UserData data = (UserData)b.getUserData();

            if(data != null && data.ready_to_collide){
                b.getFixtureList().get(0).setSensor(false);
            }
        }
    }

    private TiledMapRenderer mapRenderer;

    public void createMap(String path_to_map){

        map = new TmxMapLoader().load(path_to_map);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / Constants.PPM, game.batch);
        mapRenderer.setView(camera);

        // create solid blocks collider

        for(MapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle r = ((RectangleMapObject)object).getRectangle();

            float size = r.width;
            Vector2 v = world_to_maze_coords(
                    (r.x + size / 2) / Constants.PPM,
                    (r.y + size / 2) / Constants.PPM);

            Entity solid = new Entity();
            CellComponent cellComponent = new CellComponent(
                    this,
                    v.y,
                    v.x,
                    size / Constants.PPM,
                    size / Constants.PPM,
                    Constants.SOLID_BLOCK);

            solid.add(cellComponent);
            engine.addEntity(solid);

        }

        // create breakable blocks

        for(MapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle r = ((RectangleMapObject)object).getRectangle();

            float size = r.width;
            Vector2 v = world_to_maze_coords(
                    (r.x + size / 2) / Constants.PPM,
                    (r.y + size / 2) / Constants.PPM);

            Entity solid = new Entity();

            CellComponent cellComponent = new CellComponent(
                    this,
                    v.y,
                    v.x,
                    size / Constants.PPM,
                    size / Constants.PPM,
                    Constants.DESTRUCTIBLE_BLOCK);

            solid.add(cellComponent);
            engine.addEntity(solid);

        }

        for(MapObject object: map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Rectangle r = ((RectangleMapObject)object).getRectangle();

            float size = r.width;
            Vector2 v = world_to_maze_coords(
                    (r.x + size / 2) / Constants.PPM,
                    (r.y + size / 2) / Constants.PPM);

            Entity solid = new Entity();

            CellComponent cellComponent = new CellComponent(
                    this,
                    v.y,
                    v.x,
                    size / Constants.PPM,
                    size / Constants.PPM,
                    Constants.EMPTY_BLOCK);

            solid.add(cellComponent);
            engine.addEntity(solid);

        }

        // create outside walls

        for(MapObject object: map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject)object).getRectangle();

            float x = rectangle.x / Constants.PPM;
            float y = rectangle.y / Constants.PPM;
            float w = rectangle.width / Constants.PPM;
            float h = rectangle.height / Constants.PPM;

            createWallBody(x + w / 2, y + h / 2, w, h);

        }
    }

    private Entity player;

    protected Entity createPlayer(int maze_x, int maze_y){
        player = new Entity();

        IntMap<Animation> animations = new IntMap<Animation>();

        Animation<TextureRegion> frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                Assets.getInstance().bomberman_atlas.findRegions("bomberWhiteFront"), Animation.PlayMode.LOOP);

        frontIdle = frames.getKeyFrame(0);

        animations.put(Constants.DOWN, frames);

        frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                Assets.getInstance().bomberman_atlas.findRegions("bomberWhiteBack"), Animation.PlayMode.LOOP);

        backIdle = frames.getKeyFrame(0);

        animations.put(Constants.UP, frames);

        frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                Assets.getInstance().bomberman_atlas.findRegions("bomberWhiteSide"), Animation.PlayMode.LOOP);

        rightIdle = frames.getKeyFrame(0);

        animations.put(Constants.RIGHT, frames);

        frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                Assets.getInstance().bomberman_atlas.findRegions("bomberWhiteSide"), Animation.PlayMode.LOOP);

        leftIdle = frames.getKeyFrame(0);

        for(TextureRegion t: frames.getKeyFrames()){
            t.flip(true, false);
        }

        animations.put(Constants.LEFT, frames);

        TextureRegion[] idleRegions = new TextureRegion[4];

        idleRegions[Constants.UP] = backIdle;
        idleRegions[Constants.DOWN] = frontIdle;
        idleRegions[Constants.LEFT] = leftIdle;
        idleRegions[Constants.RIGHT] = rightIdle;

        Vector2 position = maze_to_world_coords(maze_x, maze_y);

        Body body = createBody(position.x, position.y, 45 / Constants.PPM, true, false, (short) Constants.PLAYER);
        body.setUserData(new UserData(Constants.PLAYER));

        PhysicComponent physicComponent = new PhysicComponent(body, 1);

        player.add(physicComponent);
        player.add(new SpeedComponent(Constants.MIN_SPEED));
        player.add(new IdleComponent(idleRegions));
        player.add(new StateComponent(Constants.RIGHT));

        player.add(new AnimationComponent(animations));
        player.add(new RegionComponent());
        return player;
    }

    private Vector2 world_to_maze_coords(float world_x, float world_y){
        float div = Constants.CELL_SIZE / Constants.PPM;

        return new Vector2(
                (int)(world_x/div),
                (int)(world_y/div)
        );
    }

    public Vector2 world_to_maze_coords(Vector2 world_pos){
        float div = Constants.CELL_SIZE / Constants.PPM;
        return new Vector2(
                (int)(world_pos.x / div),
                (int)(world_pos.y / div)
        );
    }

    public Vector2 maze_to_world_coords(float maze_x, float maze_y){

        return new Vector2(maze_x * Constants.CELL_SIZE / Constants.PPM + Constants.CELL_SIZE / 2 / Constants.PPM,
                maze_y * Constants.CELL_SIZE / Constants.PPM + Constants.CELL_SIZE / 2 / Constants.PPM);

    }

    public void createBomb(float x, float y, int power, PlayerComponent playerComponent){

        // convert from world coords to maze coords

        Vector2 maze_coords = world_to_maze_coords(x,y);

        Family cellsFamily = Family.all(CellComponent.class).get();

        ImmutableArray<Entity> cells = engine.getEntitiesFor(cellsFamily);

        CellComponent cell = null;

        for(int i = 0; i < cells.size(); i++){
            CellComponent cellComponent = Mappers.cell.get(cells.get(i));

            if(cellComponent.getRow() == maze_coords.y && cellComponent.getCol() == maze_coords.x){
                cell = cellComponent;
                break;
            }
        }

        if(cell == null) {
            System.out.println("ERROR ! trying to set up a bomb outside the walls");
            return;
        }

        final Entity bomb = new Entity();

        if(cell.getBomb() != null)
            return;
        else
            cell.setBomb(bomb);

        IntMap<Animation> animations = new IntMap<Animation>();
        Animation<TextureRegion> frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                Assets.getInstance().bomberman_atlas.findRegions("bomb"), Animation.PlayMode.LOOP);
        animations.put(Constants.DEFAULT_STATE, frames);

        TimerAction TimerAction = new TimerAction() {
            @Override
            public void action() {
                explodeBomb(bomb);
            }
        };

        Vector2 position = maze_to_world_coords(maze_coords.x, maze_coords.y);

        Body body = createBody(position.x, position.y,
                Constants.BOMB_SIZE / Constants.PPM, false, true, (short)Constants.PLAYER);

        body.setUserData(new UserData(Constants.BOMB));

        bomb.add(new TimerComponent(Constants.BOMB_EXPLODE_TIME, TimerAction));
        bomb.add(new BombComponent(playerComponent, power));
        bomb.add(new StateComponent(Constants.DEFAULT_STATE));
        bomb.add(new AnimationComponent(animations));
        bomb.add(new RegionComponent());
        bomb.add(new PhysicComponent(body, 0));

        engine.addEntity(bomb);

        if(playerComponent != null)
            playerComponent.setBombs(playerComponent.getBombs() - 1);
    }

    private void explodeBomb(Entity bomb){

        PhysicComponent physicComponent = Mappers.physic.get(bomb);
        BombComponent bombComponent = Mappers.bomb.get(bomb);
        PlayerComponent playerComponent = bombComponent.getPlayerComponent();

        Vector2 maze_coords = world_to_maze_coords(physicComponent.getBody().getPosition());

        Family cellsFamily = Family.all(CellComponent.class).get();

        ImmutableArray<Entity> cells = engine.getEntitiesFor(cellsFamily);

        CellComponent[] horizontal = new CellComponent[Constants.MAZE_WIDTH];
        CellComponent[] vertical = new CellComponent[Constants.MAZE_HEIGHT];

        for(int i = 0; i < cells.size(); i++){

            CellComponent cellComponent = Mappers.cell.get(cells.get(i));

            if(cellComponent.getRow() == maze_coords.y ){
                horizontal[(int)cellComponent.getCol()] = cellComponent;
            }

            if(cellComponent.getCol() == maze_coords.x){
                vertical[(int)cellComponent.getRow()] = cellComponent;
            }
        }

        if(playerComponent != null)
            playerComponent.setBombs(playerComponent.getBombs() + 1);

        engine.removeEntity(bomb);

        vertical[(int)maze_coords.y].setBomb(null);

        int counter = 0;

        // start checking cells to right

        for(int i = (int)maze_coords.x; i < Constants.MAZE_WIDTH - 1; i++){

            counter ++;

            CellComponent c = horizontal[i];

            if(c.getType() == Constants.SOLID_BLOCK){
                break;
            }

            if(c.getBomb() != null && c.getBomb() != bomb){
                explodeBomb(c.getBomb());
                break;
            }

            // create explosion

            int type;

            if(i == (int)maze_coords.x) {
                type = Constants.EXPLOSION_ORIGIN;
            }else if(c.getType() == Constants.DESTRUCTIBLE_BLOCK || counter > bombComponent.getBombPower()){
                type = Constants.EXPLOSION_CORNER;
            }else{
                type = Constants.EXPLOSION_MIDDLE;
            }

            createExplosion((int)c.getCol(), (int)c.getRow(), c, type, Constants.RIGHT);

            if(c.getType() == Constants.DESTRUCTIBLE_BLOCK){
                break;
            }

            if(counter > bombComponent.getBombPower()){
                break;
            }
        }

        counter = 0;

        // start checking cells to left

        for(int i = (int)maze_coords.x ; i > 0; i--){

            CellComponent c = horizontal[i];

            counter ++;

            if(c.getType() == Constants.SOLID_BLOCK){
                break;
            }

            if(c.getBomb() != null && c.getBomb() != bomb){
                explodeBomb(c.getBomb());
                break;
            }

            // create explosion

            int type;

            if(i == (int)maze_coords.x) {
                type = Constants.EXPLOSION_ORIGIN;
            }else if(c.getType() == Constants.DESTRUCTIBLE_BLOCK || counter > bombComponent.getBombPower()){
                type = Constants.EXPLOSION_CORNER;
            }else{
                type = Constants.EXPLOSION_MIDDLE;
            }

            createExplosion((int)c.getCol(), (int)c.getRow(), c, type, Constants.LEFT);

            if(c.getType() == Constants.DESTRUCTIBLE_BLOCK){
                break;
            }

            if(counter > bombComponent.getBombPower()){
                break;
            }

        }

        counter = 0;

        // start checking up

        for(int i = (int)maze_coords.y ; i < Constants.MAZE_HEIGHT - 1; i++){

            CellComponent c = vertical[i];

            counter++;

            if(c.getType() == Constants.SOLID_BLOCK){
                break;
            }

            if(c.getBomb() != null && c.getBomb() != bomb){
                explodeBomb(c.getBomb());
                break;
            }

            // create explosion

            int type;

            if(i == (int)maze_coords.y) {
                type = Constants.EXPLOSION_ORIGIN;
            }else if(c.getType() == Constants.DESTRUCTIBLE_BLOCK || counter > bombComponent.getBombPower()){
                type = Constants.EXPLOSION_CORNER;
            }else{
                type = Constants.EXPLOSION_MIDDLE;
            }

            createExplosion((int)c.getCol(), (int)c.getRow(), c, type, Constants.UP);

            if(c.getType() == Constants.DESTRUCTIBLE_BLOCK){
                break;
            }

            if(counter > bombComponent.getBombPower()){
                break;
            }

        }

        counter = 0;

        // start checking down

        for(int i = (int)maze_coords.y; i > 0; i--){

            CellComponent c = vertical[i];

            counter++;

            if(c.getType() == Constants.SOLID_BLOCK){
                break;
            }

            if(c.getBomb() != null && c.getBomb() != bomb){
                explodeBomb(c.getBomb());
                break;
            }

            // create explosion

            int type;

            if(i == (int)maze_coords.y) {
                type = Constants.EXPLOSION_ORIGIN;
            }else if(c.getType() == Constants.DESTRUCTIBLE_BLOCK || counter > bombComponent.getBombPower()){
                type = Constants.EXPLOSION_CORNER;
            }else{
                type = Constants.EXPLOSION_MIDDLE;
            }

            createExplosion((int)c.getCol(), (int)c.getRow(), c, type, Constants.DOWN);

            if(c.getType() == Constants.DESTRUCTIBLE_BLOCK){
                break;
            }

            if(counter > bombComponent.getBombPower()){
                break;
            }

        }
    }

    private void createExplosion(int maze_x, int maze_y,
                                 final CellComponent cell, int type, float dir){

        if(cell.getExplosion() != null){
            return;
        }

        // destroy power up in case the cell contains one of them

        if(cell.getPower_up() != null){
            engine.removeEntity(cell.getPower_up());
            cell.setPower_up(null);
        }

        final Entity explosion = new Entity();

        IntMap<Animation> animations = new IntMap<Animation>();

        Animation<TextureRegion> frames = null;

        if(type == Constants.EXPLOSION_MIDDLE) {

            if(dir == Constants.RIGHT || dir == Constants.LEFT){
                frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                        Assets.getInstance().bomberman_atlas.findRegions("middleHori"), Animation.PlayMode.NORMAL);
            }else{
                frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                        Assets.getInstance().bomberman_atlas.findRegions("middleVer"), Animation.PlayMode.NORMAL);
            }


            if(dir == Constants.LEFT ){
                for(TextureRegion t: frames.getKeyFrames())
                    t.flip(true, false);
            }

            if(dir == Constants.UP){
                for(TextureRegion t: frames.getKeyFrames())
                    t.flip(false, true);
            }

        }
        else if(type == Constants.EXPLOSION_ORIGIN) {

            if(dir == Constants.RIGHT || dir == Constants.LEFT){
                frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                        Assets.getInstance().bomberman_atlas.findRegions("originHori"), Animation.PlayMode.NORMAL);
            }else{
                frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                        Assets.getInstance().bomberman_atlas.findRegions("originVer"), Animation.PlayMode.NORMAL);
            }

            if(dir == Constants.LEFT ){
                for(TextureRegion t: frames.getKeyFrames())
                    t.flip(true, false);
            }

            if(dir == Constants.UP){
                for(TextureRegion t: frames.getKeyFrames())
                    t.flip(false, true);
            }

        }else if(type == Constants.EXPLOSION_CORNER) {

            if(dir == Constants.RIGHT || dir == Constants.LEFT){
                frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                        Assets.getInstance().bomberman_atlas.findRegions("cornerHori"), Animation.PlayMode.NORMAL);
            }else{
                frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                        Assets.getInstance().bomberman_atlas.findRegions("cornerVer"), Animation.PlayMode.NORMAL);
            }

            if(dir == Constants.LEFT ){
                for(TextureRegion t: frames.getKeyFrames())
                    t.flip(true, false);
            }

            if(dir == Constants.UP){
                for(TextureRegion t: frames.getKeyFrames()) {
                    t.flip(false, true);
                }
            }

        }

        animations.put(Constants.DEFAULT_STATE, frames);

        TimerAction TimerAction = new TimerAction() {
            @Override
            public void action() {
                explosion_action(cell, explosion);
            }
        };

        Vector2 position = maze_to_world_coords(maze_x, maze_y);

        Body body = createBody(
                position.x,
                position.y,
                48 / Constants.PPM,
                false,
                true,
                (short)0
        );

        body.setUserData(new UserData(Constants.EXPLOSION));

        explosion.add(new TimerComponent(Constants.EXPLOSION_TIME, TimerAction));
        explosion.add(new AnimationComponent(animations));
        explosion.add(new StateComponent(Constants.DEFAULT_STATE));
        explosion.add(new RegionComponent());
        explosion.add(new PhysicComponent(body, 0));

        engine.addEntity(explosion);
        cell.setExplosion(explosion);
    }

    private void updateClientSpeed(float speed){

        if(isMultiplayerGame()){
            ((MultiplayerGame)this).update_client_speed(speed);
        }
    }

    private void explosion_action(CellComponent cell, Entity explosion){

        cell.setExplosion(null);
        engine.removeEntity(explosion);

        if(cell.getType() == Constants.DESTRUCTIBLE_BLOCK){
            cell.setType(Constants.EMPTY_BLOCK);
            world.destroyBody(cell.getBody());
            cell.setBody(null);

            TiledMapTileLayer breakable_layer = (TiledMapTileLayer) map.getLayers().get(1);

            breakable_layer.getCell((int)cell.getCol(), (int)cell.getRow()).setTile(null);

            // generate power up

            if(Math.random() < Constants.POWER_UP_PROB){

                int type = MathUtils.random(2);

                if(this instanceof MultiplayerGame){
                    ((MultiplayerGame)this).emit_power_up(type, cell);
                    return;
                }

                createPowerUp(type, cell);
            }

        }
    }

    protected void createPowerUp(int type, CellComponent cell){

        Entity powerUp = new Entity();

        TextureRegion region = null;
        PowerUpAction action = null;

        Vector2 position = maze_to_world_coords(cell.getCol(), cell.getRow());

        Body body = createBody(
                position.x,
                position.y,
                32 / Constants.PPM,
                false,
                true,
                (short)0
        );

        body.setUserData(new UserData(Constants.POWER_UP));

        switch (type){
            case Constants.BOMB_POWER_UP:
                region = Assets.getInstance().bomberman_atlas.findRegion("BombPowerup");
                action = new PowerUpAction() {
                    @Override
                    public void action(Entity player) {

                        if(!hasComponent(player, PlayerComponent.class)){
                            System.out.println("I don't have a player comp");
                            return;
                        }

                        Assets.getInstance().power_up.play();
                        PlayerComponent playerComponent = Mappers.player.get(player);

                        if(playerComponent.getMax_bombs() + 1 > Constants.MAX_BOMBS)
                            return;

                        playerComponent.setMax_bombs(playerComponent.getMax_bombs() + 1);
                        playerComponent.setBombs(playerComponent.getBombs() + 1);


                    }
                };
                break;
            case Constants.SPEED_POWER_UP:
                region = Assets.getInstance().bomberman_atlas.findRegion("SpeedPowerup");
                action = new PowerUpAction() {
                    @Override
                    public void action(Entity player) {

                        if(!hasComponent(player, PlayerComponent.class)){
                            System.out.println("I don't have a speed comp");
                            return;
                        }

                        Assets.getInstance().power_up.play();

                        PlayerComponent playerComponent = Mappers.player.get(player);
                        SpeedComponent speedComponent = Mappers.speed.get(player);

                        if(playerComponent.getSpeed() + 1 > Constants.MAX_SPEED)
                            return;

                        playerComponent.setSpeed(playerComponent.getSpeed() + 1f);
                        speedComponent.setSpeed(speedComponent.getSpeed() + 1f);

                        updateClientSpeed(playerComponent.getSpeed());

                    }
                };
                break;
            case Constants.FLAME_POWER_UP:
                region = Assets.getInstance().bomberman_atlas.findRegion("FlamePowerup");
                action = new PowerUpAction() {
                    @Override
                    public void action(Entity player) {

                        if(!hasComponent(player, PlayerComponent.class)){
                            System.out.println("I don't have a player_fl comp");
                            return;
                        }

                        PlayerComponent playerComponent = Mappers.player.get(player);
                        Assets.getInstance().power_up.play();

                        if(playerComponent.getBombPower() + 1 > Constants.MAX_BOMB_POWER)
                            return;

                        playerComponent.setBombPower(playerComponent.getBombPower() + 1);

                    }
                };
                break;
        }

        powerUp.add(new RegionComponent(region));
        powerUp.add(new PhysicComponent(body, 0));
        powerUp.add(new PowerUpComponent(action));

        engine.addEntity(powerUp);
        cell.setPower_up(powerUp);
    }

    public Body createWallBody(float x, float y, float width, float height){

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);
        body.createFixture(shape, 0f);
        shape.dispose();
        return body;
    }

    protected Body createBody(float x, float y, float radius, boolean dynamic, boolean isSensor,
                            short filterIndex){

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = dynamic ? BodyDef.BodyType.DynamicBody: BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.isSensor = isSensor;

        /*PolygonShape shape = new PolygonShape();
        shape.setAsBox(radius / 2, radius / 2);*/

        CircleShape circle = new CircleShape();
        circle.setRadius(radius / 2);


        fixtureDef.shape = circle;

        fixtureDef.filter.groupIndex = filterIndex;

        body.createFixture(fixtureDef);

        //shape.dispose();
        circle.dispose();
        return body;
    }

    private void sweepDeadBodies() {

        Array<Body> bodies = new Array<Body>();

        world.getBodies(bodies);

        for(Body b: bodies){

            if(b != null){
                UserData data = (UserData) b.getUserData();
                if(data != null && data.readyToBeDestroyed) {
                    if(!world.isLocked())
                        world.destroyBody(b);
                    b.setUserData(null);
                    b = null;
                }
            }
        }
    }

    protected void player_lose(Entity player){
        engine.removeEntity(player);
        currentState = Constants.LOSE_STATE;
        Assets.getInstance().you_lose.play();

        // if this is a multiplayer game then let other clients know that this player is dead

        if(this instanceof MultiplayerGame){
            ((MultiplayerGame)this).emit_player_dead();
        }

    }

    protected void player_win(Entity player){
        currentState = Constants.WIN_STATE;
        Assets.getInstance().you_win.play();

    }

    @Override
    public void entityAdded(Entity entity) {}

    @Override
    public void entityRemoved(Entity entity) {

        PhysicComponent physicComponent = Mappers.physic.get(entity);
        ((UserData)physicComponent.getBody().getUserData()).readyToBeDestroyed = true;

    }

    @Override
    public void beginContact(Contact contact) {

        UserData dataA = (UserData) contact.getFixtureA().getBody().getUserData();
        UserData dataB = (UserData) contact.getFixtureB().getBody().getUserData();

        if(dataA == null || dataB == null)
            return;

        // Collision between player & explosion ============================================

        player_explosion_collision(dataA, dataB, contact);

        // Collision between player & power_up =============================================

        player_power_up_collision(dataA, dataB, contact);

    }

    protected void player_explosion_collision(UserData dataA, UserData dataB, Contact contact){

        Family playerFamily;
        ImmutableArray<Entity> players;

        if(dataA.type == Constants.PLAYER && dataB.type == Constants.EXPLOSION ||
                dataB.type == Constants.PLAYER && dataA.type == Constants.EXPLOSION){

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

    protected void player_power_up_collision(UserData dataA, UserData dataB, Contact contact){

        Family playerFamily;
        ImmutableArray<Entity> players;

        Family powerUpFamily;
        ImmutableArray<Entity> powerups;

        Family cellsFamily;
        ImmutableArray<Entity> cells;

        CellComponent cell = null;

        if(dataA.type == Constants.POWER_UP && dataB.type == Constants.PLAYER ||
                dataB.type == Constants.POWER_UP && dataA.type == Constants.PLAYER){

            // get the player that picks the power up

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

            // get the power up

            powerUpFamily = Family.all(PowerUpComponent.class).get();
            powerups = engine.getEntitiesFor(powerUpFamily);

            cellsFamily = Family.all(CellComponent.class).get();
            cells = engine.getEntitiesFor(cellsFamily);

            for(int i = 0; i < powerups.size(); i++){

                Entity e = powerups.get(i);

                PhysicComponent physicComponent = Mappers.physic.get(e);

                if(physicComponent.getBody().equals(contact.getFixtureA().getBody()) ||
                        physicComponent.getBody().equals(contact.getFixtureB().getBody())){

                    if(player != null)
                        Mappers.power_up.get(e).getAction().action(player);

                    // find out which cell contains this power up

                    for(int c = 0; c < cells.size(); c++){

                        Entity ent = cells.get(c);

                        CellComponent cc = Mappers.cell.get(ent);
                        if(cc.getPower_up() != null && cc.getPower_up().equals(e)){

                            // cell found
                            cell = cc;
                            cell.setPower_up(null); // remove power up reference
                            engine.removeEntity(e);
                            break;
                        }
                    }
                    break;
                }
            }

            // if this is a multiplayer game then remove power up from all clients

            if(this instanceof MultiplayerGame && cell != null){

                ((MultiplayerGame)this).power_up_taken(cell.getCol(), cell.getRow());
            }


        }
    }

    @Override
    public void endContact(Contact contact) {

        UserData dataA = (UserData) contact.getFixtureA().getBody().getUserData();
        UserData dataB = (UserData) contact.getFixtureB().getBody().getUserData();

        if(dataA != null && dataA.type == Constants.BOMB){
            dataA.ready_to_collide = true;
        }

        if(dataB != null && dataB.type == Constants.BOMB){
            dataB.ready_to_collide = true;
        }
    }

    private boolean isMultiplayerGame(){
        return this instanceof MultiplayerGame;
    }

    private boolean hasComponent(Entity e, Class<?> c){

        for(Component comp: e.getComponents()){
            if(c == comp.getClass()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public World getWorld() {
        return world;
    }

    public int getCurrentState(){return currentState;}

    public InputMultiplexer getInputMultiplexer(){return inputMultiplexer;}

    @Override
    public void dispose() {
        map.dispose();
    }
}
