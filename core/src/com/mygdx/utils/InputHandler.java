package com.mygdx.utils;

import com.badlogic.gdx.InputAdapter;
import com.mygdx.components.PhysicComponent;
import com.mygdx.components.PlayerComponent;
import com.mygdx.screens.PlayScreen;

public class InputHandler extends InputAdapter {

    private PlayScreen playScreen;
    private PlayerComponent playerComponent;
    private PhysicComponent physicComponent;

    public InputHandler(PlayScreen playScreen, PlayerComponent playerComponent, PhysicComponent physicComponent){
        this.playScreen = playScreen;
        this.playerComponent = playerComponent;
        this.physicComponent = physicComponent;
    }

    @Override
    public boolean keyDown(int keycode) {

        if(playScreen.getCurrentState() == Constants.WIN_STATE || playScreen.getCurrentState() == Constants.START_STATE ||
                playScreen.getCurrentState() == Constants.LOSE_STATE || playerComponent == null)
            return false;

        if(playerComponent.getInputKeys() != null && keycode == playerComponent.getInputKeys()[Constants.BOMB]){

            //System.out.println(Input.Keys.toString(keycode));

            if(playerComponent.getBombs() > 0) {
                playScreen.createBomb(physicComponent.getBody().getPosition().x,
                        physicComponent.getBody().getPosition().y, playerComponent);
            }
        }
        return false;
    }
}
