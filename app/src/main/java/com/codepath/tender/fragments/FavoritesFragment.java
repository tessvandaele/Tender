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
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/* user can view a list of favorite restaurants, navigate to a details page, and delete a favorite */

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private RestaurantViewModel model;
    private FavoritesAdapter.OnClickListenerDelete listener;

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

        //creating a view model in which data will be saved across screen changes
        model = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);

        //implementing delete listener interface to delete a favorite from the list
        setDeleteListener();

        //adapter and layout manager set up
        adapter = new FavoritesAdapter(getContext(), restaurants, listener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setFavorites();
    }

    //helper methods to retrieve favorites list for current user and populate adapter
    public void setFavorites() {
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

    //helper method to implement delete listener interface to delete a favorite from the list
    public void setDeleteListener() {
        listener = new FavoritesAdapter.OnClickListenerDelete() {
            @Override
            public void onItemClicked(String id) {
                //retrieve correct favorite
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Favorite");
                query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId()); //user id matches current user
                query.whereEqualTo("restaurantId", id); //restaurant id matches id from view holder
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if(e == null) {
                            //deleting object if found
                            object.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        restaurants.clear();
                                        setFavorites();
                                    }
                                }
                            });
                        }

                    }
                });
            }
        };
    }
}