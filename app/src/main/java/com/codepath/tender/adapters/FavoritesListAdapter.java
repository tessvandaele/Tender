package com.codepath.tender.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.tender.R;
import com.codepath.tender.models.Restaurant;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


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
        holder.bind(getItem(position));
    }

    public class FavoritesViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ImageView image;
        private RatingBar ratingBar;
        private TextView distance;

        //vew holder constructor
        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            image = itemView.findViewById(R.id.ivImage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            distance = itemView.findViewById(R.id.tvDistance);
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
        }
    }
}