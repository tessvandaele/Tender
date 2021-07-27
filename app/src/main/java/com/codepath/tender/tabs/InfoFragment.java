package com.codepath.tender.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.tender.R;
import com.codepath.tender.models.Restaurant;

public class InfoFragment extends Fragment {

    private Restaurant restaurant;

    private TextView address;
    private TextView distance;
    private TextView display_phone;
    private TextView website;

    public InfoFragment() {}

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        bind();
    }

    //inflate layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    //construct view hierarchy
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        address = view.findViewById(R.id.tvAddressInfo);
        distance = view.findViewById(R.id.tvDistanceInfo);
        display_phone = view.findViewById(R.id.tvPhoneInfo);
        website = view.findViewById(R.id.tvWebsiteInfo);
    }

    public void bind() {
        address.setText(getAddressString());
        distance.setText(restaurant.getDisplayDistance());
        display_phone.setText(restaurant.getDisplay_phone());
        website.setText(restaurant.getUrl().substring(0, 20) + "...");
    }

    //determines the correct way to display the location
    public String getAddressString() {
        String result = "";
        if(restaurant.getLocation().getDisplay_address().length > 2) {
            result += restaurant.getLocation().getDisplay_address()[0] + ", " + restaurant.getLocation().getDisplay_address()[1];
            result += "\n";
            result += restaurant.getLocation().getDisplay_address()[2];
        } else {
            result += restaurant.getLocation().getDisplay_address()[0];
            result += "\n";
            result += restaurant.getLocation().getDisplay_address()[1];
        }
        return result;
    }
}