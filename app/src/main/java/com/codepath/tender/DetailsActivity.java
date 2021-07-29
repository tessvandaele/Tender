package com.codepath.tender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.codepath.tender.models.Open;
import com.codepath.tender.models.Restaurant;
import com.codepath.tender.models.Review;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.codepath.tender.Constants.COMMENTS_INTENT_KEY;
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
    private TextView hours;
    private ImageButton comments;


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
        hours = findViewById(R.id.tvHoursDetails);
        rvReviews = findViewById(R.id.rvReviews);
        header = findViewById(R.id.header);
        open_maps = findViewById(R.id.btnOpenMaps);
        comments = findViewById(R.id.ibComments);

        //recycler view set up
        adapter = new ReviewAdapter(this, reviews);
        rvReviews.setAdapter(adapter);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        header.attachTo(rvReviews);

        setListeners();
        setButtons();
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
                try {
                    setTodaysHours(restaurant);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
            public void onFinishOtherUserFetch(List<ParseUser> users) {
                for(int i = 0; i<users.size(); i++) {
                    ParseUser user = users.get(i);
                    if(user != null && !user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                        favorite_usernames.add(user.getUsername());
                    }
                }
                setOtherUserText();
            }
        });
    }

    public void setButtons() {
        setupReviewLinkButton();
        setOpenMapsButton();
        setOtherUsersDialogButton();
        setCommentsButton();
    }

    private void setCommentsButton() {
        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, CommentsActivity.class);
                intent.putStringArrayListExtra(COMMENTS_INTENT_KEY, favorite_usernames);
                DetailsActivity.this.startActivity(intent);
            }
        });
    }

    public void setOtherUsersDialogButton() {
        other_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOtherUserDialog();
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

    public void setTodaysHours(Restaurant restaurant) throws ParseException {
        //calculate todays week day as int: mon = 0, tue = 1, etc
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2;
        if(today == -1) today = 6;
        for(int i = 0; i<restaurant.getHours()[0].getOpen().length; i++){
            Open open = restaurant.getHours()[0].getOpen()[i];
            if(open.getDay() == today) {
                hours.setText(getDisplayTime(open.getStart()) + " - " + getDisplayTime(open.getEnd()));
            }
        }
    }

    //coverts 4-digit military time to standard time with AM/PM
    public String getDisplayTime(String time) throws ParseException {
        DateFormat originalFormat = new SimpleDateFormat("HHmm");
        DateFormat targetFormat = new SimpleDateFormat("hh:mm a");
        Date date = originalFormat.parse(time);
        String formattedDate = targetFormat.format(date);  // 20120821
        return formattedDate;
    }

    public void openOtherUserDialog() {
        String users = "";
        for(int i = 0; i<favorite_usernames.size(); i++) {
            users += favorite_usernames.get(i) + "\n";
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);

        //opening dialog with users that have favorites restaurant
        builder.setMessage(users)
                .setCancelable(true)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() { //no
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //close dialog
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

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