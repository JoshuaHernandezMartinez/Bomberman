package com.mygdx.components;

import com.badlogic.ashley.core.Component;

public class BombComponent implements Component{

    private PlayerComponent playerComponent;

    public BombComponent(PlayerComponent playerComponent){
        this.playerComponent = playerComponent;
    }

    public PlayerComponent getPlayerComponent() {
        return playerComponent;
    }
}
