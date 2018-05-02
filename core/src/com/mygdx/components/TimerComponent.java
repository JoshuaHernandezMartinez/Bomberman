package com.mygdx.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.utils.TimerAction;

public class TimerComponent implements Component{

    private float time;

    private float currentTime;

    private TimerAction action;

    public TimerComponent(float time, TimerAction action){
        this.time = time;
        this.action = action;
        currentTime = 0;
    }

    public float getTime() {
        return time;
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(float currentTime) {
        this.currentTime = currentTime;
    }

    public TimerAction getAction() {
        return action;
    }
}
