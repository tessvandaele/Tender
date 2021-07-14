package com.codepath.tender.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.tender.R;
import com.codepath.tender.models.Restaurant;

public class FavoritesListAdapter extends ListAdapter<Restaurant, FavoritesListAdapter.FavoritesViewHolder> {

    OnClickListenerDelete listener;

    //interface to retrieve data from MainActivity class on which item to delete
    public interface OnClickListenerDelete {
        void onItemClicked(String name);
    }

    public FavoritesListAdapter(@NonNull DiffUtil.ItemCallback<Restaurant> diffCallback, OnClickListenerDelete listener) {
        super(diffCallback);
        this.listener = listener;
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
        holder.bind(getItem(position));
    }

    public class FavoritesViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ImageView image;
        private RatingBar ratingBar;
        private TextView distance;
        private ImageButton delete;

        //vew holder constructor
        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            image = itemView.findViewById(R.id.ivImage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            distance = itemView.findViewById(R.id.tvDistance);
            delete = itemView.findViewById(R.id.ibDelete);
        }

        //binding the data to the view holder
        public void bind(Restaurant restaurant) {
            name.setText(restaurant.getName());
            distance.setText(restaurant.getDisplayDistance());
            ratingBar.setRating(restaurant.getRating());
            Glide.with(itemView)
                    .load(restaurant.getImage_url())
                    .centerCrop()
                    .into(image);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(restaurant.getName());
                }
            });
        }
    }
}