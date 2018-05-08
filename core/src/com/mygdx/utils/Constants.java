package com.mygdx.utils;

public class Constants {

    public static final int VIEWPORT_WIDTH = 1270;
    public static final int VIEWPORT_HEIGHT = 720;
    public static final float PPM = 100;

    public static final int CELL_SIZE = 64;
    public static final int BOMB_SIZE = 48;
    public static final int MAZE_WIDTH = 15;
    public static final int MAZE_HEIGHT = 11;

    public static float ANIMATION_TIME = 0.06f;

    public static final float MIN_SPEED = 2f;
    public static final float MAX_SPEED = 5f;

    public static final int MIN_BOMBS = 1;
    public static final int MAX_BOMBS = 8;

    public static final int MIN_BOMB_POWER = 1;

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

    public static final String player_front = "textures/bombermanFront.atlas";
    public static final String player_back = "textures/bombermanBack.atlas";
    public static final String player_right = "textures/bombermanSide.atlas";
    public static final String player_left = "textures/bombermam_left.atlas";

    public static final String bomb = "textures/bomb.atlas";

    public static final String creep_up = "textures/creep_up.atlas";
    public static final String creep_down = "textures/creep_down.atlas";
    public static final String creep_left = "textures/creep_left.atlas";
    public static final String creep_right = "textures/creep_right.atlas";

    public static final String blood_animation = "textures/blood_animation.atlas";

    public static final String title_background = "textures/title_background.png";

    public static final String font = "fonts/blackchancery.fnt";

    public static final String explosion_horizonal = "textures/explosion_horizontal.atlas";
    public static final String explosion_vertical = "textures/explosion_vertical.atlas";

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

    // play screen states

    public static final int START_STATE = 0;
    public static final int PLAY_STATE = 1;
    public static final int LOSE_STATE = 2;
    public static final int WIN_STATE = 3;

    // game modes

    public static final int ONE_PLAYER_MODE = 0;
    public static final int TWO_PLAYER_MODE = 1;

    // input strings

    public static final String[] player_1_keys = new String[]{
            "player_1_up", "player_1_down", "player_1_left", "player_1_right", "player_1_bomb"
    };

    public static final String[] player_2_keys = new String[]{
            "player_2_up", "player_2_down", "player_2_left", "player_2_right", "player_2_bomb"
    };

    public static final String effects_key = "effects_volume";
    public static final String music_key = "music_volume";

}
