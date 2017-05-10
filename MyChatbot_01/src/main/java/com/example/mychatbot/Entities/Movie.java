package com.example.mychatbot.Entities;

import com.example.mychatbot.Utilities.MyMethods;

/**
 * Created by david on 02/05/2017.
 */

public class Movie {
    private String id;
    private String name;
    private String desc;
    private String length;
    private String category;
    private String image;

    public Movie(String id,
                 String name,
                 String desc,
                 String length,
                 String category,
                 String image){
        this.id = id;
        this.name= name;
        this.desc= desc;
        this.length = length;
        this.category = category;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
