package com.codepath.tender;

import android.os.Bundle;
import android.view.MenuItem;

import com.codepath.tender.fragments.FavoritesFragment;
import com.codepath.tender.fragments.ProfileFragment;
import com.codepath.tender.fragments.SwipeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

}