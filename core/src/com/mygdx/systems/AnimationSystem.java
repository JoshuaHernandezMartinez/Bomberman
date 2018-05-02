package com.mygdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.components.AnimationComponent;
import com.mygdx.components.RegionComponent;
import com.mygdx.components.StateComponent;
import com.mygdx.utils.Mappers;

public class AnimationSystem extends IteratingSystem {

    public AnimationSystem() {
        super(Family.all(AnimationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        RegionComponent rc = Mappers.region.get(entity);
        AnimationComponent asc = Mappers.animation.get(entity);
        StateComponent sc = Mappers.state.get(entity);

        sc.setAnimationTime(sc.getAnimationTime() + deltaTime);

        rc.setRegion((TextureRegion) asc.getAnimations().get(sc.getCurrentState()).getKeyFrame(sc.getAnimationTime()));

    }
}
