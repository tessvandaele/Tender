package com.codepath.tender.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.codepath.tender.LoginActivity;
import com.codepath.tender.R;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    private ImageButton ibLogout;
    private TextView tvUsername;

    //empty constructor
    public ProfileFragment() {}

    //set up to view object hierarchy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ibLogout = view.findViewById(R.id.ibLogout);
        tvUsername = view.findViewById(R.id.tvProfileUsername);

        tvUsername.setText(ParseUser.getCurrentUser().getUsername().toString());

        //setting up log out button to allow user to log out
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
}