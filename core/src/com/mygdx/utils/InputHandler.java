package com.mygdx.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.components.PhysicComponent;
import com.mygdx.components.PlayerComponent;
import com.mygdx.screens.GameWorld;
import com.mygdx.screens.MultiplayerGame;
import com.mygdx.screens.PlayScreen;

public class InputHandler extends InputAdapter {

    private GameWorld gameWorld;
    private Entity player;

    public InputHandler(GameWorld gameWorld, Entity player){
        this.gameWorld = gameWorld;
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {

        if(gameWorld.getCurrentState() == Constants.WIN_STATE || gameWorld.getCurrentState() == Constants.START_STATE ||
                gameWorld.getCurrentState() == Constants.LOSE_STATE)
            return false;

        PlayerComponent pc = Mappers.player.get(player);
        PhysicComponent phc = Mappers.physic.get(player);

        if(pc.getInputKeys() != null && keycode == pc.getInputKeys()[Constants.BOMB]){

            if(pc.getBombs() > 0) {

                Vector2 pos = phc.getBody().getPosition();

                gameWorld.createBomb(pos.x,
                        pos.y, pc.getBombPower(), pc);

                if(gameWorld instanceof MultiplayerGame){
                    ((MultiplayerGame)gameWorld).emit_bomb(pos.x, pos.y, pc.getBombPower());
                }

            }
        }
        return false;
    }
}
