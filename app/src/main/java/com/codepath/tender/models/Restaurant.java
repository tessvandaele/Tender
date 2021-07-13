package com.codepath.tender.models;

import java.text.DecimalFormat;

public class Restaurant {

    private String name;
    private String image_url;
    private float distance; //in meters
    private float rating;

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

    //helper method that returns a display string of the distance in miles
    public String getDisplayDistance() {
        float milesPerMeter = 0.000621371f;
        DecimalFormat df = new DecimalFormat("#.#");
        float distanceInMiles = milesPerMeter * distance;
        return df.format(distanceInMiles) + " mi";

    }
}
