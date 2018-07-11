package com.mygdx.utils;

public class UserData {

    public int type;
    public boolean readyToBeDestroyed; // true if the body can be destroyed
    public boolean ready_to_collide; // true if the bomb can be set to collide with players

    public UserData(int type){
        this.type = type;
        readyToBeDestroyed = false;
        ready_to_collide = false;
    }

}
