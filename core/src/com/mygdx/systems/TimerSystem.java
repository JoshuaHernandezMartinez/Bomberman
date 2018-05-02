package com.mygdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.components.TimerComponent;
import com.mygdx.utils.Mappers;

public class TimerSystem extends IteratingSystem{

    public TimerSystem() {
        super(Family.all(TimerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        TimerComponent tc = Mappers.timer.get(entity);

        tc.setCurrentTime(tc.getCurrentTime() + deltaTime);

        if(tc.getCurrentTime() > tc.getTime()){
            tc.getAction().action();
        }

    }
}
