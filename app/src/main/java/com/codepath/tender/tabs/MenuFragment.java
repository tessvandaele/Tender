package com.codepath.tender.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.tender.R;

public class MenuFragment extends Fragment {

    public MenuFragment() {}

    //inflate layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    //construct view hierarchy
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }
}