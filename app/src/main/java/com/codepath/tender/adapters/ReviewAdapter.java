package com.codepath.tender.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.codepath.tender.R;
import com.codepath.tender.models.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{

    private List<Review> reviews;
    private Context context;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    //inflating the item_restaurant layout as the view for the view holder
    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        holder.bind(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView user_image;
        private TextView user_name;
        private TextView created_at;
        private RatingBar user_rating;
        private TextView user_text;
        private ImageButton review_link;


        //vew holder constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.ivUserImage);
            user_name = itemView.findViewById(R.id.tvUserName);
            created_at = itemView.findViewById(R.id.tvCreated_at);
            user_rating = itemView.findViewById(R.id.rbUserRating);
            user_text = itemView.findViewById(R.id.tvReviewText);
            review_link = itemView.findViewById(R.id.btnReviewLink);
        }

        //binding the data to the view holder
        public void bind(Review review) {
            user_name.setText(review.getUser().getName());
            created_at.setText(review.getTime_created());
            user_rating.setRating(review.getRating());
            user_text.setText(review.getText());

            Glide.with(context)
                    .load(review.getUser().getImage_url())
                    .circleCrop()
                    .into(user_image);

            review_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
                    context.startActivity(browserIntent);
                }
            });

        }
    }
}
