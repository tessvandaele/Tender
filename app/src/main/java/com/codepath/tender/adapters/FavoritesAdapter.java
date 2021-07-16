package com.codepath.tender.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.tender.R;
import com.codepath.tender.models.Restaurant;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    //list of restaurants
    private List<Restaurant> restaurants;
    private Context context;

    public FavoritesAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
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

        //private ImageView image;
        private TextView name;
        //private TextView distance;
        //private RatingBar rating;

        //vew holder constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.tvName);
            //distance = itemView.findViewById(R.id.tvDistanceCard);
            //rating = itemView.findViewById(R.id.rbRatingCard);
        }

        //binding the data to the view holder
        public void bind(Restaurant restaurant) {
//            Glide.with(context)
//                    .load(restaurant.getImage_url())
//                    .into(image);

            name.setText(restaurant.getName());
//            distance.setText(restaurant.getDisplayDistance());
//            rating.setRating((float) restaurant.getRating());
        }
    }
}