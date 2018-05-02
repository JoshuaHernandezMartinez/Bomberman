package com.mygdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.components.CellComponent;
import com.mygdx.utils.Constants;
import com.mygdx.utils.Mappers;

public class MazeRendererSystem extends IteratingSystem{

    private SpriteBatch batch;

    public MazeRendererSystem(SpriteBatch batch) {
        super(Family.all(CellComponent.class).get());
        this.batch = batch;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        CellComponent cellComponent = Mappers.cell.get(entity);

        int row = cellComponent.getRow();
        int col = cellComponent.getCol();
        TextureRegion region = cellComponent.getBlocks()[cellComponent.getType()];

        batch.draw(region, col * Constants.CELL_SIZE / Constants.PPM,
                row * Constants.CELL_SIZE / Constants.PPM,
                region.getRegionWidth() / Constants.PPM,
                region.getRegionHeight() / Constants.PPM);
    }
}
