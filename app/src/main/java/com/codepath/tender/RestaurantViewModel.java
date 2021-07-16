package com.codepath.tender;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.codepath.tender.models.Restaurant;

import java.util.List;

/* View model that provides data to the UI and survives configuration changes between fragments/activities */

public class RestaurantViewModel extends AndroidViewModel {

    RestaurantRepository repository;
    private int topPosition;
    private int offset;

    public RestaurantViewModel (Application application) {
        super(application);
        repository = new RestaurantRepository();
    }

    public void insert(Restaurant restaurant) { repository.insert(restaurant); }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setTopPosition(int topPosition) {
        this.topPosition = topPosition;
    }

    public int getOffset() {
        return offset;
    }

    public int getTopPosition() {
        return topPosition;
    }
}