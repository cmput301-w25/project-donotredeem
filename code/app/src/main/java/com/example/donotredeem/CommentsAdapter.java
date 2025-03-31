package com.example.donotredeem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Locale;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * RecyclerView adapter for displaying comments with user details and relative timestamps.
 * Handles:
 * - Comment author information display
 * - Profile picture loading from Firestore
 * - Time formatting for comments
 * - Dynamic comment list updates
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    /** List of comments to display */
    private ArrayList<Comment> commentList;

    /**
     * Constructs a CommentsAdapter with initial comment data
     * @param commentList List of Comment objects to display
     */
    public CommentsAdapter(ArrayList<Comment> commentList) {
        this.commentList = commentList;
    }

    /**
     * Creates ViewHolder instances for comment items
     * @return New ViewHolder with inflated comment_item layout
     */
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(v);
    }

    /**
     * Binds comment data to ViewHolder elements
     * @param holder ViewHolder to configure
     * @param position Position in comment list
     */
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment currentComment = commentList.get(position);
        holder.authorTextView.setText(currentComment.getAuthor());
        holder.commentTextView.setText(currentComment.getCommentText());
        holder.timestampTextView.setText(formatTimestamp(currentComment.getTimestamp()));
        loadProfilePicture(currentComment.getAuthor(), holder.authorIcon);
    }

    /**
     * Loads user profile picture from Firestore using Glide
     * @param username User to load picture for
     * @param imageView Target view to display the image
     */
    private void loadProfilePicture(String username, ImageView imageView) {
        FirebaseFirestore.getInstance()
                .collection("User")
                .document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String pfpUrl = documentSnapshot.getString("pfp");
                        if (pfpUrl != null && !pfpUrl.isEmpty()) {
                            Glide.with(imageView.getContext())
                                    .load(pfpUrl)
                                    .placeholder(R.drawable.ic_account_circle)
                                    .error(R.drawable.ic_account_circle)
                                    .into(imageView);
                        }
                    }
                });
    }

    /**
     * Gets the number of comments in the adapter
     * @return Total number of comments
     */
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    /**
     * Formats Firestore timestamp to relative time string
     * @param timestamp Comment creation timestamp
     * @return Human-readable time difference (e.g., "2 hours ago")
     */
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

    /**
     * ViewHolder class containing UI components for individual comment items
     */
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTextView, commentTextView, timestampTextView;
        public CircleImageView authorIcon;

        /**
         * Constructs ViewHolder and binds layout components
         * @param itemView Root view of comment item layout
         */
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.comment_author);
            commentTextView = itemView.findViewById(R.id.comment_text);
            timestampTextView = itemView.findViewById(R.id.comment_timestamp);
            authorIcon = itemView.findViewById(R.id.author_icon);
        }
    }

}

