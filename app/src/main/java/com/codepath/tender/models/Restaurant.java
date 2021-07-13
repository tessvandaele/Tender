package com.codepath.tender.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Restaurant {

    private String name;
    private String image_url;
    private float distance;
    private float rating;
    //private List<Location> location;

    public Restaurant() {}

    public Restaurant(String name, String image_url, float distance, float rating) {
        this.name = name;
        this.image_url = image_url;
        this.distance = distance;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image_url;
    }

    public float getDistance() {
        return distance;
    }

    public float getRating() {
        return rating;
    }

    public String getDisplayDistance() {
        float milesPerMeter = 0.000621371f;
        float distanceInMiles = milesPerMeter * distance;
        return distanceInMiles + " mi";

    }
}
