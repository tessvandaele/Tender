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
import com.codepath.tender.adapters.FavoritesListAdapter;

public class FavoritesFragment extends Fragment {

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
        RecyclerView recyclerView = view.findViewById(R.id.rvRestaurants);

        RestaurantViewModel model = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);

        //setting up the Items Adapter which handles the View Holder
        FavoritesListAdapter.OnClickListenerDelete listener = new FavoritesListAdapter.OnClickListenerDelete() {
            @Override
            public void onItemClicked(String name) {
                //delete item from list
                model.delete(name);
            }
        };


        final FavoritesListAdapter adapter = new FavoritesListAdapter(new FavoritesListAdapter.RestaurantDiff(), listener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        model.getAllRestaurants().observe(getViewLifecycleOwner(), words -> {
            // Update the cached copy of the words in the adapter.
            adapter.submitList(words);
        });

    }
}