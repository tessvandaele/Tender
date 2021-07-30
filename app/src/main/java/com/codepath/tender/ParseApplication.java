package com.codepath.tender;

/* connects to the backend to access and edit user data */

import android.app.Application;

import com.codepath.tender.models.Comment;
import com.codepath.tender.models.Restaurant;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //register our parse models
        ParseObject.registerSubclass(Restaurant.class);
        ParseObject.registerSubclass(Comment.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("CnDW4tVgE6ByWBz3m83vMXHY8Lz4GDqSJ9lb4ame")
                .clientKey("h5phc5tO6UJz2VMbM1vui3Uy5W3nwEHMStDutePq")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}