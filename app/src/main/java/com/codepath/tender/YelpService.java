package com.codepath.tender;

import com.codepath.tender.models.Restaurant;
import com.codepath.tender.models.YelpReviewResult;
import com.codepath.tender.models.YelpSearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/* interface declaring API call to fetch YelpSearchResult object */

public interface YelpService {

    @GET("businesses/search")
    Call<YelpSearchResult> getRestaurants(
            @Header("Authorization") String authHeader, //authorization using API key
            @Query("latitude") double latitude, //latitude parameter
            @Query("longitude") double longitude, //longitude parameter
            @Query("limit") int limit, //limit number of restaurants fetched
            @Query("offset") int offset, //offset for where to start retrieval
            @Query("radius") int radius,
            @Query("price") String prices);

    @GET("businesses/{id}")
    Call<Restaurant> getRestaurantDetails(
            @Header("Authorization") String authHeader,
            @Path("id") String restaurant_id);

    @GET("businesses/{id}/reviews")
    Call<YelpReviewResult> getRestaurantReviews(
            @Header("Authorization") String authHeader,
            @Path("id") String restaurant_id);
}
