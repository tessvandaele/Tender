package com.codepath.tender.models;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DecimalFormat;

import static com.codepath.tender.Constants.DISTANCE_KEY;
import static com.codepath.tender.Constants.IMAGE_KEY;
import static com.codepath.tender.Constants.NAME_KEY;
import static com.codepath.tender.Constants.PHONE_KEY;
import static com.codepath.tender.Constants.PRICE_KEY;
import static com.codepath.tender.Constants.RATING_KEY;
import static com.codepath.tender.Constants.REVIEW_COUNT_KEY;

@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {

    private String id;
    private String name;
    private String image_url;
    private String url;
    private double distance;
    private double rating;
    private int review_count;
    private String price;
    private String phone;
    private Coordinates coordinates;
    private Location location;

    public Restaurant() {}

    public String getId() { return this.id; }

    public String getName() {return this.name;}

    public String getImage_url() {
        return this.image_url;
    }

    public String getUrl() {
        return url;
    }

    public double getDistance() {
        return this.distance;
    }

    public double getRating() {
        return this.rating;
    }

    public int getReview_count() {
        return this.review_count;
    }

    public String getPrice() {
        return this.price;
    }

    public String getPhone() {
        return this.phone;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Location getLocation() {
        return location;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(@NonNull String name) {
        if(name == null) name = "";
        put(NAME_KEY, name);
    }

    public void setImage_url(String image_url) {
        if(image_url == null) image_url = "";
        put(IMAGE_KEY, image_url);
    }

    public void setUrl(String url) {
        this.url = url;
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
        double lon1 = Math.toRadians(ParseUser.getCurrentUser().getDouble("longitude"));
        double lon2 = Math.toRadians(coordinates.getLongitude());
        double lat1 = Math.toRadians(ParseUser.getCurrentUser().getDouble("latitude"));
        double lat2 = Math.toRadians(coordinates.getLatitude());

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth
        double r = 3956;

        c *= r;

        DecimalFormat df = new DecimalFormat("#.#");
        // calculate the result
        return df.format(c) + " mi";

    }
}

