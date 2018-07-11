package com.mygdx.networking;

public class PlayerClient {

    public String id;
    public String name;
    public int session_id;
    public boolean ready;

    public PlayerClient(String id, String name, int session_id, boolean ready){
        this.id = id;
        this.name = name;
        this.session_id = session_id;
        this.ready = ready;
    }

}
