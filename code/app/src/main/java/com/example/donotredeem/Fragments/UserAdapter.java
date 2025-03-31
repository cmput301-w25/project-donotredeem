package com.example.donotredeem.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donotredeem.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * RecyclerView adapter for displaying user list with profile pictures.
 *
 * Features:
 * - Displays usernames in a scrollable list
 * - Loads profile pictures from Firebase Firestore
 * - Handles user click events
 * - Supports dynamic list updates
 * - Uses circular image views for profile pictures
 *
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    /**
     * Interface for handling user item click events
     */
    public interface ItemClickListener {
        /**
         * Called when a user item is clicked
         * @param username The username of the clicked item
         */
        void onItemClick(String username);
    }

    private List<String> userList;
    private final ItemClickListener clickListener;
    private FirebaseFirestore db;

    /**
     * Constructs a UserAdapter with initial data
     *
     * @param userList List of usernames to display
     * @param clickListener Listener for item click events
     */
    public UserAdapter(List<String> userList, ItemClickListener clickListener) {
        this.userList = userList;
        this.clickListener = clickListener;
    }

    /**
     * Creates ViewHolder instances for list items
     *
     * @param parent The parent ViewGroup
     * @param viewType The view type identifier
     * @return New ViewHolder instance with inflated layout
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to list item views
     *
     * @param holder ViewHolder containing item views
     * @param position Position in the data list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String username = userList.get(position);
        holder.textView.setText(username);
        db = FirebaseFirestore.getInstance();

        loadProfilePicture(username, holder.profilePic);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(username);
            }
        });
    }

    /**
     * @return Total number of items in the list
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * Updates the adapter's data source
     *
     * @param newList New list of usernames to display
     */
    public void updateList(List<String> newList) {
        // Create a new list instance
        this.userList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class containing references to list item views
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        public CircleImageView profilePic;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.item_text);
            profilePic = view.findViewById(R.id.item_icon);
        }
    }

    /**
     * Loads profile picture from Firestore storage
     *
     * @param username User to load image for
     * @param imageView Target view for the profile picture
     */
    private void loadProfilePicture(String username, CircleImageView imageView) {
        db.collection("User")
                .document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String pfpUrl = documentSnapshot.getString("pfp");
                        if (pfpUrl != null && !pfpUrl.isEmpty()) {
                            Glide.with(imageView.getContext())
                                    .load(pfpUrl)
                                    .placeholder(R.drawable.ic_account_circle)  // Fallback image
                                    .error(R.drawable.ic_account_circle)        // Error fallback
                                    .into(imageView);
                        } else {
                            imageView.setImageResource(R.drawable.ic_account_circle);  // Fallback if no image
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_account_circle);
                    }
                })
                .addOnFailureListener(e -> imageView.setImageResource(R.drawable.ic_account_circle));
    }
}