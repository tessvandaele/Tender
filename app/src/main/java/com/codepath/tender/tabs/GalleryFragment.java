package com.codepath.tender.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.tender.R;
import com.codepath.tender.RestaurantViewModel;
import com.codepath.tender.models.Restaurant;

public class GalleryFragment extends Fragment {

    private Restaurant restaurant;

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;

    private RestaurantViewModel model;

    public GalleryFragment() {}

    //inflate layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    //construct view hierarchy
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        image1 = view.findViewById(R.id.image1Gallery);
        image2 = view.findViewById(R.id.image2Gallery);
        image3 = view.findViewById(R.id.image3Gallery);

        model = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);

        model.getCurrent_restaurant().observe(getViewLifecycleOwner(), restaurant1 -> {
            this.restaurant = restaurant1;
            bind();
        });

    }

    private void bind() {
        if(image1 != null) {
            Glide.with(getContext()).load(restaurant.getPhotos()[0]).centerCrop().into(image1);
            Glide.with(getContext()).load(restaurant.getPhotos()[1]).centerCrop().into(image2);
            Glide.with(getContext()).load(restaurant.getPhotos()[2]).centerCrop().into(image3);
        }

    }
}