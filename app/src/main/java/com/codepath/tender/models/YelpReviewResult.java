package com.codepath.tender.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YelpReviewResult {

    public @SerializedName("reviews") List<Review> reviews;
    public @SerializedName("total") int total;
    public @SerializedName("possible_languages") List<String> languages;

}
