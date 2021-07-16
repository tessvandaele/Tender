package com.codepath.tender.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

import java.text.DecimalFormat;

@ParseClassName("Post")
public class Restaurant extends ParseObject {

    public static final String NAME_KEY = "name";
    public static final String IMAGE_KEY = "image_url";
    public static final String DISTANCE_KEY = "distance";
    public static final String RATING_KEY = "rating";

    public Restaurant() {}

    public String getName(){return getString(NAME_KEY);}

    public String getImage_url() {
        return getString(IMAGE_KEY);
    }

    public float getDistance() {
        return getLong(DISTANCE_KEY);
    }

    public float getRating() {
        return getLong(RATING_KEY);
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

    public void setName(@NonNull String name) {
        put(NAME_KEY, name);
    }

    public void setImage_url(String image_url) {
        put(IMAGE_KEY, image_url);
    }

    public void setDistance(float distance) {
        put(DISTANCE_KEY, distance);
    }

    public void setRating(float rating) {
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

