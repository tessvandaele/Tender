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
    public static final String REVIEW_COUNT_KEY = "review_count";
    public static final String PRICE_KEY = "price";
    public static final String PHONE_KEY = "phone";

    private String name;
    private String image_url;
    private double distance;
    private double rating;
    private int review_count;
    private String price;
    private String phone;

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

    public int getReview_count() {
        return getInt(REVIEW_COUNT_KEY);
    }

    public String getPrice() {
        return getString(PRICE_KEY);
    }

    public String getPhone() {
        return getString(PHONE_KEY);
    }

    public void setRestaurantProperties() {
        setName(this.name);
        setImage_url(this.image_url);
        setDistance(this.distance);
        setRating(this.rating);
        setReview_count(this.review_count);
        setPrice(this.price);
        setPhone(this.phone);
    }

    public void setName(@NonNull String name) {
        if(name == null) name = "";
        put(NAME_KEY, name);
    }

    public void setImage_url(String image_url) {
        if(image_url == null) image_url = "";
        put(IMAGE_KEY, image_url);
    }

    public void setDistance(double distance) {
        put(DISTANCE_KEY, distance);
    }

    public void setRating(double rating) {
        put(RATING_KEY, rating);
    }

    public void setPhone(String phone) {
        if(phone == null) phone = "";
        put(PHONE_KEY, phone);
    }

    public void setPrice(String price) {
        if(price == null) price = "";
        put(PRICE_KEY, price);
    }

    public void setReview_count(int rating_count) {
        put(REVIEW_COUNT_KEY, rating_count);
    }

    //helper method that returns a display string of the distance in miles
    public String getDisplayDistance() {
        float milesPerMeter = 0.000621371f;
        DecimalFormat df = new DecimalFormat("#.#");
        float distanceInMiles = (float) (milesPerMeter * distance);
        return df.format(distanceInMiles) + " mi";

    }
}

