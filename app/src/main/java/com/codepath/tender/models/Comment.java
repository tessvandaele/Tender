package com.codepath.tender.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import static com.codepath.tender.Constants.PROFILE_IMAGE_KEY;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String USERNAME_KEY = "username";
    public static final String BODY_KEY = "body";

    public Comment() {}

    public String getUsername() {
        return getString(USERNAME_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public String getProfile_image() {
        return getString(PROFILE_IMAGE_KEY);
    }

    public void setUsername(String userId) {
        put(USERNAME_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public void setProfile_image(String profile_image) {
        put(PROFILE_IMAGE_KEY, profile_image);
    }
}
