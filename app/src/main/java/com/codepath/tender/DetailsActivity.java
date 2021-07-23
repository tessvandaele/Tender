package com.codepath.tender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.bumptech.glide.Glide;
import com.codepath.tender.adapters.ReviewAdapter;
import com.codepath.tender.models.Restaurant;
import com.codepath.tender.models.Review;

import java.util.ArrayList;
import java.util.List;

import static com.codepath.tender.Constants.RESTAURANT_INTENT_KEY;

/* user can view a detailed screen of restaurant information */

public class DetailsActivity extends AppCompatActivity {

    ArrayList<Review> reviews;

    //defining views
    private ImageView image;
    private TextView name;
    private TextView distance;
    private TextView rating;
    private TextView price;
    private TextView location;
    private ImageButton all_reviews_link;
    private RecyclerView rvReviews;
    private RecyclerViewHeader header;

    private ReviewAdapter adapter;
    private RestaurantViewModel model;

    private String restaurant_id;
    private String yelp_url;

    private MapService mapService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mapService = new MapService(DetailsActivity.this, findViewById(R.id.user_list_map));
        mapService.createMap(savedInstanceState);

        reviews = new ArrayList<>();

        model = new ViewModelProvider(this).get(RestaurantViewModel.class);

        image = findViewById(R.id.ivImageDetails);
        name = findViewById(R.id.tvNameDetails);
        location = findViewById(R.id.tvAddressDetails);
        rating = findViewById(R.id.tvRatingDetails);
        distance = findViewById(R.id.tvDistanceDetails);
        price = findViewById(R.id.tvPriceDetails);
        all_reviews_link = findViewById(R.id.btnAllReviewsLink);
        rvReviews = findViewById(R.id.rvReviews);
        header = findViewById(R.id.header);

        //recycler view set up
        adapter = new ReviewAdapter(this, reviews);
        rvReviews.setAdapter(adapter);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        header.attachTo(rvReviews);

        setListeners();
        setupLinkButton();
        bindData();
    }

    //retrieves the correct restaurant based on restaurant name
    public void bindData() {
        restaurant_id = getIntent().getStringExtra(RESTAURANT_INTENT_KEY);
        model.getRestaurantDetails(restaurant_id);
        model.getRestaurantReviews(restaurant_id);
    }

    public void setListeners() {
        model.setRestaurantDetailsListener(new RestaurantRepository.RestaurantDetailsListener() {
            @Override
            public void onFinishDetailsFetch(Restaurant restaurant) {
                name.setText(restaurant.getName());
                rating.setText(Double.toString(restaurant.getRating()));
                distance.setText(restaurant.getDisplayDistance());
                location.setText(restaurant.getLocation().getDisplay_address()[0] + ", " + restaurant.getLocation().getDisplay_address()[1]);
                if(restaurant.getLocation().getDisplay_address().length > 2) {
                    location.setText(restaurant.getLocation().getDisplay_address()[0] + ", " + restaurant.getLocation().getDisplay_address()[1] + ", " + restaurant.getLocation().getDisplay_address()[2]);
                }
                price.setText(restaurant.getPrice());
                Glide.with(DetailsActivity.this)
                        .load(restaurant.getImage_url())
                        .centerCrop()
                        .into(image);

                yelp_url = restaurant.getUrl();
                mapService.setMarker(restaurant.getCoordinates().getLatitude(), restaurant.getCoordinates().getLongitude());
            }
        });

        model.setReviewsListener(new RestaurantRepository.ReviewsListener() {
            @Override
            public void onFinishReviewsFetch(List<Review> new_reviews) {
                reviews.addAll(new_reviews);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void setupLinkButton() {
        all_reviews_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(yelp_url));
                startActivity(browserIntent);
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapService.onSaveInstance(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapService.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapService.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapService.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapService.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapService.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapService.onLowMemory();
    }

}