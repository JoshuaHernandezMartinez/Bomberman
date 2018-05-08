package com.mygdx.screens;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.components.AnimationComponent;
import com.mygdx.components.BombComponent;
import com.mygdx.components.CellComponent;
import com.mygdx.components.CreepComponent;
import com.mygdx.components.IdleComponent;
import com.mygdx.components.InputComponent;
import com.mygdx.components.PhysicComponent;
import com.mygdx.components.PlayerComponent;
import com.mygdx.components.PowerUpComponent;
import com.mygdx.components.RegionComponent;
import com.mygdx.components.SpeedComponent;
import com.mygdx.components.StateComponent;
import com.mygdx.components.TimerComponent;
import com.mygdx.game.Game;
import com.mygdx.systems.AnimationSystem;
import com.mygdx.systems.CreepSystem;
import com.mygdx.systems.IdleSystem;
import com.mygdx.systems.PlayerSystem;
import com.mygdx.systems.MazeRendererSystem;
import com.mygdx.systems.MovementSystem;
import com.mygdx.systems.EntityRendererSystem;
import com.mygdx.systems.TimerSystem;
import com.mygdx.utils.Assets;
import com.mygdx.utils.Constants;
import com.mygdx.utils.InputHandler;
import com.mygdx.utils.Mappers;
import com.mygdx.utils.PowerUpAction;
import com.mygdx.utils.TimerAction;
import com.mygdx.utils.UserData;

public class PlayScreen extends DefaultScreen implements EntityListener, ContactListener{

    private OrthographicCamera camera;
    private ExtendViewport viewport;

    private TextureRegion[] blocks;

    // Idle texture regions

    private TextureRegion frontIdle;
    private TextureRegion backIdle;
    private TextureRegion rightIdle;
    private TextureRegion leftIdle;

    private Engine engine;

    // Systems

    private EntityRendererSystem entityRendererSystem;
    private AnimationSystem animationSystem;
    private MovementSystem movementSystem;
    private PlayerSystem playerSystem;
    private IdleSystem idleSystem;
    private MazeRendererSystem mazeRendererSystem;
    private TimerSystem timerSystem;
    private CreepSystem creepSystem;

    private World world;

    private Box2DDebugRenderer box2DDebugRenderer;

    private int creepCount;

    private boolean portal;

    private int currentState;
    private float currentTime;
    private int seconds;

    private InputMultiplexer inputMultiplexer;

    private int game_mode;

    public PlayScreen(Game game){
        super(game);
    }

    @Override
    public void show() {

        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Constants.VIEWPORT_WIDTH / Constants.PPM,
                Constants.VIEWPORT_HEIGHT / Constants.PPM, camera);

        camera.position.set(Constants.CELL_SIZE * Constants.MAZE_WIDTH/2 / Constants.PPM,
                Constants.CELL_SIZE * Constants.MAZE_HEIGHT/2 / Constants.PPM, 0);
        camera.update();

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
        playerSystem = new PlayerSystem();
        idleSystem = new IdleSystem();
        mazeRendererSystem = new MazeRendererSystem(game.batch);
        timerSystem = new TimerSystem();
        creepSystem = new CreepSystem(this);

        portal = false;
        creepCount = 0;

        // randomly generate the maze & the enemies

        for(int row = 0; row < Constants.MAZE_HEIGHT ; row ++){
            for(int col = 0; col < Constants.MAZE_WIDTH ; col++){

                Entity wall = new Entity();
                CellComponent cellComponent;


                if(row % 2 != 0 && row > 0 && row < Constants.MAZE_HEIGHT - 1 &&
                        col % 2 != 0 && col > 0 && col < Constants.MAZE_WIDTH - 1){
                    cellComponent = new CellComponent(this, blocks, row, col, Constants.SOLID_BLOCK);
                    wall.add(cellComponent);
                    engine.addEntity(wall);
                    continue;
                }

                // randomly set the cell to empty or destructible block
                // without taking into account the players starting positions

                if(     row == 0 && col == 0 ||
                        row == 0 && col == 1 ||
                        row == 1 && col == 0 ||
                        row == Constants.MAZE_HEIGHT - 1 && col == Constants.MAZE_WIDTH - 1 ||
                        row == Constants.MAZE_HEIGHT - 2 && col == Constants.MAZE_WIDTH - 1 ||
                        row == Constants.MAZE_HEIGHT - 1 && col == Constants.MAZE_WIDTH - 2
                        ) {

                    cellComponent = new CellComponent(this, blocks, row, col,
                            Constants.EMPTY_BLOCK);


                }else{

                    cellComponent = new CellComponent(this, blocks, row, col,
                            MathUtils.random() < Constants.TILE_PROB ? Constants.DESTRUCTIBLE_BLOCK: Constants.EMPTY_BLOCK);
                }

                wall.add(cellComponent);
                engine.addEntity(wall);

            }
        }

