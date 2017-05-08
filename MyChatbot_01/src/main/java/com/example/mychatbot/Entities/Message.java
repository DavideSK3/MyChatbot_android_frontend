package com.example.mychatbot.Entities;

import java.sql.Timestamp;

/**
 * Created by david on 15/04/2017.
 */

public class Message {
    private String sender;
    private String content;
    private String time;

    public Message(String sender, String content, String time){
        this.sender = sender;
        this.content = content;
        this.time = time;
    }

    public String getSender(){
        return sender;
    }

    public String getContent(){
        return content;
    }

    public String getTime(){
        return  time;
    }

}
