package com.codepath.tender.models;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DecimalFormat;

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

    //for details api only
    private String display_phone;
    private Hours[] hours;
    private String[] photos;

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

    public String getDisplay_phone() { return display_phone; }

    public Hours[] getHours() { return hours; }

    public String[] getPhotos() { return photos; }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDisplay_phone(String display_phone) {
        this.display_phone = display_phone;
    }

    public void setHours(Hours[] hours) { this.hours = hours;}

    public void setPhotos(String[] photos) { this.photos = photos; }

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

