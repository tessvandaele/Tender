package com.codepath.tender.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereEqualTo(USERNAME_KEY, getUsername());
        try {
            return query.getFirst().getParseFile(PROFILE_IMAGE_KEY).getUrl();
        } catch (ParseException exception) {
            exception.printStackTrace();
        }
        return "";
    }

    public void setUsername(String userId) {
        put(USERNAME_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }
}
