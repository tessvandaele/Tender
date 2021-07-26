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
import com.codepath.tender.RestaurantRepository;
import com.codepath.tender.RestaurantViewModel;
import com.codepath.tender.adapters.FavoritesAdapter;
import com.codepath.tender.models.Restaurant;

import java.util.ArrayList;
import java.util.List;

/* user can view a list of favorite restaurants, navigate to a details page, and delete a favorite */

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private RestaurantViewModel model;
    private FavoritesAdapter.OnClickListenerDelete listener;

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
        setDeleteListeners();

        //adapter and layout manager set up
        adapter = new FavoritesAdapter(getContext(), favorites, listener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setFavoriteListener();
        model.getFavorites();
    }

    //helper method to set the fetch favorite listener which returns a list of favorite restaurants
    public void setFavoriteListener() {
        model.setFetchFavoritesListener(new RestaurantRepository.FetchFavoritesListener() {
            @Override
            public void onFinishFetch(Restaurant restaurant) {
                favorites.add(restaurant);
                adapter.notifyDataSetChanged();
            }
        });
    }

    //helper method to implement delete listener interface to delete a favorite from the list
    public void setDeleteListeners() {
        listener = new FavoritesAdapter.OnClickListenerDelete() {
            @Override
            public void onItemClicked(String id) {
                model.deleteFavorite(id);
            }
        };

        model.setDeleteFavoriteListener(new RestaurantRepository.DeleteFavoriteListener() {
            @Override
            public void onFinishDelete() {
                favorites.clear();
                model.getFavorites();
            }
        });
    }
}