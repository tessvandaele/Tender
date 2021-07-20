package com.codepath.tender;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.tender.models.Restaurant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* user can view a detailed screen of restaurant information */

public class DetailsActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.yelp.com/v3/";
    private static final String API_KEY = "GrsRS-QAb3mRuvqWsTPW5Bye4DAJ1TJY9v5addUNFFIhpb-iL8DwR0NJ_y-hOWIc94vW7wpIYZc3HRU7NQyAf0PQ0vsSddtF1qnNXlebmvey-5Vq6myMcfFgYJrtYHYx";

    //defining views
    private ImageView image;
    private TextView name;
    private TextView distance;
    private TextView rating;
    private TextView price;
    private TextView location;

    private String restaurant_id;
    private YelpService yelpService;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        image = findViewById(R.id.ivImageDetails);
        name = findViewById(R.id.tvNameDetails);
        location = findViewById(R.id.tvAddressDetails);
        rating = findViewById(R.id.tvRatingDetails);
        distance = findViewById(R.id.tvDistanceDetails);
        price = findViewById(R.id.tvPriceDetails);

        bindData();
    }

    //retrieves the correct restaurant based on restaurant name
    public void bindData() {
        restaurant_id = getIntent().getStringExtra("restaurant_id");
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
    }
}