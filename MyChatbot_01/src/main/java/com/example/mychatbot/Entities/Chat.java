package com.example.mychatbot.Entities;

/**
 * Created by david on 15/04/2017.
 */

public class Chat {

    private String chatid;
    private String chatname;
    private String time;

    public Chat(String chatid, String chatname,String time){
        this.chatname=chatname;
        this.chatid=chatid;
        this.time=time;
    }

    public String getChatId(){
        return chatid;
    }

    public String getChatName(){
        return chatname;
    }

    public String getTime(){
        return time;
    }

}
