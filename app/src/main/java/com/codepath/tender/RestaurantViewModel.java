package com.codepath.tender;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.codepath.tender.models.Restaurant;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/* View model that provides data to the UI and survives configuration changes between fragments/activities */

public class RestaurantViewModel extends AndroidViewModel {

    private RestaurantRepository repository;
    private MutableLiveData<ArrayList<Restaurant>> restaurants;
    private int topPosition;
    private int offset;

    private MutableLiveData<Restaurant> currentRestaurant;

    public RestaurantViewModel (Application application) {
        super(application);
        repository = new RestaurantRepository();
        restaurants = new MutableLiveData<>();
        currentRestaurant = new MutableLiveData<>();

        //initializing restaurants arraylist
        ArrayList<Restaurant> temp = new ArrayList<>();
        restaurants.setValue(temp);
    }

    public void insertFavorite(String restaurant_id, String user_id) { repository.insertFavorite(restaurant_id, user_id); }

    public void fetchRestaurants(double latitude, double longitude, int limit, int offset, int radius, String prices, String categories, String sort) { repository.fetchRestaurants(latitude, longitude, limit, offset, radius, prices, categories, sort); }

    public void deleteFavorite(String id) { repository.deleteFavorite(id); }

    public void getFavorites() { repository.getFavorites(); }

    public void getRestaurantDetails(String restaurant_id) {repository.getRestaurantDetails(restaurant_id);}

    public void getRestaurantReviews(String restaurant_id) {repository.getRestaurantReviews(restaurant_id);}

    public void getOtherUsersWithFavorite(String restaurant_id) { repository.getOtherUserFavorite(restaurant_id);}

    public void setFetchRestaurantListener(RestaurantRepository.FetchRestaurantsListener listener) { repository.setFetchRestaurantListener(listener); }

    public void setFetchFavoritesListener(RestaurantRepository.FetchFavoritesListener listener) { repository.setFetchFavoritesListener(listener); }

    public void setRestaurantDetailsListener(RestaurantRepository.RestaurantDetailsListener listener) { repository.setRestaurantDetailsListener(listener);}

    public void setReviewsListener(RestaurantRepository.ReviewsListener listener) { repository.setReviewsListener(listener); }

    public void setOtherUsersListener(RestaurantRepository.OtherUsersListener listener) { repository.setOtherUsersListener(listener);}

    public void setOffset(int offset) { this.offset = offset; }

    public void setTopPosition(int topPosition) { this.topPosition = topPosition; }

    public void setRestaurant(Restaurant restaurant) {
        this.currentRestaurant.setValue(restaurant);
    }

    public int getOffset() {
        return offset;
    }

    public int getTopPosition() {
        return topPosition;
    }

    public MutableLiveData<Restaurant> getCurrent_restaurant() {
        return currentRestaurant;
    }

    public void addAllRestaurants(List<Restaurant> restaurants) {
        this.restaurants.getValue().addAll(restaurants);
    }

    public void clearRestaurants() {
        this.restaurants.getValue().clear();
    }

    public MutableLiveData<ArrayList<Restaurant>> getRestaurants() {
        return this.restaurants;
    }
}