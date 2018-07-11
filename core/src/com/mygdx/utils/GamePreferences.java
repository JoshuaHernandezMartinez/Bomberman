package com.mygdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

public class GamePreferences {

    public static final GamePreferences instance = new GamePreferences();

    private Preferences preferences;

    public boolean sound;
    public boolean music;
    public float volSound;
    public float volMusic;
    public int language;
    public boolean showFpsCounter;

    // players input

    public int[] player_keys;

    private GamePreferences(){
        preferences = Gdx.app.getPreferences(Constants.PREFERENCES);
    }

    public void load(){
        sound = preferences.getBoolean("sound", true);
        music = preferences.getBoolean("music", true);
        volSound = MathUtils.clamp(preferences.getFloat("volSound", 0.5f), 0.0f, 1.0f);
        volMusic = MathUtils.clamp(preferences.getFloat("volMusic", 0.5f), 0.0f, 1.0f);
        language = preferences.getInteger("language", 0);

        // load players input

        player_keys = new int[10];

        player_keys[Constants.UP] = preferences.getInteger("player_1_up", Input.Keys.W);
        player_keys[Constants.UP + 5] = preferences.getInteger("player_2_up", Input.Keys.UP);

        player_keys[Constants.DOWN] = preferences.getInteger("player_1_down", Input.Keys.S);
        player_keys[Constants.DOWN + 5] = preferences.getInteger("player_2_down", Input.Keys.DOWN);

        player_keys[Constants.LEFT] = preferences.getInteger("player_1_left", Input.Keys.A);
        player_keys[Constants.LEFT + 5] = preferences.getInteger("player_2_left", Input.Keys.LEFT);

        player_keys[Constants.RIGHT] = preferences.getInteger("player_1_right", Input.Keys.D);
        player_keys[Constants.RIGHT + 5] = preferences.getInteger("player_2_right", Input.Keys.RIGHT);

        player_keys[Constants.BOMB] = preferences.getInteger("player_1_bomb", Input.Keys.B);
        player_keys[Constants.BOMB + 5] = preferences.getInteger("player_2_bomb", Input.Keys.P);

    }

    public void save(){
        preferences.putBoolean("sound", sound);
        preferences.putBoolean("music", music);
        preferences.putFloat("volSound", volSound);
        preferences.putFloat("volMusic", volMusic);
        preferences.putInteger("language", language);

        preferences.putInteger("player_1_up", player_keys[Constants.UP]);
        preferences.putInteger("player_2_up", player_keys[Constants.UP + 5]);

        preferences.putInteger("player_1_down", player_keys[Constants.DOWN]);
        preferences.putInteger("player_2_down", player_keys[Constants.DOWN + 5]);

        preferences.putInteger("player_1_left", player_keys[Constants.LEFT]);
        preferences.putInteger("player_2_left", player_keys[Constants.LEFT + 5]);

        preferences.putInteger("player_1_right", player_keys[Constants.RIGHT]);
        preferences.putInteger("player_2_right", player_keys[Constants.RIGHT + 5]);

        preferences.putInteger("player_1_bomb", player_keys[Constants.BOMB]);
        preferences.putInteger("player_2_bomb", player_keys[Constants.BOMB + 5]);

        preferences.flush();
    }


}
