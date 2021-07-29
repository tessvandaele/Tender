package com.codepath.tender.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.tender.R;
import com.codepath.tender.models.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Comment> comments;
    private Context context;
    private String userId;

    public CommentsAdapter(Context context, String userId, List<Comment> comments) {
        comments = comments;
        this.userId = userId;
        context = context;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentsAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bindComment(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    //common message view holder class
    public class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView body;
        TextView username;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            body = itemView.findViewById(R.id.tvBodyComment);
            username = itemView.findViewById(R.id.tvUsernameComment);
        }

        public void bindComment(Comment comment) {
            username.setText(comment.getUsername());
            body.setText(comment.getBody());
        }
    }
}
