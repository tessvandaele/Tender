package com.codepath.tender;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.codepath.tender.models.Restaurant;

import java.util.List;

/* View model that provides data to the UI and survives configuration changes between fragments/activities */

public class RestaurantViewModel extends AndroidViewModel {

    private RestaurantRepository repository;
    private final LiveData<List<Restaurant>> restaurants;
    private Restaurant latestRestaurant;

    public RestaurantViewModel (Application application) {
        super(application);
        repository = new RestaurantRepository(application);
        restaurants = repository.getAllRestaurants();
    }

    public LiveData<List<Restaurant>> getAllRestaurants() { return restaurants; }

    public void insert(Restaurant restaurant) { repository.insert(restaurant);
    latestRestaurant = restaurant;}

    public Restaurant getLatestRestaurant() {
        return this.latestRestaurant;
    }
}