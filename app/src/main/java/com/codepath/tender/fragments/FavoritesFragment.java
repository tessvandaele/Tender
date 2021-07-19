package com.codepath.tender.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.tender.R;
import com.codepath.tender.RestaurantViewModel;
import com.codepath.tender.YelpService;
import com.codepath.tender.adapters.FavoritesAdapter;
import com.codepath.tender.models.Restaurant;
import com.codepath.tender.models.YelpSearchResult;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* user can view a list of favorite restaurants, navigate to a details page, and delete a favorite */

public class FavoritesFragment extends Fragment {

    private static final String BASE_URL = "https://api.yelp.com/v3/";
    private static final String API_KEY = "GrsRS-QAb3mRuvqWsTPW5Bye4DAJ1TJY9v5addUNFFIhpb-iL8DwR0NJ_y-hOWIc94vW7wpIYZc3HRU7NQyAf0PQ0vsSddtF1qnNXlebmvey-5Vq6myMcfFgYJrtYHYx";

    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private RestaurantViewModel model;
    private FavoritesAdapter.OnClickListenerDelete listener;

    private YelpService yelpService;
    private Retrofit retrofit;

    ArrayList<Restaurant> favorites;

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
        favorites = new ArrayList<>();

        //creating a view model in which data will be saved across screen changes
        model = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);

        //implementing delete listener interface to delete a favorite from the list
        setDeleteListener();

        //adapter and layout manager set up
        adapter = new FavoritesAdapter(getContext(), favorites, listener);
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
                //populating favorites list with favorite restaurants
                for (ParseObject object : objects) {
                    String id = object.getString("restaurantId");
                    getRestaurant(id);
                }
            }
        });
    }

    //run GET request to Yelp API and add resulting restaurant objects to the list of restaurants
    private void getRestaurant(String id) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        yelpService = retrofit.create(YelpService.class);

        yelpService.getRestaurantDetails("Bearer " + API_KEY, id).enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                favorites.add(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                Log.d("Favorites fragment", "Could not retrieve restaurant");
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
                                        favorites.clear();
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