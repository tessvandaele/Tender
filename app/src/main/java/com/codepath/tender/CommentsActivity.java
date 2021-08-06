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

/* displays a list of the comments of the current restaurant */

public class CommentsActivity extends AppCompatActivity {

    EditText userComment;
    ImageButton sendBtn;
    ImageButton back;
    RecyclerView rvComments;
    List<Comment> commentsList;
    Boolean firstLoad;
    CommentsAdapter adapter;
    String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        //getting restaurant id from intent
        restaurantId = getIntent().getStringExtra(COMMENTS_INTENT_KEY);

        //assigning views
        userComment = findViewById(R.id.etMessage);
        sendBtn = findViewById(R.id.ibSend);
        back = findViewById(R.id.ibBackComments);
        rvComments = findViewById(R.id.rvComments);

        commentsList = new ArrayList<>();
        firstLoad = true;

        //setting up recycler view with adapter and layout manager
        adapter = new CommentsAdapter(CommentsActivity.this, commentsList);
        rvComments.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);

        setSendButton();
        setBackButton();
        refreshMessages();
    }

    //logic for publishing a comment
    private void setSendButton() {
        // When send button is clicked, create comment object on Parse
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = userComment.getText().toString();

                // Using new `Message.java` Parse-backed model now
                ParseObject comment = ParseObject.create(COMMENT_TABLE_KEY);
                comment.put(USERNAME_KEY, ParseUser.getCurrentUser().getUsername());
                comment.put(BODY_KEY, data);
                comment.put(RESTAURANT_ID_KEY, restaurantId);

                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        //refresh the list of comments
                        refreshMessages();
                    }
                });
                userComment.setText(null);
            }
        });
    }

    //sets up button to navigate back to details page
    private void setBackButton() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        // Construct query to execute
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // Configure limit and sort order
        query.setLimit(MAX_COMMENTS_TO_DISPLAY);
        query.whereEqualTo(RESTAURANT_ID_KEY, restaurantId);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Comment>() {
            public void done(List<Comment> comments, ParseException e) {
                if (e == null) {
                    commentsList.clear();
                    commentsList.addAll(comments);
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