package com.codepath.tender.models;

import com.google.gson.annotations.SerializedName;

public class Restaurant {

    private String name;
    private String url;
    private String distance;
    private float rating;

    public Restaurant() {}

    public Restaurant(String name, String url, String distance, float rating) {
        this.name = name;
        this.url = url;
        this.distance = distance;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return url;
    }

    public String getDistance() {
        return distance;
    }

    public float getRating() {
        return rating;
    }
}