        // set outside walls

        createOutsideWalls();

        engine.addSystem(playerSystem);
        engine.addSystem(creepSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(animationSystem);
        engine.addSystem(idleSystem);
        engine.addSystem(mazeRendererSystem);
        engine.addSystem(entityRendererSystem);
        engine.addSystem(timerSystem);

        world.setContactListener(this);

        currentState = Constants.START_STATE;
        currentTime = 0;
        seconds = 0;

        movementSystem.setProcessing(false);
        playerSystem.setProcessing(false);

    }

    public void startGame(int game_mode){

        this.game_mode = game_mode;

        int[] player_1_keys = new int[5];

        player_1_keys[Constants.LEFT] =
                game.preferences.getInteger(Constants.player_1_keys[Constants.LEFT], Input.Keys.A);
        player_1_keys[Constants.RIGHT] =
                game.preferences.getInteger(Constants.player_1_keys[Constants.RIGHT], Input.Keys.D);
        player_1_keys[Constants.UP] =
                game.preferences.getInteger(Constants.player_1_keys[Constants.UP], Input.Keys.W);
        player_1_keys[Constants.DOWN] =
                game.preferences.getInteger(Constants.player_1_keys[Constants.DOWN], Input.Keys.S);
        player_1_keys[Constants.BOMB] =
                game.preferences.getInteger(Constants.player_1_keys[Constants.BOMB], Input.Keys.B);

        createPlayer(0,0, player_1_keys);


        if(game_mode == Constants.ONE_PLAYER_MODE){

            Family cellFamily = Family.all(CellComponent.class).get();

            ImmutableArray<Entity> cells= engine.getEntitiesFor(cellFamily);

            for(int i = 0; i < cells.size(); i++){
                Entity e = cells.get(i);

                CellComponent cellComponent = Mappers.cell.get(e);

                int col = cellComponent.getCol();
                int row = cellComponent.getRow();

                if(cellComponent.getType() == Constants.EMPTY_BLOCK &&
                        row != 0 && col != 0 ||
                        row == 0 && col == 1 ||
                        row == 1 && col == 0){

                    if(MathUtils.random() < Constants.ENEMY_PROB)
                        createCreep(cellComponent.getCol(), cellComponent.getRow());

                }

            }

        }else if(game_mode == Constants.TWO_PLAYER_MODE){

            int[] player_2_keys = new int[5];

            player_2_keys[Constants.LEFT] =
                    game.preferences.getInteger(Constants.player_2_keys[Constants.LEFT], Input.Keys.LEFT);
            player_2_keys[Constants.RIGHT] =
                    game.preferences.getInteger(Constants.player_2_keys[Constants.RIGHT], Input.Keys.RIGHT);
            player_2_keys[Constants.UP] =
                    game.preferences.getInteger(Constants.player_2_keys[Constants.UP], Input.Keys.UP);
            player_2_keys[Constants.DOWN] =
                    game.preferences.getInteger(Constants.player_2_keys[Constants.DOWN], Input.Keys.DOWN);
            player_2_keys[Constants.BOMB] =
                    game.preferences.getInteger(Constants.player_2_keys[Constants.BOMB], Input.Keys.P);

            createPlayer(Constants.MAZE_WIDTH - 1, Constants.MAZE_HEIGHT - 1, player_2_keys);

        }


    }


    private void createPlayer(int maze_x, int maze_y, int[] keys){
        Entity player = new Entity();

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

        frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                Assets.getInstance().blood_animation.getRegions(), Animation.PlayMode.LOOP);

        animations.put(Constants.DEAD, frames);

