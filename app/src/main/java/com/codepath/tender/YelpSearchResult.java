package com.codepath.tender;

import com.codepath.tender.models.Restaurant;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YelpSearchResult {

    @SerializedName("total") int total;
    @SerializedName("businesses") List<Restaurant> restaurants;

    public YelpSearchResult() {}
}
