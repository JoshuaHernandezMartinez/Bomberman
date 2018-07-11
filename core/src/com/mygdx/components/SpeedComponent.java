package com.mygdx.components;

import com.badlogic.ashley.core.Component;

public class SpeedComponent implements Component {

    private float speed;

    public SpeedComponent(float speed){
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
