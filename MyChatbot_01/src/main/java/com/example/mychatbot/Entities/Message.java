package com.example.mychatbot.Entities;

import java.sql.Timestamp;

/**
 * Created by david on 15/04/2017.
 */

public class Message {
    private String sender;
    private String content;
    private String time;
    private String intent;
    private String restaurant;
    private String cinema;
    private String image;

    public Message(String sender, String content, String time,String intent, String restaurant, String cinema, String image){
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.intent = intent;
        this.restaurant = restaurant;
        this.cinema = cinema;
        this.image = image;
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

    public String getIntent() {
        return intent;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getCinema() {
        return cinema;
    }

    public String getImage() {
        return image;
    }
}
