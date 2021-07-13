package com.codepath.tender;

import com.codepath.tender.models.YelpSearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/* interface declaring API call to fetch YelpSearchResult object */

public interface YelpService {

    @GET("businesses/search")
    Call<YelpSearchResult> getRestaurants(
            @Header("Authorization") String authHeader, //authorization using API key
            @Query("location") String location); //location paramater

}
