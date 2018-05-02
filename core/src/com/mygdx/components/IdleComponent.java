package com.mygdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class IdleComponent implements Component{

    private TextureRegion[] idleRegions;

    private boolean isIdle;

    public IdleComponent(TextureRegion[] idleRegions)
    {
        this.idleRegions = idleRegions;
        isIdle = true;
    }

    public TextureRegion[] getIdleRegions() {
        return idleRegions;
    }

    public boolean isIdle() {
        return isIdle;
    }

    public void setIdle(boolean idle) {
        isIdle = idle;
    }
}
