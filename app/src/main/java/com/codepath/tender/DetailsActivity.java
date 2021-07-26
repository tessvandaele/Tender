package com.codepath.tender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.bumptech.glide.Glide;
import com.codepath.tender.adapters.ReviewAdapter;
import com.codepath.tender.models.Restaurant;
import com.codepath.tender.models.Review;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.codepath.tender.Constants.RESTAURANT_INTENT_KEY;

/* user can view a detailed screen of restaurant information */

public class DetailsActivity extends AppCompatActivity {

    ArrayList<Review> reviews;
    ArrayList<String> favorite_usernames;

    //defining views
    private ImageView image;
    private TextView name;
    private TextView distance;
    private TextView rating;
    private TextView price;
    private TextView location;
    private TextView other_users;
    private ImageButton all_reviews_link;
    private RecyclerView rvReviews;
    private RecyclerViewHeader header;
    private Button open_maps;

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
        favorite_usernames = new ArrayList<>();

        model = new ViewModelProvider(this).get(RestaurantViewModel.class);

        image = findViewById(R.id.ivImageDetails);
        name = findViewById(R.id.tvNameDetails);
        location = findViewById(R.id.tvAddressDetails);
        rating = findViewById(R.id.tvRatingDetails);
        distance = findViewById(R.id.tvDistanceDetails);
        price = findViewById(R.id.tvPriceDetails);
        other_users = findViewById(R.id.tvOtherUsers);
        all_reviews_link = findViewById(R.id.btnAllReviewsLink);
        rvReviews = findViewById(R.id.rvReviews);
        header = findViewById(R.id.header);
        open_maps = findViewById(R.id.btnOpenMaps);

        //recycler view set up
        adapter = new ReviewAdapter(this, reviews);
        rvReviews.setAdapter(adapter);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        header.attachTo(rvReviews);

        setListeners();
        setupReviewLinkButton();
        setOpenMapsButton();
        bindData();
    }

    //retrieves the correct restaurant based on restaurant name
    public void bindData() {
        restaurant_id = getIntent().getStringExtra(RESTAURANT_INTENT_KEY);
        model.getRestaurantDetails(restaurant_id);
        model.getRestaurantReviews(restaurant_id);
        model.getOtherUsersWithFavorite(restaurant_id);
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
                mapService.setMarker(restaurant.getCoordinates().getLatitude(), restaurant.getCoordinates().getLongitude(), restaurant.getName());
            }
        });

        model.setReviewsListener(new RestaurantRepository.ReviewsListener() {
            @Override
            public void onFinishReviewsFetch(List<Review> new_reviews) {
                reviews.addAll(new_reviews);
                adapter.notifyDataSetChanged();
            }
        });

        model.setOtherUsersListener(new RestaurantRepository.OtherUsersListener() {
            @Override
            public void onFinishOtherUserFetch(ParseUser user) {
                if(user != null && !user.getUsername().equals(ParseUser.getCurrentUser().getUsername())){
                    favorite_usernames.add(user.getUsername());
                    setOtherUserText();
                }

            }
        });
    }

    public void setupReviewLinkButton() {
        all_reviews_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(yelp_url));
                startActivity(browserIntent);
            }
        });
    }

    //redirects user to google maps for directions to restaurant location
    public void setOpenMapsButton() {
        open_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapService.openGoogleMapsDialog();
            }
        });
    }

    //helper method to set text according to how many other users liked the restaurant
    public void setOtherUserText() {
        String liked_by = "Liked by ";

        if(favorite_usernames.size() == 0 | favorite_usernames == null) {
            //no other users; keep text empty
        } else if(favorite_usernames.size() == 1) {
            String username = favorite_usernames.get(0);
            String final_string = liked_by + username;

            //making certain parts of text bold
            Spannable sb = new SpannableString(final_string);
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 9, final_string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            other_users.setText(sb, TextView.BufferType.SPANNABLE);
        } else if(favorite_usernames.size() == 2) {
            String username = favorite_usernames.get(0);
            String final_string = liked_by + username + " and 1 other";

            //making certain parts of text bold
            Spannable sb = new SpannableString(final_string);
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 9, 9+username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 14+username.length(), final_string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            other_users.setText(sb, TextView.BufferType.SPANNABLE);
        } else {
            String username = favorite_usernames.get(0);
            String final_string = liked_by + username + " and " + (favorite_usernames.size()-1) + " others";

            //making certain parts of text bold
            Spannable sb = new SpannableString(final_string);
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 9, 9+username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 14+username.length(), final_string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            other_users.setText(sb, TextView.BufferType.SPANNABLE);
        }
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