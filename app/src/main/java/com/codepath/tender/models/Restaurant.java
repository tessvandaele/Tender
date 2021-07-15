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
    private String name;
    private String image_url;
    private float distance;
    private float rating;
    private String phone;
    private String price;
    private int review_count;

    public Restaurant() {}

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

    public String getPhone() {
        return phone;
    }

    public String getPrice() {
        return price;
    }

    public int getReview_count() {
        return review_count;
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setReview_count(int rating_count) {
        this.review_count = rating_count;
    }

    //helper method that returns a display string of the distance in miles
    public String getDisplayDistance() {
        float milesPerMeter = 0.000621371f;
        DecimalFormat df = new DecimalFormat("#.#");
        float distanceInMiles = milesPerMeter * distance;
        return df.format(distanceInMiles) + " mi";

    }
}

