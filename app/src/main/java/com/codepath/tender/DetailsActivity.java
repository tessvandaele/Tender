package com.codepath.tender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.bumptech.glide.Glide;
import com.codepath.tender.adapters.ReviewAdapter;
import com.codepath.tender.models.Restaurant;
import com.codepath.tender.models.Review;
import com.codepath.tender.models.YelpReviewResult;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* user can view a detailed screen of restaurant information */

public class DetailsActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.yelp.com/v3/";
    private static final String API_KEY = "GrsRS-QAb3mRuvqWsTPW5Bye4DAJ1TJY9v5addUNFFIhpb-iL8DwR0NJ_y-hOWIc94vW7wpIYZc3HRU7NQyAf0PQ0vsSddtF1qnNXlebmvey-5Vq6myMcfFgYJrtYHYx";
    private static  final String RESTAURANT_INTENT_KEY = "restaurant_id";

    ArrayList<Review> reviews;

    //defining views
    private ImageView image;
    private TextView name;
    private TextView distance;
    private TextView rating;
    private TextView price;
    private TextView location;
    private RecyclerView rvReviews;
    private RecyclerViewHeader header;

    private ReviewAdapter adapter;

    private String restaurant_id;
    private YelpService yelpService;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        reviews = new ArrayList<>();

        image = findViewById(R.id.ivImageDetails);
        name = findViewById(R.id.tvNameDetails);
        location = findViewById(R.id.tvAddressDetails);
        rating = findViewById(R.id.tvRatingDetails);
        distance = findViewById(R.id.tvDistanceDetails);
        price = findViewById(R.id.tvPriceDetails);
        rvReviews = findViewById(R.id.rvReviews);
        header = findViewById(R.id.header);

        //recycler view set up
        adapter = new ReviewAdapter(this, reviews);
        rvReviews.setAdapter(adapter);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        header.attachTo(rvReviews);

        bindData();
    }

    //retrieves the correct restaurant based on restaurant name
    public void bindData() {
        restaurant_id = getIntent().getStringExtra(RESTAURANT_INTENT_KEY);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        yelpService = retrofit.create(YelpService.class);

        yelpService.getRestaurantDetails("Bearer " + API_KEY, restaurant_id).enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                Restaurant restaurant = response.body();
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
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                Log.d("Favorites fragment", "Could not retrieve restaurant");
            }
        });

        yelpService.getRestaurantReviews("Bearer " + API_KEY, restaurant_id).enqueue(new Callback<YelpReviewResult>() {
            @Override
            public void onResponse(Call<YelpReviewResult> call, Response<YelpReviewResult> response) {
                YelpReviewResult searchResult = response.body();
                if(searchResult == null){
                    Log.e("View model", "No restaurants retrieved");
                    return;
                }
                //sends the list of restaurants to the swipe fragment through a listener
                reviews.addAll(searchResult.reviews);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<YelpReviewResult> call, Throwable t) {

            }
        });
    }
}