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
    private ArrayList<Restaurant> restaurants;
    private int topPosition;
    private int offset;

    private MutableLiveData<Integer> radius;
    private MutableLiveData<String> prices;
    private MutableLiveData<Double> latitude;
    private MutableLiveData<Double> longitude;


    public RestaurantViewModel (Application application) {
        super(application);
        repository = new RestaurantRepository();
        restaurants = new ArrayList<>();
    }

    public void insertFavorite(String restaurant_id, String user_id) { repository.insertFavorite(restaurant_id, user_id); }

    public void fetchRestaurants(double latitude, double longitude, int limit, int offset, int radius, String prices) { repository.fetchRestaurants(latitude, longitude, limit, offset, radius, prices); }

    public void deleteFavorite(String id) { repository.deleteFavorite(id); }

    public void getFavorites() { repository.getFavorites(); }

    public void getRestaurantDetails(String restaurant_id) {repository.getRestaurantDetails(restaurant_id);}

    public void getRestaurantReviews(String restaurant_id) {repository.getRestaurantReviews(restaurant_id);}

    public void setFetchRestaurantListener(RestaurantRepository.FetchRestaurantsListener listener) { repository.setFetchRestaurantListener(listener); }

    public void setFetchFavoritesListener(RestaurantRepository.FetchFavoritesListener listener) { repository.setFetchFavoritesListener(listener); }

    public void setDeleteFavoriteListener(RestaurantRepository.DeleteFavoriteListener listener) { repository.setDeleteFavoritesListener(listener); }

    public void setRestaurantDetailsListener(RestaurantRepository.RestaurantDetailsListener listener) { repository.setRestaurantDetailsListener(listener);}

    public void setReviewsListener(RestaurantRepository.ReviewsListener listener) { repository.setReviewsListener(listener); }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setTopPosition(int topPosition) {
        this.topPosition = topPosition;
    }

    public void setRadius(int radius) {
        if(this.radius == null) {
            this.radius = new MutableLiveData<>();
        }
        this.radius.setValue(radius);
    }

    public void setPrices(String prices) {
        if(this.prices == null) {
            this.prices = new MutableLiveData<>();
        }
        this.prices.setValue(prices);
    }

    public void setLatitude(Double latitude) {
        if(this.latitude == null) {
            this.latitude = new MutableLiveData<>();
        }
        this.latitude.setValue(latitude);
    }

    public void setLongitude(Double longitude) {
        if(this.longitude == null) {
            this.longitude = new MutableLiveData<>();
        }
        this.longitude.setValue(longitude);
    }


    public int getOffset() {
        return offset;
    }

    public int getTopPosition() {
        return topPosition;
    }

    public MutableLiveData<Integer> getRadius() {
        if(radius == null) {
            radius = new MutableLiveData<>();
            radius.setValue(10);
        }
        return radius;
    }

    public MutableLiveData<String> getPrices() {
        if(prices == null) {
            this.prices = new MutableLiveData<>();
            this.prices.setValue(ParseUser.getCurrentUser().getString("prices"));
        }
        return prices;
    }

    public MutableLiveData<Double> getLatitude() {
        if(latitude == null) {
            latitude = new MutableLiveData<>();
            latitude.setValue(ParseUser.getCurrentUser().getDouble("latitude"));
        }
        return latitude;
    }

    public MutableLiveData<Double> getLongitude() {
        if(longitude == null) {
            longitude = new MutableLiveData<>();
            longitude.setValue(ParseUser.getCurrentUser().getDouble("longitude"));
        }
        return longitude;
    }

    public void addAllRestaurants(List<Restaurant> restaurants) {
        this.restaurants.addAll(restaurants);
    }

    public void clearRestaurants() {
        this.restaurants.clear();
    }

    public ArrayList<Restaurant> getRestaurants() {
        return this.restaurants;
    }
}