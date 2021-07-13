package com.codepath.tender.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/* Class that defines what is returned from the yelp API request */

public class YelpSearchResult {

    public @SerializedName("total") int total;
    public @SerializedName("businesses") List<Restaurant> restaurants;

}
