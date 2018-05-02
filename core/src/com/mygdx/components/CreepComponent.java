package com.mygdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.utils.Constants;

public class CreepComponent implements Component {

    public Vector2[] rays;

    public CreepComponent(){
        rays = new Vector2[4];

        rays[Constants.DOWN] = new Vector2(0, -Constants.RAY_LENGTH / Constants.PPM);
        rays[Constants.UP] = new Vector2(0, Constants.RAY_LENGTH / Constants.PPM);
        rays[Constants.LEFT] = new Vector2(-Constants.RAY_LENGTH / Constants.PPM, 0);
        rays[Constants.RIGHT] = new Vector2(Constants.RAY_LENGTH / Constants.PPM, 0);

    }

}
