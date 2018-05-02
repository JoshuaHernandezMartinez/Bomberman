package com.mygdx.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.utils.PowerUpAction;

public class PowerUpComponent implements Component {

    private PowerUpAction action;

    public PowerUpComponent(PowerUpAction action){
        this.action = action;
    }

    public PowerUpAction getAction() {
        return action;
    }
}
