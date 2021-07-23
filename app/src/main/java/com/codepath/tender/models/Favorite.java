package com.codepath.tender.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import static com.codepath.tender.Constants.RESTAURANT_ID_KEY;
import static com.codepath.tender.Constants.USER_ID_KEY;

@ParseClassName("Favorite")
public class Favorite extends ParseObject {

    public Favorite() {}

    public String getUserId(){return getString(USER_ID_KEY);}

    public String getRestaurantId() {
        return getString(RESTAURANT_ID_KEY);
    }

    public void setUserId(String user_id){ put(USER_ID_KEY, user_id);}

    public void setRestaurantId(String restaurant_id) {
        put(RESTAURANT_ID_KEY, restaurant_id);
    }

}
