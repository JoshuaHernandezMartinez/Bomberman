package com.mygdx.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.utils.Constants;
import com.mygdx.utils.InputHandler;

public class PlayerComponent implements Component{

    private float speed;

    private int bombPower;

    private int bombs;

    private int max_bombs;

    private int[] inputKeys;

    public PlayerComponent(int[] inputKeys){

        this.inputKeys = inputKeys;
        speed = Constants.MIN_SPEED;
        bombPower = Constants.MIN_BOMB_POWER;
        bombs = Constants.MIN_BOMBS;
        max_bombs = Constants.MIN_BOMBS;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getBombPower() {
        return bombPower;
    }

    public void setBombPower(int bombPower) {
        this.bombPower = bombPower;
    }

    public int getBombs() {
        return bombs;
    }

    public void setBombs(int bombs) {
        this.bombs = bombs;
    }

    public int[] getInputKeys() {
        return inputKeys;
    }

    public int getMax_bombs() {
        return max_bombs;
    }

    public void setMax_bombs(int max_bombs) {
        this.max_bombs = max_bombs;
    }
}