        TextureRegion[] idleRegions = new TextureRegion[4];

        idleRegions[Constants.UP] = backIdle;
        idleRegions[Constants.DOWN] = frontIdle;
        idleRegions[Constants.LEFT] = leftIdle;
        idleRegions[Constants.RIGHT] = rightIdle;

        // add components to player

        Vector2 position = maze_to_world_coords(maze_x,maze_y);

        Body body = createBody(position.x,position.y, 45, true, false, (short) Constants.PLAYER);
        body.setUserData(new UserData(Constants.PLAYER));

        PlayerComponent playerComponent = new PlayerComponent(keys);
        PhysicComponent physicComponent = new PhysicComponent(body, 1);
        InputHandler inputHandler = new InputHandler(this, playerComponent, physicComponent);

        inputMultiplexer.addProcessor(inputHandler);

        player.add(playerComponent);
        player.add(physicComponent);
        player.add(new SpeedComponent(Constants.MIN_SPEED));
        player.add(new IdleComponent(idleRegions));
        player.add(new StateComponent(Constants.RIGHT));

        player.add(new AnimationComponent(animations));
        player.add(new RegionComponent());

        engine.addEntity(player);

        engine.addEntityListener(this);
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

        frames = new Animation<TextureRegion>(Constants.ANIMATION_TIME,
                Assets.getInstance().blood_animation.getRegions(), Animation.PlayMode.LOOP);

        animations.put(Constants.DEAD, frames);

        Vector2 position = maze_to_world_coords(maze_x,
                maze_y);

