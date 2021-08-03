package com.codepath.tender;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

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
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

/* handles the bottom navigation menu */

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    final static int PERMISSION_ID = 44;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private RestaurantViewModel restaurantViewModel;
    private UserViewModel userViewModel;
    private LocationService locationService;
    private RelativeLayout overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instructions overlay
        overlay = findViewById(R.id.rlOverlay);
        overlay.setVisibility(View.INVISIBLE);
        Boolean login = getIntent().getBooleanExtra("login_user", false);

        if(login) {
            setOverlayButton();
            overlay.setVisibility(View.VISIBLE);
        }

        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

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

        //observer on user filter data to refresh the restaurant deck
        MediatorLiveData liveDataMerger = new MediatorLiveData<>();
        liveDataMerger.addSource(userViewModel.getRadius(), value -> liveDataMerger.setValue(value));
        liveDataMerger.addSource(userViewModel.getPrices(), value -> liveDataMerger.setValue(value));
        liveDataMerger.addSource(userViewModel.getLatitude(), value -> liveDataMerger.setValue(value));
        liveDataMerger.addSource(userViewModel.getLongitude(), value -> liveDataMerger.setValue(value));
        liveDataMerger.addSource(userViewModel.getCategories(), value -> liveDataMerger.setValue(value));
        liveDataMerger.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                refreshRestaurants();
            }
        });


        locationService = new LocationService(this, mFusedLocationClient, userViewModel);
        locationService.getLastLocation();


    }

    //helper method for when the deck of restaurants needs to be refloaded
    public void refreshRestaurants() {
        //reset offset and restaurants list
        restaurantViewModel.setOffset(30);
        restaurantViewModel.setTopPosition(0);
        //fetch restaurants
        double latitude = userViewModel.getLatitude().getValue();
        double longitude = userViewModel.getLongitude().getValue();
        int radius = userViewModel.getRadius().getValue() * 1609;
        String prices = userViewModel.getPrices().getValue();
        String categories = userViewModel.getCategories().getValue();
        restaurantViewModel.fetchRestaurants(latitude, longitude, 30, 0, radius, prices, categories, "best_match");
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

    public void setOverlayButton() {
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setVisibility(View.GONE);
            }
        });
    }


}