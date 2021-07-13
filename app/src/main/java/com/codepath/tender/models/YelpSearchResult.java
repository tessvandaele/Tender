package com.codepath.tender.models;

import com.codepath.tender.models.Restaurant;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YelpSearchResult {

    public @SerializedName("total") int total;
    public @SerializedName("businesses") List<Restaurant> restaurants;

    public YelpSearchResult() {}
}
