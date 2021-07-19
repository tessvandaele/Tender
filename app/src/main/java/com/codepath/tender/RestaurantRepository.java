package com.codepath.tender;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.codepath.tender.models.Favorite;
import com.codepath.tender.models.Restaurant;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static com.parse.ParseObject.saveAllInBackground;

/* Repository class provides a clean API for data access to the rest of the application */

class RestaurantRepository {

    public RestaurantRepository() {}

    void insertFavorite(String restaurant_id, String user_id) {
        ParseObject favorite = new ParseObject("Favorite");
        favorite.put(Favorite.RESTAURANT_ID_KEY, restaurant_id);
        favorite.put(Favorite.USER_ID_KEY, user_id);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Favorite");
        query.whereEqualTo("userId", user_id);
        query.whereEqualTo("restaurantId", restaurant_id);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(objects.size() == 0) { //favorite does not already exist
                    favorite.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null) {
                                Log.e("SwipeFragment", "Error saving restaurant", e);
                                return;
                            }
                        }
                    });
                }
            }
        });
    }
}
