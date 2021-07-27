package com.codepath.tender.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.tender.R;
import com.codepath.tender.models.Restaurant;

public class HoursFragment extends Fragment {

    private Restaurant restaurant;

    public HoursFragment() {}

    public HoursFragment(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    //inflate layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hours, container, false);
    }

    //construct view hierarchy
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }
}