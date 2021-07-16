package com.codepath.tender.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.tender.DetailsActivity;
import com.codepath.tender.R;
import com.codepath.tender.models.Restaurant;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    //list of restaurants
    private List<Restaurant> restaurants;
    private Context context;
    private OnClickListenerDelete listener;

    //interface to retrieve data from MainActivity class on which item to delete
    public interface OnClickListenerDelete {
        void onItemClicked(String id);
    }

    public FavoritesAdapter(Context context, List<Restaurant> restaurants, OnClickListenerDelete listener) {
        this.context = context;
        this.restaurants = restaurants;
        this.listener = listener;
    }

    //inflating the item_card layout as the view for the view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(restaurants.get(position));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView image;
        private RatingBar ratingBar;
        private TextView distance;
        private TextView reviewCount;
        private TextView price;
        private ImageButton delete;

        //vew holder constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            image = itemView.findViewById(R.id.ivImage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            distance = itemView.findViewById(R.id.tvDistance);
            delete = itemView.findViewById(R.id.ibDelete);
            reviewCount = itemView.findViewById(R.id.tvReviewCount);
            price = itemView.findViewById(R.id.tvPrice);
        }

        //binding the data to the view holder
        public void bind(Restaurant restaurant) {
            name.setText(restaurant.getName());
            distance.setText(restaurant.getDisplayDistance());
            ratingBar.setRating((float) restaurant.getRating());
            reviewCount.setText(Integer.toString(restaurant.getReview_count()));
            price.setText(restaurant.getPrice());
            Glide.with(itemView)
                    .load(restaurant.getImage_url())
                    .centerCrop()
                    .into(image);
        }
    }
}