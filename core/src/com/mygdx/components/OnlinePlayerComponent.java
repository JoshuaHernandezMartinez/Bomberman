package com.mygdx.components;

import com.badlogic.ashley.core.Component;

public class OnlinePlayerComponent implements Component {

    private String id;

    public OnlinePlayerComponent(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
