package com.mygdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.components.PhysicComponent;
import com.mygdx.components.RegionComponent;
import com.mygdx.utils.Constants;
import com.mygdx.utils.Mappers;

import java.util.Comparator;

public class EntityRendererSystem extends SortedIteratingSystem {

    private SpriteBatch batch;

    public EntityRendererSystem(SpriteBatch batch){
        super(Family.all(RegionComponent.class).get(), new ZComparator());
        this.batch = batch;
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {

        PhysicComponent physicComponent = Mappers.physic.get(entity);
        RegionComponent region = Mappers.region.get(entity);

        batch.draw(region.getRegion(),
                physicComponent.getBody().getPosition().x - region.getRegion().getRegionWidth() / 2 / Constants.PPM,
                physicComponent.getBody().getPosition().y - region.getRegion().getRegionWidth() / 2 / Constants.PPM,
                region.getRegion().getRegionWidth() / Constants.PPM,
                region.getRegion().getRegionHeight() / Constants.PPM);

    }


    private static class ZComparator implements Comparator<Entity> {

        @Override
        public int compare(Entity a, Entity b) {

            if(Mappers.physic.get(a).getzOrder() > Mappers.physic.get(b).getzOrder())
                return 1;
            return -1;
        }
    }

}
