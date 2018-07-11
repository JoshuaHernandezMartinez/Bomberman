package com.mygdx.screens;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.components.PlayerComponent;
import com.mygdx.game.Game;
import com.mygdx.utils.Constants;
import com.mygdx.utils.GamePreferences;
import com.mygdx.utils.InputHandler;

public class VersusGame extends GameWorld{

    private Entity player;

    public VersusGame(Game game, OrthographicCamera camera){
        super(game, camera);

        // create player 2 ===============================================================

        int[] player_2_keys = new int[5];

        player_2_keys[Constants.LEFT] =
                GamePreferences.instance.player_keys[Constants.LEFT + 5];
        player_2_keys[Constants.RIGHT] =
                GamePreferences.instance.player_keys[Constants.RIGHT + 5];
        player_2_keys[Constants.UP] =
                GamePreferences.instance.player_keys[Constants.UP + 5];
        player_2_keys[Constants.DOWN] =
                GamePreferences.instance.player_keys[Constants.DOWN + 5];
        player_2_keys[Constants.BOMB] =
                GamePreferences.instance.player_keys[Constants.BOMB + 5];

        player = createPlayer(5, 1);

        PlayerComponent playerComponent = new PlayerComponent(player_2_keys);
        InputHandler inputHandler = new InputHandler(this, player);
        inputMultiplexer.addProcessor(inputHandler);
        player.add(playerComponent);
        engine.addEntity(player);

        // create player 1 ==============================================

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

        playerComponent = new PlayerComponent(player_1_keys);
        inputHandler = new InputHandler(this, player);
        inputMultiplexer.addProcessor(inputHandler);
        player.add(playerComponent);
        engine.addEntity(player);

        this.createMap(Constants.lava);

    }

}
