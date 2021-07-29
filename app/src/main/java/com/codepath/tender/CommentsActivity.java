package com.codepath.tender;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

import static com.codepath.tender.Constants.COMMENTS_INTENT_KEY;

public class CommentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ArrayList<String> usernames = getIntent().getStringArrayListExtra(COMMENTS_INTENT_KEY);

    }


}