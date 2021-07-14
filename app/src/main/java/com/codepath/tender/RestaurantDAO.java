package com.codepath.tender;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.tender.models.Restaurant;

import java.util.List;

/* DAO (data access object) to represent our database operations */

@Dao
public interface RestaurantDAO {

    //OnConflictStrategy allows the same name to be inserted multiple times
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Restaurant restaurant);

    @Query("DELETE FROM restaurant_table")
    void deleteAll();

    @Query("SELECT * FROM restaurant_table ORDER BY name ASC")
    LiveData<List<Restaurant>> getAlphabetizedWords();
}
