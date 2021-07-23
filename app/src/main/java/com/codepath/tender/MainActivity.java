package com.codepath.tender;

import android.os.Bundle;
import android.view.MenuItem;

import com.codepath.tender.fragments.FavoritesFragment;
import com.codepath.tender.fragments.ProfileFragment;
import com.codepath.tender.fragments.SwipeFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

/* handles the bottom navigation menu */

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    final static int PERMISSION_ID = 44;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private RestaurantViewModel model;
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new ViewModelProvider(this).get(RestaurantViewModel.class);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
                refreshRestaurants();
            }
        });

        //observer on price data to refresh the restaurant deck when user changes price preference
        model.getPrices().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String string) {
                refreshRestaurants();
            }
        });

        model.getLatitude().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                refreshRestaurants();
            }
        });

        model.getLongitude().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                refreshRestaurants();
            }
        });

        locationService = new LocationService(this, mFusedLocationClient, model);
        locationService.getLastLocation();
    }

    //helper method for when the deck of restaurants needs to be refloaded
    public void refreshRestaurants() {
        //reset offset and restaurants list
        model.setOffset(0);
        model.clearRestaurants();
        //fetch restaurants
        double latitude = model.getLatitude().getValue();
        double longitude = model.getLongitude().getValue();
        int limit = 30;
        int radius = model.getRadius().getValue() * 1609;
        String prices = model.getPrices().getValue();
        model.fetchRestaurants(latitude, longitude, limit, model.getOffset(), radius, prices);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationService.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        locationService.onResume();
    }
}