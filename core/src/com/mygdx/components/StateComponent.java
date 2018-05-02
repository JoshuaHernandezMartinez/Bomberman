package com.mygdx.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.utils.Constants;

public class StateComponent implements Component{

    private int currentState;

    private float animationTime;

    public StateComponent(int currentState){
        this.currentState = currentState;
        animationTime = 0;
    }

    public int getCurrentState() {
        return currentState;
    }

    public float getAnimationTime() {
        return animationTime;
    }

    public void setAnimationTime(float animationTime) {
        this.animationTime = animationTime;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }
}
