package com.example.mychatbot.Entities;

/**
 * Created by david on 02/05/2017.
 */

public class Restaurant {
    private String name;
    private String desc;
    private String lat;
    private String lon;
    private String street;
    private String number;
    private String city;
    private String phone;
    private String email;
    private String url;

    public Restaurant(String name,
            String desc,
            String lat,
            String lon,
            String street,
            String number,
            String city,
            String phone,
            String email,
            String url){
        this.name= name;
        this.desc= desc;
        this.lat= lat;
        this.lon= lon;
        this.street= street;
        this.number= number;
        this.city= city;
        this.phone= phone;
        this.email= email;
        this.url= url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
