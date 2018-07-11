package com.mygdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.IntMap;

public class AnimationComponent implements Component {

    private IntMap<Animation> animations;

    public AnimationComponent(IntMap<Animation> animations){
        this.animations = animations;
    }

    public IntMap<Animation> getAnimations(){
        return animations;
    }

}
