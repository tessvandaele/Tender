package com.codepath.tender;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.tender.models.Restaurant;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/* user can view a detailed screen of restaurant information */

public class DetailsActivity extends AppCompatActivity {

    //defining views
    private ImageView image;
    private TextView name;
    private TextView distance;
    private RatingBar rating;
    private TextView review_count;
    private TextView price;
    private String restaurant_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        image = findViewById(R.id.ivImageDetails);
        name = findViewById(R.id.tvNameDetails);
        distance = findViewById(R.id.tvDistanceDetails);
        rating = findViewById(R.id.rbRatingDetails);
        review_count = findViewById(R.id.tvReviewCountDetails);
        price = findViewById(R.id.tvPriceDetails);

        bindData();
    }

    //retrieves the correct restaurant based on restaurant name
    public void bindData() {
        restaurant_name = getIntent().getStringExtra("name");
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Restaurant");
        query.whereEqualTo("name", restaurant_name);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                Restaurant restaurant = (Restaurant) object;
                name.setText(restaurant.getName());
                distance.setText(restaurant.getDisplayDistance());
                rating.setRating((float) restaurant.getRating());
                review_count.setText(restaurant.getReview_count() + " reviews");
                price.setText(restaurant.getPrice());

                Glide.with(DetailsActivity.this)
                        .load(restaurant.getImage_url())
                        .centerCrop()
                        .into(image);
            }
        });
    }
}