package com.codepath.tender;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.codepath.tender.models.Restaurant;

import java.util.List;

/* Repository class provides a clean API for data access to the rest of the application */

class RestaurantRepository {

    private RestaurantDAO mRestaurantDao;
    private LiveData<List<Restaurant>> mRestaurants;

    RestaurantRepository(Application application) {
        RestaurantRoomDatabase db = RestaurantRoomDatabase.getDatabase(application);
        mRestaurantDao = db.restaurantDAO();
        mRestaurants = mRestaurantDao.getAlphabetizedWords();
    }

    LiveData<List<Restaurant>> getAllRestaurants() {
        return mRestaurants;
    }

    void insert(Restaurant restaurant) {
        RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> {
            mRestaurantDao.insert(restaurant);
        });
    }

    void delete(String name) {
        RestaurantRoomDatabase.databaseWriteExecutor.execute(() -> {
            mRestaurantDao.delete(name);
        });
    }
}
