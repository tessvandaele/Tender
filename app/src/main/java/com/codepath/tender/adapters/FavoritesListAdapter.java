package com.codepath.tender.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.tender.R;
import com.codepath.tender.models.Restaurant;


public class FavoritesListAdapter extends ListAdapter<Restaurant, FavoritesListAdapter.FavoritesViewHolder> {

    public FavoritesListAdapter(@NonNull DiffUtil.ItemCallback<Restaurant> diffCallback) {
        super(diffCallback);
    }

    //inflating the item_card layout as the view for the view holder
    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new FavoritesViewHolder(view);
    }

    public static class RestaurantDiff extends DiffUtil.ItemCallback<Restaurant> {

        @Override
        public boolean areItemsTheSame(@NonNull Restaurant oldItem, @NonNull Restaurant newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Restaurant oldItem, @NonNull Restaurant newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        holder.bind(getItem(position).getName());
    }

    public class FavoritesViewHolder extends RecyclerView.ViewHolder{

        private TextView name;

        //vew holder constructor
        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
        }

        //binding the data to the view holder
        public void bind(String text) {
            name.setText(text);
        }
    }
}