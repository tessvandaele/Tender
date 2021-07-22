package com.codepath.tender.models;

public class Review {

    public Review() { }

    private String text;
    private int rating;
    private String time_created;
    private User user;

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public String getTime_created() {
        return time_created;
    }

    public User getUser() {
        return user;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
