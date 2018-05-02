package com.mygdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.screens.PlayScreen;
import com.mygdx.utils.Constants;
import com.mygdx.utils.UserData;

public class CellComponent implements Component{

    private int row, col; // maze indexes

    private int type; // block type;

    private Entity bomb; // if not null this cell contains a bomb

    private Body body; // The body block (SOLID or Destructible)

    private Entity explosion; // if not null, the cell contains an explosion

    private PlayScreen playScreen; // we need a reference to the world class in order to create bodies.

    private TextureRegion[] blocks;

    private Entity power_up; // if not null, the cell constains a power up

    public CellComponent(PlayScreen playScreen,TextureRegion[] blocks,
                         int row, int col, int type){
        this.playScreen = playScreen;
        this.blocks = blocks;
        this. row = row;
        this.col = col;
        this.type = type;
        bomb = null;
        explosion = null;
        power_up = null;
        if(type == Constants.SOLID_BLOCK || type == Constants.DESTRUCTIBLE_BLOCK)
            body = playScreen.createWallBody(col, row);
        else
            body = null;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getType() {
        return type;
    }

    public Entity getBomb() {
        return bomb;
    }

    public Body getBody() {
        return body;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setBomb(Entity bomb) {
        this.bomb = bomb;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public TextureRegion[] getBlocks() {
        return blocks;
    }

    public void setExplosion(Entity explosion){
        this.explosion = explosion;
    }

    public Entity getExplosion() {
        return explosion;
    }

    public void setPower_up(Entity power_up) {
        this.power_up = power_up;
    }

    public Entity getPower_up() {
        return power_up;
    }
}
