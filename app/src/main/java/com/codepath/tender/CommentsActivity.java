package com.codepath.tender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.tender.adapters.CommentsAdapter;
import com.codepath.tender.models.Comment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static com.codepath.tender.Constants.COMMENTS_INTENT_KEY;
import static com.codepath.tender.Constants.COMMENT_TABLE_KEY;
import static com.codepath.tender.Constants.MAX_COMMENTS_TO_DISPLAY;
import static com.codepath.tender.Constants.PROFILE_IMAGE_KEY;
import static com.codepath.tender.Constants.RESTAURANT_ID_KEY;
import static com.codepath.tender.models.Comment.BODY_KEY;
import static com.codepath.tender.models.Comment.USERNAME_KEY;

public class CommentsActivity extends AppCompatActivity {

    EditText user_comment;
    ImageButton send_btn;
    RecyclerView rv_comments;
    List<Comment> comments_list;
    Boolean firstLoad;
    CommentsAdapter adapter;

    String restaurant_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        restaurant_id = getIntent().getStringExtra(COMMENTS_INTENT_KEY);

        //assigning views
        user_comment = findViewById(R.id.etMessage);
        send_btn = findViewById(R.id.ibSend);
        rv_comments = findViewById(R.id.rvComments);
        comments_list = new ArrayList<>();
        firstLoad = true;

        //setting up recycler view with adapter and layout manager
        String userId = ParseUser.getCurrentUser().getObjectId();
        adapter = new CommentsAdapter(CommentsActivity.this, userId, comments_list);
        rv_comments.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_comments.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);

        // When send button is clicked, create message object on Parse
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = user_comment.getText().toString();
                Toast.makeText(CommentsActivity.this, data, Toast.LENGTH_SHORT).show();

                // Using new `Message.java` Parse-backed model now
                ParseObject comment = ParseObject.create(COMMENT_TABLE_KEY);
                comment.put(USERNAME_KEY, ParseUser.getCurrentUser().getUsername());
                comment.put(BODY_KEY, data);
                comment.put(RESTAURANT_ID_KEY, restaurant_id);
                comment.put(PROFILE_IMAGE_KEY, ParseUser.getCurrentUser().getString(PROFILE_IMAGE_KEY));

                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        refreshMessages();
                    }
                });
                user_comment.setText(null);
            }
        });

        refreshMessages();
    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        // Construct query to execute
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // Configure limit and sort order
        query.setLimit(MAX_COMMENTS_TO_DISPLAY);
        query.whereEqualTo(RESTAURANT_ID_KEY, restaurant_id);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Comment>() {
            public void done(List<Comment> comments, ParseException e) {
                if (e == null) {
                    comments_list.clear();
                    comments_list.addAll(comments);
                    adapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (firstLoad) {
                        //rvChat.scrollToPosition(0);
                        firstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }


}