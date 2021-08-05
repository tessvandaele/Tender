package com.codepath.tender.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.tender.R;
import com.codepath.tender.RecyclerTouchItemHelper;
import com.codepath.tender.RestaurantRepository;
import com.codepath.tender.RestaurantViewModel;
import com.codepath.tender.adapters.FavoritesAdapter;
import com.codepath.tender.models.Restaurant;

import java.util.ArrayList;

/* user can view a list of favorite restaurants, navigate to a details page, and delete a favorite */

public class FavoritesFragment extends Fragment implements RecyclerTouchItemHelper.RecyclerItemTouchHelperListener{

    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private RestaurantViewModel model;
    private FavoritesAdapter.OnClickListenerDelete listener;
    private LinearLayoutManager layoutManager;

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

        //adapter and layout manager set up
        adapter = new FavoritesAdapter(getContext(), favorites, listener);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //touch helper to allow swipe to delete; left swipe only
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerTouchItemHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

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

    //when item is swipe left, it is deleted from list
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavoritesAdapter.ViewHolder) {
            // remove item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition(), model);
        }
    }
}