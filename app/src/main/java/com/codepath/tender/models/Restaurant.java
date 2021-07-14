package com.codepath.tender.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.parceler.Parcel;

import java.text.DecimalFormat;

@Parcel
@Entity(tableName = "restaurant_table")
public class Restaurant {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;
    private String image_url;
    private float distance;
    private float rating;

    public Restaurant() {}

    public Restaurant(@NonNull String name) {
        this.name = name;
        this.image_url = "";
        this.distance = 0;
        this.rating = 0;
    }

    public String getName(){return this.name;}

    public String getImage_url() {
        return image_url;
    }

    public float getDistance() {
        return distance;
    }

    public float getRating() {
        return rating;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    //helper method that returns a display string of the distance in miles
    public String getDisplayDistance() {
        float milesPerMeter = 0.000621371f;
        DecimalFormat df = new DecimalFormat("#.#");
        float distanceInMiles = milesPerMeter * distance;
        return df.format(distanceInMiles) + " mi";

    }
}

