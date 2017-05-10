package com.example.mychatbot.Entities;

/**
 * Created by david on 02/05/2017.
 */

public class Schedule {
    private String id;
    private String cinema;
    private String day;
    private String hour;

    public Schedule(String id,
                    String cinema,
                    String day,
                    String hour){
        this.id = id;
        this.cinema= cinema;
        this.day= day;
        this.hour = hour;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCinema() {
        return cinema;
    }

    public void setCinema(String cinema) {
        this.cinema = cinema;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
