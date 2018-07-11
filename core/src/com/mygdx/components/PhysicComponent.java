package com.mygdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

public class PhysicComponent implements Component{

    private Body body;

    private int zOrder;

    public PhysicComponent(Body body, int zOrder){
        this.body = body;
        this.zOrder = zOrder;
    }

    public Body getBody() {
        return body;
    }

    public int getzOrder(){return zOrder;}
}
