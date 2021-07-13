package com.codepath.tender;

import com.codepath.tender.models.Restaurant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface YelpService {

    @GET("businesses/search")
    Call<YelpSearchResult> getRestaurants(
            @Header("Authorization") String authHeader,
            @Query("location") String location);

}
