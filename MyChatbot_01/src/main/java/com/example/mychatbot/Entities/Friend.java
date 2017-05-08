package com.example.mychatbot.Entities;

/**
 * Created by david on 14/04/2017.
 */

public class Friend {
    private String fbid;
    private String name;

    public Friend(String fbid, String name){
        this.name=name;
        this.fbid=fbid;
    }

    public String getFbid(){
        return fbid;
    }

    public String getName(){
        return name;
    }
}
