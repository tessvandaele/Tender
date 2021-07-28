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

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.codepath.tender.LoginActivity;
import com.codepath.tender.R;
import com.codepath.tender.RestaurantViewModel;
import com.codepath.tender.UserViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import static com.codepath.tender.Constants.CATEGORIES_KEY;
import static com.codepath.tender.Constants.CATEGORY_FIVE;
import static com.codepath.tender.Constants.CATEGORY_FOUR;
import static com.codepath.tender.Constants.CATEGORY_ONE;
import static com.codepath.tender.Constants.CATEGORY_THREE;
import static com.codepath.tender.Constants.CATEGORY_TWO;
import static com.codepath.tender.Constants.LATITUDE_KEY;
import static com.codepath.tender.Constants.LONGITUDE_KEY;
import static com.codepath.tender.Constants.PRICES_KEY;
import static com.codepath.tender.Constants.RADIUS_KEY;

/* user can logout of account and view profile */

public class ProfileFragment extends Fragment {

    private ImageButton ibLogout;
    private TextView tvUsername;
    private TextView tvLocation;
    private SeekBar barRadius;
    private TextView tvRadius;
    private ChipGroup priceChips;
    private ChipGroup categoryChips;

    private boolean[] prices;
    private boolean[] categories;

    private UserViewModel userViewModel;

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
        categories = new boolean[] {false, false, false, false, false};

        ibLogout = view.findViewById(R.id.ibLogout);
        tvUsername = view.findViewById(R.id.tvProfileUsername);
        tvLocation = view.findViewById(R.id.tvLocation);
        barRadius = view.findViewById(R.id.barRadius);
        tvRadius = view.findViewById(R.id.tvRadius);
        priceChips = view.findViewById(R.id.priceChips);
        categoryChips = view.findViewById(R.id.categoryChips);

        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvRadius.setText(Integer.toString(ParseUser.getCurrentUser().getInt(RADIUS_KEY)));
        barRadius.setProgress(ParseUser.getCurrentUser().getInt(RADIUS_KEY));
        tvLocation.setText(ParseUser.getCurrentUser().getDouble(LATITUDE_KEY) + " | " + ParseUser.getCurrentUser().getDouble(LONGITUDE_KEY));

        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        setLogout();

        setPriceChipsData();
        setCategoryChipsData();

        setStatusBarListener();
        setPriceChipsListener();
        setCategoryChipsListener();
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
    public void setPriceChipsData() {
        List<String> user_prices = Arrays.asList(ParseUser.getCurrentUser().getString(PRICES_KEY).split(", "));
        //initialize price chips
        for(int i = 0; i<user_prices.size(); i++){
            int price = Integer.parseInt(user_prices.get(i));
            Chip chip = (Chip) priceChips.getChildAt(price-1);
            prices[price-1] = true;
            chip.setChecked(true);
        }
    }

    //sets the category chips to the correct setting based on user preference
    public void setCategoryChipsData() {
        List<String> user_categories = Arrays.asList(ParseUser.getCurrentUser().getString(CATEGORIES_KEY).split(", "));
        //initialize price chips
        for(int i = 0; i<user_categories.size(); i++){
            int category = getCategoryNum(user_categories.get(i));
            Chip chip = (Chip) categoryChips.getChildAt(category);
            categories[category] = true;
            chip.setChecked(true);
        }
    }

    private int getCategoryNum(String s) {
        if(s.equals(CATEGORY_ONE)) return 0;
        if(s.equals(CATEGORY_TWO)) return 1;
        if(s.equals(CATEGORY_THREE)) return 2;
        if(s.equals(CATEGORY_FOUR)) return 3;
        if(s.equals(CATEGORY_FIVE)) return 4;
        return 0;
    }


    //sets up the status bar listener
    public void setStatusBarListener() {
        barRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRadius.setText(progress + " mi");
                userViewModel.setRadius(progress);
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
    public void setPriceChipsListener() {
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
                    userViewModel.setPrices(getPriceString());
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

    //sets up the chip listener
    public void setCategoryChipsListener() {
        for (int i = 0; i<categoryChips.getChildCount(); i++) {
            Chip chip = (Chip)categoryChips.getChildAt(i);

            // Set the chip checked change listener
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        if(buttonView.getText().equals(CATEGORY_ONE)) categories[0] = true;
                        if(buttonView.getText().equals(CATEGORY_TWO)) categories[1] = true;
                        if(buttonView.getText().equals(CATEGORY_THREE)) categories[2] = true;
                        if(buttonView.getText().equals(CATEGORY_FOUR)) categories[3] = true;
                        if(buttonView.getText().equals(CATEGORY_FIVE)) categories[4] = true;
                    } else {
                        if(buttonView.getText().equals(CATEGORY_ONE)) categories[0] = false;
                        if(buttonView.getText().equals(CATEGORY_TWO)) categories[1] = false;
                        if(buttonView.getText().equals(CATEGORY_THREE)) categories[2] = false;
                        if(buttonView.getText().equals(CATEGORY_FOUR)) categories[3] = false;
                        if(buttonView.getText().equals(CATEGORY_FIVE)) categories[4] = false;
                    }
                    //userViewModel.setPrices(getPriceString());
                    String categoryString = getCategoryString();
                    ParseUser.getCurrentUser().put(CATEGORIES_KEY, categoryString);
                    ParseUser.getCurrentUser().saveInBackground();
                }
            });
        }
    }

    //converts boolean array to string of categories the user has selected
    //ex: [true, false, true, true, false] -> "pizza, chinese, burgers"
    public String getCategoryString() {
        String result = "";
        for(int i = 0; i<5; i++){
            if(categories[i] == true) {
                result = result + getCategoryName(i) + ", ";
            }
        }
        return result.substring(0, result.length()-2);
    }

    public String getCategoryName(int position){
        if(position == 0) return CATEGORY_ONE;
        if(position == 1) return CATEGORY_TWO;
        if(position == 2) return CATEGORY_THREE;
        if(position == 3) return CATEGORY_FOUR;
        if(position == 4) return CATEGORY_FIVE;
        return "";
    }
}