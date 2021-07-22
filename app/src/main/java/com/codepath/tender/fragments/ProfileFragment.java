package com.codepath.tender.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.codepath.tender.LoginActivity;
import com.codepath.tender.R;
import com.codepath.tender.RestaurantViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/* user can logout of account and view profile */

public class ProfileFragment extends Fragment {

    private static final String RADIUS_KEY = "radius";
    private static final String PRICES_KEY = "prices";

    private ImageButton ibLogout;
    private TextView tvUsername;
    private TextView tvLocation;
    private SeekBar barRadius;
    private TextView tvRadius;
    private ChipGroup priceChips;

    private boolean[] prices;

    private RestaurantViewModel model;

    //empty constructor
    public ProfileFragment() {}

    //inflate layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        prices = new boolean[] {false, false, false, false};

        ibLogout = view.findViewById(R.id.ibLogout);
        tvUsername = view.findViewById(R.id.tvProfileUsername);
        tvLocation = view.findViewById(R.id.tvLocation);
        barRadius = view.findViewById(R.id.barRadius);
        tvRadius = view.findViewById(R.id.tvRadius);
        priceChips = view.findViewById(R.id.priceChips);

        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvRadius.setText(Integer.toString(ParseUser.getCurrentUser().getInt(RADIUS_KEY)));
        barRadius.setProgress(ParseUser.getCurrentUser().getInt(RADIUS_KEY));
        tvLocation.setText(ParseUser.getCurrentUser().getDouble("latitude") + " | " + ParseUser.getCurrentUser().getDouble("longitude"));

        model = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);

        setLogout();
        setChips();
        setStatusBarListener();
        setChipListener();
    }

    //sets up the log out button to allow user to log out
    public void setLogout() {
        ibLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //log out user account
                ParseUser.logOut();

                //create intent back to login page
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    //sets the price chips to the correct setting based on user preference
    public void setChips() {
        List<String> user_prices = Arrays.asList(ParseUser.getCurrentUser().getString(PRICES_KEY).split(", "));
        //initialize price chips
        for(int i = 0; i<user_prices.size(); i++){
            int price = Integer.parseInt(user_prices.get(i));
            Chip chip = (Chip) priceChips.getChildAt(price-1);
            prices[price-1] = true;
            chip.setChecked(true);
        }
    }

    //sets up the status bar listener
    public void setStatusBarListener() {
        barRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRadius.setText(progress + " mi");
                model.setRadius(progress);
                ParseUser.getCurrentUser().put(RADIUS_KEY, progress);
                ParseUser.getCurrentUser().saveInBackground();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    //sets up the chip listener
    public void setChipListener() {
        for (int i = 0; i<priceChips.getChildCount(); i++) {
            Chip chip = (Chip)priceChips.getChildAt(i);


            // Set the chip checked change listener
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        if(buttonView.getText().equals("$")) prices[0] = true;
                        if(buttonView.getText().equals("$$")) prices[1] = true;
                        if(buttonView.getText().equals("$$$")) prices[2] = true;
                        if(buttonView.getText().equals("$$$$")) prices[3] = true;
                    } else {
                        if(buttonView.getText().equals("$")) prices[0] = false;
                        if(buttonView.getText().equals("$$")) prices[1] = false;
                        if(buttonView.getText().equals("$$$")) prices[2] = false;
                        if(buttonView.getText().equals("$$$$")) prices[3] = false;
                    }
                    model.setPrices(getPriceString());
                    ParseUser.getCurrentUser().put(PRICES_KEY, getPriceString());
                    ParseUser.getCurrentUser().saveInBackground();
                }
            });
        }
    }

    //converts boolean array to string of prices the user has selected
    //ex: [true, false, true, true] -> "1, 3, 4"
    public String getPriceString() {
        String result = "";
        for(int i = 0; i<4; i++){
            if(prices[i] == true) {
                result = result + (i+1) + ", ";
            }
        }
        return result.substring(0, result.length()-2);
    }
}