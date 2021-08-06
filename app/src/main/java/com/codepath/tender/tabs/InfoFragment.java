package com.codepath.tender.tabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codepath.tender.R;
import com.codepath.tender.RestaurantViewModel;
import com.codepath.tender.models.Restaurant;

/* tab 1: displays address, phone number, and website */

public class InfoFragment extends Fragment {

    private Restaurant restaurant;

    private TextView address;
    private TextView distance;
    private TextView displayPhone;
    private TextView website;
    private ImageButton phoneButton;
    private ImageButton linkButton;
    private ImageButton mapButton;

    private RestaurantViewModel model;

    public InfoFragment() {}

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
        displayPhone = view.findViewById(R.id.tvPhoneInfo);
        website = view.findViewById(R.id.tvWebsiteInfo);
        phoneButton = view.findViewById(R.id.ivPhoneInfo);
        linkButton = view.findViewById(R.id.ivLinkInfo);
        mapButton = view.findViewById(R.id.ivLocationInfo);

        model = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);

        //updating every time the current restaurant changes
        model.getCurrent_restaurant().observe(getViewLifecycleOwner(), restaurant -> {
            this.restaurant = restaurant;
            bind();
        });
    }

    //binding restaurant data to view
    public void bind() {
        address.setText(getAddressString());
        distance.setText(restaurant.getDisplayDistance());
        displayPhone.setText(restaurant.getDisplay_phone());
        website.setText(restaurant.getUrl().substring(0, 20) + "...");

        setButtonRedirects();
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

    //sets up all of the image buttons that redirect the user to an outside source
    public void setButtonRedirects() {
        //redirect to google maps
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = restaurant.getCoordinates().getLatitude();
                double longitude = restaurant.getCoordinates().getLongitude();
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                //passing in the uri to google maps
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                try{
                    if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                        getContext().startActivity(mapIntent);
                    }
                }catch (NullPointerException e){
                    Log.e("InfoFragment", "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                }
            }
        });

        //redirect to phone
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + restaurant.getPhone()));
                startActivity(intent);
            }
        });

        //redirect to yelp webpage
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.getUrl()));
                startActivity(browserIntent);
            }
        });
    }
}