package com.codepath.tender.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.tender.R;
import com.codepath.tender.models.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Comment> comments;
    private Context context;
    private String userId;

    public CommentsAdapter(Context context, String userId, List<Comment> comments) {
        this.comments = comments;
        this.userId = userId;
        this.context = context;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentsAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        holder.bindComment(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    //common message view holder class
    public class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView body;
        TextView username;
        ImageView profile_image;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            body = itemView.findViewById(R.id.tvBodyComment);
            username = itemView.findViewById(R.id.tvUsernameComment);
            profile_image = itemView.findViewById(R.id.ivProfileImageComment);
        }

        public void bindComment(Comment comment) {
            username.setText(comment.getUsername());
            body.setText(comment.getBody());
            Glide.with(context).load(comment.getProfile_image()).circleCrop().into(profile_image);
        }
    }
}
