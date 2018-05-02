package com.mygdx.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mygdx.components.AnimationComponent;
import com.mygdx.components.IdleComponent;
import com.mygdx.components.InputComponent;
import com.mygdx.components.PlayerComponent;
import com.mygdx.components.StateComponent;
import com.mygdx.utils.Constants;
import com.mygdx.utils.Mappers;

public class PlayerSystem extends IteratingSystem{

    public PlayerSystem()
    {
        super(Family.all(PlayerComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        IdleComponent idleComponent = Mappers.idle.get(entity);
        AnimationComponent animationComponent = Mappers.animation.get(entity);
        PlayerComponent playerComponent = Mappers.player.get(entity);
        StateComponent stateComponent = Mappers.state.get(entity);

        if(stateComponent.getCurrentState() == Constants.DEAD &&
                animationComponent.getAnimations().get(Constants.DEAD).isAnimationFinished(stateComponent.getAnimationTime())){
            this.getEngine().removeEntity(entity);
            return;
        }

        if(Gdx.input.isKeyPressed(playerComponent.getInputKeys()[Constants.RIGHT])){
            stateComponent.setCurrentState(Constants.RIGHT);
            idleComponent.setIdle(false);
        }else if(Gdx.input.isKeyPressed(playerComponent.getInputKeys()[Constants.LEFT])){
            stateComponent.setCurrentState(Constants.LEFT);
            idleComponent.setIdle(false);
        }else if(Gdx.input.isKeyPressed(playerComponent.getInputKeys()[Constants.UP])){
            stateComponent.setCurrentState(Constants.UP);
            idleComponent.setIdle(false);
        }else if(Gdx.input.isKeyPressed(playerComponent.getInputKeys()[Constants.DOWN])){
            stateComponent.setCurrentState(Constants.DOWN);
            idleComponent.setIdle(false);
        }else{
            idleComponent.setIdle(true);
        }

    }
}