        Body body = createBody(position.x, position.y,
                45, true, true, (short)-Constants.CREEP);
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
                45, true, true, (short)0);
        body.setUserData(new UserData(Constants.PORTAL));

        portal.add(new RegionComponent(Assets.getInstance().bomberman_atlas.findRegion("Portal")));
        portal.add(new PhysicComponent(body, 0));

        engine.addEntity(portal);

    }

    private void createOutsideWalls(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(Constants.CELL_SIZE * Constants.MAZE_WIDTH / 2 / Constants.PPM,
                -Constants.CELL_SIZE / Constants.PPM);


        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.MAZE_WIDTH * Constants.CELL_SIZE /2 / Constants.PPM,
                Constants.CELL_SIZE / Constants.PPM);
        body.createFixture(shape, 0f);

        //
        bodyDef.position.set(Constants.CELL_SIZE * Constants.MAZE_WIDTH/2 / Constants.PPM,
                Constants.MAZE_HEIGHT * Constants.CELL_SIZE / Constants.PPM + Constants.CELL_SIZE/2 / Constants.PPM);

        body = world.createBody(bodyDef);

        shape.setAsBox(Constants.CELL_SIZE * Constants.MAZE_WIDTH / 2 / Constants.PPM,
                Constants.CELL_SIZE / 2 / Constants.PPM);
        body.createFixture(shape, 0f);

        //
        bodyDef.position.set(-Constants.CELL_SIZE/2 / Constants.PPM,
                Constants.MAZE_HEIGHT * Constants.CELL_SIZE / 2 / Constants.PPM);

        body = world.createBody(bodyDef);

        shape.setAsBox(Constants.CELL_SIZE / 2 / Constants.PPM,
                Constants.CELL_SIZE * Constants.MAZE_HEIGHT / 2 / Constants.PPM);
        body.createFixture(shape, 0f);

        //
        bodyDef.position.set(Constants.CELL_SIZE * Constants.MAZE_WIDTH / Constants.PPM +
                Constants.CELL_SIZE / 2 / Constants.PPM,
                Constants.MAZE_HEIGHT * Constants.CELL_SIZE / 2 / Constants.PPM);

        body = world.createBody(bodyDef);

        shape.setAsBox(Constants.CELL_SIZE / 2 / Constants.PPM,
                Constants.CELL_SIZE * Constants.MAZE_HEIGHT / 2 / Constants.PPM);
        body.createFixture(shape, 0f);

    }

    private Vector2 world_to_maze_coords(float word_x, float world_y){
        return new Vector2(
                (int)(word_x * Constants.PPM / Constants.CELL_SIZE),
                (int)(world_y * Constants.PPM / Constants.CELL_SIZE)
        );
    }

    public Vector2 world_to_maze_coords(Vector2 world_pos){
        return new Vector2(
                (int)(world_pos.x * Constants.PPM / Constants.CELL_SIZE),
                (int)(world_pos.y * Constants.PPM / Constants.CELL_SIZE)
        );
    }

    public Vector2 maze_to_world_coords(float maze_x, float maze_y){

        return new Vector2(maze_x * Constants.CELL_SIZE + Constants.CELL_SIZE / 2,
                maze_y * Constants.CELL_SIZE + Constants.CELL_SIZE / 2);

    }

    public void createBomb(float x, float y, PlayerComponent playerComponent){

        // convert from world coords to maze coords

        Vector2 maze_coords = world_to_maze_coords(x,y);

        Family cellsFamily = Family.all(CellComponent.class).get();

        ImmutableArray<Entity> cells = engine.getEntitiesFor(cellsFamily);
        ComponentMapper<CellComponent> cellMapper = ComponentMapper.getFor(CellComponent.class);

        CellComponent cell = null;

        for(int i = 0; i < cells.size(); i++){
            CellComponent cellComponent = cellMapper.get(cells.get(i));

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
                Constants.BOMB_SIZE, false, true, (short)-Constants.PLAYER);

        body.setUserData(new UserData(Constants.BOMB));

        bomb.add(new TimerComponent(Constants.BOMB_EXPLODE_TIME, TimerAction));
        bomb.add(new BombComponent(playerComponent));
        bomb.add(new StateComponent(Constants.DEFAULT_STATE));
        bomb.add(new AnimationComponent(animations));
        bomb.add(new RegionComponent());
        bomb.add(new PhysicComponent(body, 0));

        engine.addEntity(bomb);
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
                horizontal[cellComponent.getCol()] = cellComponent;
            }

            if(cellComponent.getCol() == maze_coords.x){
                vertical[cellComponent.getRow()] = cellComponent;
            }
        }

        playerComponent.setBombs(playerComponent.getBombs() + 1);
        engine.removeEntity(bomb);
        vertical[(int)maze_coords.y].setBomb(null);

        int counter = 0;

        // start checking cells to right

        for(int i = (int)maze_coords.x; i < Constants.MAZE_WIDTH; i++){

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
            }else if(c.getType() == Constants.DESTRUCTIBLE_BLOCK || counter > playerComponent.getBombPower()){
                type = Constants.EXPLOSION_CORNER;
            }else{
                type = Constants.EXPLOSION_MIDDLE;
            }

            createExplosion(c.getCol(), c.getRow(), c, type, Constants.RIGHT);

            if(c.getType() == Constants.DESTRUCTIBLE_BLOCK){
                break;
            }

            if(counter > playerComponent.getBombPower()){
                break;
            }
        }

        counter = 0;

        // start checking cells to left

        for(int i = (int)maze_coords.x ; i > -1; i--){

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
            }else if(c.getType() == Constants.DESTRUCTIBLE_BLOCK || counter > playerComponent.getBombPower()){
                type = Constants.EXPLOSION_CORNER;
            }else{
                type = Constants.EXPLOSION_MIDDLE;
            }

            createExplosion(c.getCol(), c.getRow(), c, type, Constants.LEFT);

            if(c.getType() == Constants.DESTRUCTIBLE_BLOCK){
                break;
            }

            if(counter > playerComponent.getBombPower()){
                break;
            }

        }

        counter = 0;

        // start checking up

        for(int i = (int)maze_coords.y ; i < Constants.MAZE_HEIGHT; i++){

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
            }else if(c.getType() == Constants.DESTRUCTIBLE_BLOCK || counter > playerComponent.getBombPower()){
                type = Constants.EXPLOSION_CORNER;
            }else{
                type = Constants.EXPLOSION_MIDDLE;
            }

            createExplosion(c.getCol(), c.getRow(), c, type, Constants.UP);

            if(c.getType() == Constants.DESTRUCTIBLE_BLOCK){
                break;
            }

            if(counter > playerComponent.getBombPower()){
                break;
            }

        }

        counter = 0;

        // start checking down

        for(int i = (int)maze_coords.y; i > -1; i--){

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
            }else if(c.getType() == Constants.DESTRUCTIBLE_BLOCK || counter > playerComponent.getBombPower()){
                type = Constants.EXPLOSION_CORNER;
            }else{
                type = Constants.EXPLOSION_MIDDLE;
            }

            createExplosion(c.getCol(), c.getRow(), c, type, Constants.DOWN);

            if(c.getType() == Constants.DESTRUCTIBLE_BLOCK){
                break;
            }

            if(counter > playerComponent.getBombPower()){
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
                cell.setExplosion(null);
                engine.removeEntity(explosion);

                if(cell.getType() == Constants.DESTRUCTIBLE_BLOCK){
                    cell.setType(Constants.EMPTY_BLOCK);
                    world.destroyBody(cell.getBody());
                    cell.setBody(null);

                    if(MathUtils.random() < Constants.POWER_UP_PROB){
                        int type = MathUtils.random(2);
                        createPowerUp(type, cell);
                    }

                }


            }
        };

        Vector2 position = maze_to_world_coords(maze_x, maze_y);

        Body body = createBody(
                position.x,
                position.y,
                48,
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

    private void createPowerUp(int type, CellComponent cell){

        Entity powerUp = new Entity();

        TextureRegion region = null;
        PowerUpAction action = null;

        Vector2 position = maze_to_world_coords(cell.getCol(), cell.getRow());

        Body body = createBody(
                position.x,
                position.y,
                32,
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

                        Assets.getInstance().power_up.play();
                        PlayerComponent playerComponent = Mappers.player.get(player);
                        SpeedComponent speedComponent = Mappers.speed.get(player);

                        if(playerComponent.getSpeed() + 1 > Constants.MAX_SPEED)
                            return;

                        playerComponent.setSpeed(playerComponent.getSpeed() + 0.5f);
                        speedComponent.setSpeed(speedComponent.getSpeed() + 0.5f);

                    }
                };
                break;
            case Constants.FLAME_POWER_UP:
                region = Assets.getInstance().bomberman_atlas.findRegion("FlamePowerup");
                action = new PowerUpAction() {
                    @Override
                    public void action(Entity player) {

                        PlayerComponent playerComponent = Mappers.player.get(player);
                        playerComponent.setBombPower(playerComponent.getBombPower() + 1);
                        Assets.getInstance().power_up.play();
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

    public Body createWallBody(int x, int y){

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x * Constants.CELL_SIZE / Constants.PPM + Constants.CELL_SIZE / 2 / Constants.PPM,
                y * Constants.CELL_SIZE / Constants.PPM + Constants.CELL_SIZE / 2 / Constants.PPM);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.CELL_SIZE/2 / Constants.PPM, Constants.CELL_SIZE / 2 / Constants.PPM);
        body.createFixture(shape, 0f);
        shape.dispose();
        return body;
    }

    private Body createBody(float x, float y, float radius, boolean dynamic, boolean isSensor,
                            short filterIndex){

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = dynamic ? BodyDef.BodyType.DynamicBody: BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x / Constants.PPM, y / Constants.PPM);

        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.isSensor = isSensor;
        
        CircleShape circle = new CircleShape();
        circle.setRadius(radius / 2 / Constants.PPM);

        fixtureDef.shape = circle;

        fixtureDef.filter.groupIndex = filterIndex;

        body.createFixture(fixtureDef);

        circle.dispose();
        return body;
    }

    @Override
    public void render(float delta) {

        game.batch.begin();

        game.batch.setProjectionMatrix(camera.combined);

        engine.update(delta);

        if(currentState == Constants.START_STATE){
            currentTime += delta;

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

            currentTime += delta;

            if(currentTime > 1f){
                currentTime -= 1f;
                seconds ++;

                if(seconds == 2){
                    Assets.getInstance().game_over.play();
                }else if(seconds == 4){
                    game.setScreen(new MainMenuScreen(game));
                }
            }

        }else if(currentState == Constants.WIN_STATE){

            currentTime += delta;

            if(currentTime > 1f){
                currentTime -= 1f;
                seconds ++;
                if(seconds == 2){
                    Assets.getInstance().congratulations.play();
                }else if(seconds == 4){
                    game.setScreen(new MainMenuScreen(game));
                }

            }

        }

        game.batch.end();

        //box2DDebugRenderer.render(world, camera.combined);

        world.step(1/60f, 6, 2);

        sweepDeadBodies();


        if(creepCount <= 0 && portal == false && game_mode == Constants.ONE_PLAYER_MODE){
            createPortal();
            portal = true;
        }

    }

    private void sweepDeadBodies() {

        Array<Body> bodies = new Array<Body>();

        world.getBodies(bodies);

        for(Body b: bodies){

            if(b != null){
                UserData data = (UserData) b.getUserData();
                if(data != null && data.isReadyToBeDestroyed()) {
                    world.destroyBody(b);
                    b.setUserData(null);
                    b = null;
                }
            }
        }
    }

    private void player_lose(Entity player){

        StateComponent stateComponent = Mappers.state.get(player);

        if(stateComponent.getCurrentState() == Constants.DEAD)
            return;

        stateComponent.setCurrentState(Constants.DEAD);

        currentState = Constants.LOSE_STATE;
        Assets.getInstance().you_lose.play();
    }

    private void player_win(Entity player){

        engine.removeEntity(player);
        currentState = Constants.WIN_STATE;
        Assets.getInstance().you_win.play();

    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        world.dispose();
        box2DDebugRenderer.dispose();
    }

    @Override
    public void entityAdded(Entity entity) {}

    @Override
    public void entityRemoved(Entity entity) {

        PhysicComponent physicComponent = Mappers.physic.get(entity);
        ((UserData)physicComponent.getBody().getUserData()).setReadyToBeDestroyed(true);

    }

    @Override
    public void beginContact(Contact contact) {

        UserData dataA = (UserData) contact.getFixtureA().getBody().getUserData();
        UserData dataB = (UserData) contact.getFixtureB().getBody().getUserData();

        if(dataA == null || dataB == null)
            return;

        Family powerUpFamily;
        ImmutableArray<Entity> powerups;

        Family playerFamily;
        ImmutableArray<Entity> players;

        Family creepsFamily;
        ImmutableArray<Entity> creeps;

        // Collision between player & creep ===============================================

        if(dataA.getType() == Constants.PLAYER && dataB.getType() == Constants.CREEP ||
                dataB.getType() == Constants.PLAYER && dataA.getType() == Constants.CREEP){

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

        // Collision between explosion & creep ============================================

        if(dataA.getType() == Constants.EXPLOSION && dataB.getType() == Constants.CREEP ||
                dataB.getType() == Constants.EXPLOSION && dataA.getType() == Constants.CREEP){

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

        // Collision between player & explosion ============================================

        if(dataA.getType() == Constants.PLAYER && dataB.getType() == Constants.EXPLOSION ||
                dataB.getType() == Constants.PLAYER && dataA.getType() == Constants.EXPLOSION){

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

        // Collision between player & power_up =============================================

        if(dataA.getType() == Constants.POWER_UP && dataB.getType() == Constants.PLAYER ||
                dataB.getType() == Constants.POWER_UP && dataA.getType() == Constants.PLAYER){

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

            for(int i = 0; i < powerups.size(); i++){

                Entity e = powerups.get(i);

                PhysicComponent physicComponent = Mappers.physic.get(e);

                if(physicComponent.getBody().equals(contact.getFixtureA().getBody()) ||
                        physicComponent.getBody().equals(contact.getFixtureB().getBody())){

                    Mappers.power_up.get(e).getAction().action(player);
                    engine.removeEntity(e);
                    return;
                }
            }
        }

        // Collision between player & portal =============================================

        if(dataA.getType() == Constants.PLAYER && dataB.getType() == Constants.PORTAL ||
                dataB.getType() == Constants.PLAYER && dataA.getType() == Constants.PORTAL){

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

    @Override
    public void endContact(Contact contact) {

        UserData dataA = (UserData) contact.getFixtureA().getBody().getUserData();
        UserData dataB = (UserData) contact.getFixtureB().getBody().getUserData();

        if(dataA != null && dataA.getType() == Constants.BOMB){
            contact.getFixtureA().setSensor(false);
        }
        if(dataB != null && dataB.getType() == Constants.BOMB){
            contact.getFixtureB().setSensor(false);
        }
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

}
