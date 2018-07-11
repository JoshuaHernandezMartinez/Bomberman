package com.mygdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import com.mygdx.components.CellComponent;
import com.mygdx.components.CreepComponent;
import com.mygdx.components.PhysicComponent;
import com.mygdx.components.StateComponent;
import com.mygdx.screens.GameWorld;
import com.mygdx.screens.PlayScreen;
import com.mygdx.utils.Constants;
import com.mygdx.utils.Mappers;
import com.mygdx.utils.UserData;

public class CreepSystem extends IteratingSystem implements RayCastCallback{

    private GameWorld gameWorld;

    private StateComponent stateComponent;
    private PhysicComponent physicComponent;
    private CreepComponent creepComponent;

    public CreepSystem(GameWorld gameWorld) {
        super(Family.all(CreepComponent.class).get());
        this.gameWorld = gameWorld;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        stateComponent = Mappers.state.get(entity);
        physicComponent = Mappers.physic.get(entity);
        creepComponent = Mappers.creep.get(entity);

        Vector2 from = physicComponent.getBody().getPosition();
        Vector2 dir = creepComponent.rays[stateComponent.getCurrentState()];

        gameWorld.getWorld().rayCast(this,
                 from.x, from.y,
                from.x + dir.x,
                from.y + dir.y);

    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {

        UserData userData = (UserData) fixture.getBody().getUserData();

        if(userData != null){

            if(
                    userData.type == Constants.PLAYER ||
                    userData.type == Constants.POWER_UP ||
                    userData.type == Constants.CREEP
                    ) {
                return -1;
            }
        }

        pickRandomDirection();

        return 0;
    }

    private void pickRandomDirection(){

        Array<Integer> dirs = getDirections();

        int range = dirs.size - 1;

        if(range < 0)
            return;

        stateComponent.setCurrentState(dirs.get(MathUtils.random(range)));

        // fix position offset

        Vector2 newPosition = gameWorld.world_to_maze_coords(physicComponent.getBody().getPosition());

        newPosition = gameWorld.maze_to_world_coords(newPosition.x, newPosition.y);

         physicComponent.getBody().setTransform(newPosition.x, newPosition.y, 0);

    }

    private Array<Integer> getDirections(){

        Family cellsFamily = Family.all(CellComponent.class).get();
        ImmutableArray<Entity> cells = this.getEngine().getEntitiesFor(cellsFamily);

        Vector2 mazePosition = gameWorld.world_to_maze_coords(physicComponent.getBody().getPosition());

        Array<Integer> dirs = new Array<Integer>();

        for(int i = 0; i < cells.size(); i++){

            CellComponent cellComponent = Mappers.cell.get(cells.get(i));

            if(cellComponent.getCol() == mazePosition.x + 1 && cellComponent.getRow() == mazePosition.y){
                dirs.add(Constants.RIGHT);
            }
            if(cellComponent.getCol() == mazePosition.x - 1 && cellComponent.getRow() == mazePosition.y){
                dirs.add(Constants.LEFT);
            }
            if(cellComponent.getCol() == mazePosition.x && cellComponent.getRow() == mazePosition.y + 1){
                dirs.add(Constants.UP);
            }

            if(cellComponent.getCol() == mazePosition.x && cellComponent.getRow() == mazePosition.y - 1){
                dirs.add(Constants.DOWN);
            }

        }

        return dirs;
    }

}
