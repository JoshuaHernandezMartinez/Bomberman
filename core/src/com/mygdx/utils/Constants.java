package com.mygdx.utils;

public class Constants {

    public static final int VIEWPORT_WIDTH = 1524;
    public static final int VIEWPORT_HEIGHT = 864;

    public static final int VIEWPORT_WIDTH_GUI = (int)(1524*0.6f);
    public static final int VIEWPORT_HEIGHT_GUI = (int)(864*0.6f);

    public static final float PPM = 100;

    public static final int CELL_SIZE = 64;
    public static final int BOMB_SIZE = 48;
    public static final int MAZE_WIDTH = 17;
    public static final int MAZE_HEIGHT = 13;

    public static float ANIMATION_TIME = 0.06f;

    public static final float MIN_SPEED = 2f;
    public static final float MAX_SPEED = 4f;

    public static final int MIN_BOMBS = 1;
    public static final int MAX_BOMBS = 8;

    public static final int MIN_BOMB_POWER = 1;
    public static final int MAX_BOMB_POWER = 8;

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int DEAD = 4;
    public static final int DEFAULT_STATE = 0;

    public static final int BOMB = 4;
    public static final int POWER_UP = 5;
    public static final int EXPLOSION = 6;
    public static final int PLAYER = 7;
    public static final int CREEP = 8;
    public static final int PORTAL = 9;

    public static final float RAY_LENGTH = 32;

    public static final float BOMB_EXPLODE_TIME = 3f;
    public static final float EXPLOSION_TIME = 0.42f;

    public static final int EMPTY_BLOCK = 0;
    public static final int SOLID_BLOCK = 1;
    public static final int DESTRUCTIBLE_BLOCK = 2;

    public static final int FLAME_POWER_UP = 0;
    public static final int BOMB_POWER_UP = 1;
    public static final int SPEED_POWER_UP = 2;

    public static  final float TILE_PROB = 0.5f;
    public static  final float ENEMY_PROB = 0.15f;
    public static  final float POWER_UP_PROB = 0.3f;

    public static final int EXPLOSION_ORIGIN = 0;
    public static final int EXPLOSION_MIDDLE = 1;
    public static final int EXPLOSION_CORNER = 2;

    // assets file names

    public static final String bomberman = "textures/bomberman.atlas";

    public static final String bomb = "textures/bomb.atlas";

    public static final String title_background = "textures/title_background_big.png";

    public static final String font = "fonts/action_jackson.fnt";

    public static final String libgdxSkinAtlas = "skin/uiskin.atlas";
    public static final String libgdxSkinJson = "skin/uiskin.json";


    // map minies

    public static final String map_minies = "textures/minies.atlas";
    // sounds

    public static final String number_3 = "sounds/3.ogg";
    public static final String number_2 = "sounds/2.ogg";
    public static final String number_1 = "sounds/1.ogg";
    public static final String go = "sounds/go.ogg";
    public static final String power_up = "sounds/power_up.ogg";
    public static final String congratulations = "sounds/congratulations.ogg";
    public static final String game_over = "sounds/game_over.ogg";
    public static final String you_win = "sounds/you_win.ogg";
    public static final String yow_lose = "sounds/you_lose.ogg";

    // loading screen assets

    public static final String progress_bar = "textures/progress_bar.png";
    public static final String progress_bar_base = "textures/progress_bar_base.png";
    public static final String logo = "textures/logo.png";
    public static final String background = "textures/background.png";

    // play screen states

    public static final int START_STATE = 0;
    public static final int PLAY_STATE = 1;
    public static final int LOSE_STATE = 2;
    public static final int WIN_STATE = 3;

    // game modes

    public static final int ONE_PLAYER_MODE = 0;
    public static final int TWO_PLAYER_MODE = 1;
    public static final int MULTIPLAYER_MODE = 2;

    public static final String PREFERENCES = "bomberman.pref";

    // maps

    public static final String lava = "maps/lava.tmx";
    public static final String normal = "maps/default.tmx";
    public static final String bricks = "maps/bricks.tmx";

}
