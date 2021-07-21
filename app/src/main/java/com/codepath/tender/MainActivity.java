package com.codepath.tender;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.codepath.tender.fragments.FavoritesFragment;
import com.codepath.tender.fragments.ProfileFragment;
import com.codepath.tender.fragments.SwipeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

/* handles the bottom navigation menu */

public class MainActivity extends AppCompatActivity {

    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String PRICES_KEY = "prices";

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private RestaurantViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new ViewModelProvider(this).get(RestaurantViewModel.class);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //sets up the bottom navigation menu such that the correct fragments are displayed
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_favorites: //list fragment
                        fragment = new FavoritesFragment();
                        break;
                    case R.id.navigation_swipe: //swipe fragment
                        fragment = new SwipeFragment();
                        break;
                    case R.id.navigation_profile: //profile fragment
                        fragment = new ProfileFragment();
                        break;
                    default:
                        fragment = new SwipeFragment();
                        break;
                }
                //replaced the contained with the correct fragment
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        //set default fragment selection
        bottomNavigationView.setSelectedItemId(R.id.navigation_swipe);

        //observer on radius data to refresh the restaurant deck when user changes radius preference
        model.getRadius().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                //reset offset and restaurants list
                model.setOffset(0);
                model.clearRestaurants();
                //fetch restaurants
                double latitude = ParseUser.getCurrentUser().getDouble(LATITUDE_KEY);
                double longitude = ParseUser.getCurrentUser().getDouble(LONGITUDE_KEY);
                int limit = 30;
                int radius = model.getRadius().getValue() * 1609;
                String prices = model.getPrices().getValue();
                model.fetchRestaurants(latitude, longitude, limit, model.getOffset(), radius, prices);
            }
        });

        //observer on price data to refresh the restaurant deck when user changes price preference
        model.getPrices().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String string) {
                //reset offset and restaurants list
                model.setOffset(0);
                model.clearRestaurants();
                //fetch restaurants
                double latitude = ParseUser.getCurrentUser().getDouble(LATITUDE_KEY);
                double longitude = ParseUser.getCurrentUser().getDouble(LONGITUDE_KEY);
                int limit = 30;
                int radius = model.getRadius().getValue() * 1609;
                String prices = model.getPrices().getValue();
                model.fetchRestaurants(latitude, longitude, limit, model.getOffset(), radius, prices);
            }
        });
    }
}