package com.mygdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.components.IdleComponent;
import com.mygdx.components.PhysicComponent;
import com.mygdx.components.RegionComponent;
import com.mygdx.components.StateComponent;
import com.mygdx.utils.Constants;
import com.mygdx.utils.Mappers;

public class IdleSystem extends IteratingSystem {

    public IdleSystem() {
        super(Family.all(IdleComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        PhysicComponent physicComponent = Mappers.physic.get(entity);
        RegionComponent regionComponent = Mappers.region.get(entity);
        IdleComponent idleComponent = Mappers.idle.get(entity);
        StateComponent stateComponent = Mappers.state.get(entity);


        if(idleComponent.isIdle()){
            regionComponent.setRegion(idleComponent.getIdleRegions()[stateComponent.getCurrentState()]);
            physicComponent.getBody().setLinearVelocity(0f, 0f);
        }

    }
}
