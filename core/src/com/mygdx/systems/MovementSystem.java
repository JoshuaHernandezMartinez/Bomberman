package com.mygdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.components.PhysicComponent;
import com.mygdx.components.SpeedComponent;
import com.mygdx.components.StateComponent;
import com.mygdx.utils.Constants;
import com.mygdx.utils.Mappers;

public class MovementSystem extends IteratingSystem {

    public MovementSystem() {
        super(Family.all(SpeedComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        PhysicComponent physicComponent = Mappers.physic.get(entity);
        StateComponent stateComponent = Mappers.state.get(entity);
        SpeedComponent speedComponent = Mappers.speed.get(entity);

        switch (stateComponent.getCurrentState()){
            case Constants.UP:
                physicComponent.getBody().setLinearVelocity(0,speedComponent.getSpeed());
                break;
            case Constants.DOWN:
                physicComponent.getBody().setLinearVelocity(0,-speedComponent.getSpeed());
                break;
            case Constants.LEFT:
                physicComponent.getBody().setLinearVelocity(-speedComponent.getSpeed(),0);
                break;
            case Constants.RIGHT:
                physicComponent.getBody().setLinearVelocity(speedComponent.getSpeed(),0);
                break;
            case Constants.DEAD:
                physicComponent.getBody().setLinearVelocity(0,0);
                break;
        }
    }
}
