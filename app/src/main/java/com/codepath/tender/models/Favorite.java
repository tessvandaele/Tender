package com.codepath.tender.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Favorite")
public class Favorite extends ParseObject {

    public static final String USER_ID_KEY = "userId";
    public static final String RESTAURANT_ID_KEY = "restaurantId";

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
