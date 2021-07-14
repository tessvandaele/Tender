package com.codepath.tender;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.tender.models.Restaurant;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {

    private ImageView image;
    private TextView name;
    private TextView distance;
    private RatingBar rating;

    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        image = findViewById(R.id.ivImageDetails);
        name = findViewById(R.id.tvNameDetails);
        distance = findViewById(R.id.tvDistanceDetails);
        rating = findViewById(R.id.rbRatingDetails);

        restaurant = (Restaurant) Parcels.unwrap(getIntent().getParcelableExtra(Restaurant.class.getSimpleName()));

        name.setText(restaurant.getName());
        distance.setText(restaurant.getDisplayDistance());
        rating.setRating(restaurant.getRating());

        Glide.with(this)
                .load(restaurant.getImage_url())
                .centerCrop()
                .into(image);
    }
}