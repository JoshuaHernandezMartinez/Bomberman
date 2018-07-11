package com.mygdx.components;

import com.badlogic.ashley.core.Component;

public class BombComponent implements Component{

    private PlayerComponent playerComponent;
    private int bombPower;

    public BombComponent(PlayerComponent playerComponent, int bombPower){
        this.playerComponent = playerComponent;
        this.bombPower = bombPower;
    }

    public int getBombPower() {
        return bombPower;
    }

    public PlayerComponent getPlayerComponent() {
        return playerComponent;
    }
}
