package com.codepath.tender.models;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.text.DecimalFormat;

@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {

    public static final String NAME_KEY = "name";
    public static final String IMAGE_KEY = "image_url";
    public static final String DISTANCE_KEY = "distance";
    public static final String RATING_KEY = "rating";

    private String name;
    private String image_url;
    private double distance;
    private double rating;

    public Restaurant() {}

    public String getName(){return getString(NAME_KEY);}

    public String getImage_url() {
        return getString(IMAGE_KEY);
    }

    public double getDistance() {
        return (float) getNumber(DISTANCE_KEY);
    }

    public double getRating() {
        return getDouble(RATING_KEY);
    }

    public String getPhone() {
        return "phone";
    }

    public String getPrice() {
        return "price";
    }

    public int getReview_count() {
        return 0;
    }

    public void setRestaurantProperties() {
        put(NAME_KEY, this.name);
        put(IMAGE_KEY, this.image_url);
        put(DISTANCE_KEY, this.distance);
        put(RATING_KEY, this.rating);
    }

    public void setName(@NonNull String name) {
        put(NAME_KEY, name);
    }

    public void setImage_url(String image_url) {
        put(IMAGE_KEY, image_url);
    }

    public void setDistance(double distance) {
        put(DISTANCE_KEY, distance);
    }

    public void setRating(double rating) {
        put(RATING_KEY, rating);
    }

    public void setPhone(String phone) {
    }

    public void setPrice(String price) {
    }

    public void setReview_count(int rating_count) {
    }

    //helper method that returns a display string of the distance in miles
    public String getDisplayDistance() {
        float milesPerMeter = 0.000621371f;
        DecimalFormat df = new DecimalFormat("#.#");
        float distanceInMiles = milesPerMeter * getLong(DISTANCE_KEY);
        return df.format(distanceInMiles) + " mi";

    }
}

