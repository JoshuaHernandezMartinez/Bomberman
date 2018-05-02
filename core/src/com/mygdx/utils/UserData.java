package com.mygdx.utils;

public class UserData {

    public int type;
    public boolean readyToBeDestroyed;

    public UserData(int type){
        this.type = type;
        readyToBeDestroyed = false;
    }

    public boolean isReadyToBeDestroyed() {
        return readyToBeDestroyed;
    }

    public void setReadyToBeDestroyed(boolean readyToBeDestroyed) {
        this.readyToBeDestroyed = readyToBeDestroyed;
    }

    public int getType() {
        return type;
    }
}
