package com.codepath.tender;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.codepath.tender.models.Restaurant;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.List;

import static com.parse.ParseObject.saveAllInBackground;

/* Repository class provides a clean API for data access to the rest of the application */

class RestaurantRepository {

    public RestaurantRepository() {}

    void insert(Restaurant restaurant) {
        //saving to backend
        restaurant.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e("SwipeFragment", "Error saving post", e);
                    return;
                }
            }
        });
    }
}
