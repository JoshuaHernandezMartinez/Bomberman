package com.mygdx.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mygdx.components.IdleComponent;
import com.mygdx.components.PlayerComponent;
import com.mygdx.components.StateComponent;
import com.mygdx.screens.GameWorld;
import com.mygdx.screens.MultiplayerGame;
import com.mygdx.utils.Constants;
import com.mygdx.utils.Mappers;

public class PlayerSystem extends IteratingSystem{

    private GameWorld gameWorld;

    public PlayerSystem(GameWorld gameWorld)
    {
        super(Family.all(PlayerComponent.class).get());
        this.gameWorld = gameWorld;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        IdleComponent idleComponent = Mappers.idle.get(entity);
        PlayerComponent playerComponent = Mappers.player.get(entity);
        StateComponent stateComponent = Mappers.state.get(entity);

        if(Gdx.input.isKeyPressed(playerComponent.getInputKeys()[Constants.RIGHT])){

            if(gameWorld instanceof MultiplayerGame && stateComponent.getCurrentState() != Constants.RIGHT)
                ((MultiplayerGame)gameWorld).playerMoved(Constants.RIGHT);
            stateComponent.setCurrentState(Constants.RIGHT);
            idleComponent.setIdle(false);
        }else if(Gdx.input.isKeyPressed(playerComponent.getInputKeys()[Constants.LEFT])){
            if(gameWorld instanceof MultiplayerGame && stateComponent.getCurrentState() != Constants.LEFT)
                ((MultiplayerGame)gameWorld).playerMoved(Constants.LEFT);

            stateComponent.setCurrentState(Constants.LEFT);
            idleComponent.setIdle(false);
        }else if(Gdx.input.isKeyPressed(playerComponent.getInputKeys()[Constants.UP])){
            if(gameWorld instanceof MultiplayerGame && stateComponent.getCurrentState() != Constants.UP)
                ((MultiplayerGame)gameWorld).playerMoved(Constants.UP);
            stateComponent.setCurrentState(Constants.UP);
            idleComponent.setIdle(false);
        }else if(Gdx.input.isKeyPressed(playerComponent.getInputKeys()[Constants.DOWN])){
            if(gameWorld instanceof MultiplayerGame && stateComponent.getCurrentState() != Constants.DOWN)
                ((MultiplayerGame)gameWorld).playerMoved(Constants.DOWN);
            stateComponent.setCurrentState(Constants.DOWN);
            idleComponent.setIdle(false);
        }else{
            if(gameWorld instanceof MultiplayerGame && !idleComponent.isIdle())
                ((MultiplayerGame)gameWorld).playerMoved(-1);
            idleComponent.setIdle(true);
        }

    }
}
