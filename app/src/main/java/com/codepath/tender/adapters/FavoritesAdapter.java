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

/* adapter for the favorites list of restaurants */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<Restaurant> favorites;
    private Context context;

    private OnClickListenerDelete listener;

    //interface to send positional data to favorites fragment
    public interface OnClickListenerDelete {
        void onItemClicked(String id);
    }

    public FavoritesAdapter(Context context, List<Restaurant> favorites, OnClickListenerDelete listener) {
        this.context = context;
        this.favorites = favorites;
        this.listener = listener;
    }

    //inflating the item_restaurant layout as the view for the view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(favorites.get(position));
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView name;
        private ImageView image;
        private RatingBar ratingBar;
        private TextView distance;
        private TextView reviewCount;
        private TextView price;
        private ImageButton delete;
        private TextView address1;
        private TextView address2;

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
            address1 = itemView.findViewById(R.id.tvAddress1);
            address2 = itemView.findViewById(R.id.tvAddress2);

            //setting click listener on view holder
            itemView.setOnClickListener(this);
        }

        //binding the data to the view holder
        public void bind(Restaurant restaurant) {
            name.setText(restaurant.getName());
            distance.setText(restaurant.getDisplayDistance());
            ratingBar.setRating((float) restaurant.getRating());
            reviewCount.setText(Integer.toString(restaurant.getReview_count()));
            price.setText(restaurant.getPrice());

            if(restaurant.getLocation().getDisplay_address().length > 2) {
                address1.setText(restaurant.getLocation().getDisplay_address()[0] + ", " + restaurant.getLocation().getDisplay_address()[1]);
                address2.setText(restaurant.getLocation().getDisplay_address()[2]);
            } else {
                address1.setText(restaurant.getLocation().getDisplay_address()[0]);
                address2.setText(restaurant.getLocation().getDisplay_address()[1]);
            }

            Glide.with(itemView)
                    .load(restaurant.getImage_url())
                    .centerCrop()
                    .into(image);

            //setting delete icon to call interface method defined in favorites fragment
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(restaurant.getId());
                }
            });
        }

        //start intent for details activity
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            //check that position is valid
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("restaurant_id", favorites.get(position).getId());
                context.startActivity(intent);
            }
        }
    }
}