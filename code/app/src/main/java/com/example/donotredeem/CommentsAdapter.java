package com.example.donotredeem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.donotredeem.Comment;
import com.example.donotredeem.R;
import java.util.ArrayList;
import java.util.Locale;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private ArrayList<Comment> commentList;

    public CommentsAdapter(ArrayList<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment currentComment = commentList.get(position);
        holder.authorTextView.setText(currentComment.getAuthor());
        holder.commentTextView.setText(currentComment.getCommentText());
        holder.timestampTextView.setText(formatTimestamp(currentComment.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

//    public static class CommentViewHolder extends RecyclerView.ViewHolder {
//        public TextView authorTextView, commentTextView;
//        public CommentViewHolder(@NonNull View itemView) {
//            super(itemView);
//            authorTextView = itemView.findViewById(R.id.comment_author);
//            commentTextView = itemView.findViewById(R.id.comment_text);
//        }
//    }
//    private String formatTimestamp(Timestamp timestamp) {
//        long commentTime = timestamp.toDate().getTime();
//        long currentTime = System.currentTimeMillis();
//        long diff = currentTime - commentTime;
//        long hours = diff / (60 * 60 * 1000);
//
//        if (hours < 24) {
//            return hours + " hour" + (hours != 1 ? "s" : "") + " ago";
//        } else {
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//            return sdf.format(timestamp.toDate());
//        }
//    }
private String formatTimestamp(Timestamp timestamp) {
    long commentTime = timestamp.toDate().getTime();
    long currentTime = System.currentTimeMillis();
    long diff = currentTime - commentTime;

    long seconds = diff / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    long days = hours / 24;

    if (days > 0) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    } else if (hours > 0) {
        return hours + " hour" + (hours != 1 ? "s" : "") + " ago";
    } else if (minutes > 0) {
        return minutes + " minute" + (minutes != 1 ? "s" : "") + " ago";
    } else {
        return "Just now";
    }
}
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTextView, commentTextView, timestampTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.comment_author);
            commentTextView = itemView.findViewById(R.id.comment_text);
            timestampTextView = itemView.findViewById(R.id.comment_timestamp);
        }
    }

}

