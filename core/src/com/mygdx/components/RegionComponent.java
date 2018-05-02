package com.mygdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RegionComponent implements Component {

    private TextureRegion region;
    public RegionComponent(){
        region = null;
    }

    public RegionComponent(TextureRegion region){
        this.region = region;
    }

    public TextureRegion getRegion() {
        return region;
    }

    public void setRegion(TextureRegion region) {
        this.region = region;
    }
}
