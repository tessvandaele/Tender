package com.codepath.tender.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.tender.R;
import com.codepath.tender.RestaurantViewModel;
import com.codepath.tender.adapters.FavoritesAdapter;
import com.codepath.tender.models.Restaurant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private RestaurantViewModel model;
    ArrayList<Restaurant> restaurants;

    //empty constructor
    public FavoritesFragment() {
    }

    //inflate layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.rvRestaurants);
        restaurants = new ArrayList<>();

        model = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);

        //set up adapter
        adapter = new FavoritesAdapter(getContext(), restaurants);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //parse for favorites restaurant objects
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Favorite");
        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() { //parse for favorites of user
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject object : objects) {
                    String id = object.getString("restaurantId");
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
                    query.getInBackground(id, (result, exception) -> { //parse for restaurants in favorites
                        if (exception == null) {
                            restaurants.add((Restaurant)result);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}