package com.codepath.tender.models;

public class Restaurant {

    private String name;
    private int image;
    private String distance;
    private double rating;

    public Restaurant() {}

    public Restaurant(String name, int image, String distance, double rating) {
        this.name = name;
        this.image = image;
        this.distance = distance;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public String getDistance() {
        return distance;
    }

    public double getRating() {
        return rating;
    }
}
