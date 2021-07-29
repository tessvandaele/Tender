package com.codepath.tender.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String USERNAME_KEY = "username";
    public static final String BODY_KEY = "body";

    public String getUsername() {
        return getString(USERNAME_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setUsername(String userId) {
        put(USERNAME_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }
}
