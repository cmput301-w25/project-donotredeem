package com.example.donotredeem.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * RecyclerView adapter for displaying a list of user followers.
 * Handles:
 * - Follower username display
 * - Profile picture loading from Firestore storage
 * - Graceful fallback to default avatar when images are unavailable
 * - Efficient view recycling for smooth scrolling
 */
public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.FollowerViewHolder> {
    private final Context context;
    private final List<String> follower_list;
    private final UserProfileManager userProfileManager = new UserProfileManager();
    private FirebaseFirestore db;

    /**
     * Constructs a FollowerAdapter with required parameters
     * @param context Host activity context for resource access
     * @param follower_list List of follower usernames to display
     */
    public FollowerAdapter(Context context, List<String> follower_list) {
        this.context = context;
        this.follower_list = follower_list;
    }

    /**
     * Creates ViewHolder instances for list items
     * @param parent The parent ViewGroup for inflation
     * @param viewType The type of view (unused in this implementation)
     * @return New FollowerViewHolder instance with inflated layout
     */
    @NonNull
    @Override
    public FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.follower_item, parent, false);
        return new FollowerViewHolder(view);
    }

    /**
     * Binds data to list item views
     * @param holder ViewHolder to populate with data
     * @param position Position in the dataset
     */
    @Override
    public void onBindViewHolder(@NonNull FollowerViewHolder holder, int position) {
        String followerUsername = follower_list.get(position);
        holder.username.setText(followerUsername);
        db = FirebaseFirestore.getInstance();
        loadProfilePicture(followerUsername, holder.profilePic);
    }

    /**
     * Returns total number of items in the adapter
     * @return Size of follower list
     */
    @Override
    public int getItemCount() {
        return follower_list.size();
    }

    /**
     * ViewHolder class containing UI components for individual list items
     */
    public static class FollowerViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        public CircleImageView profilePic;

        /**
         * Constructs ViewHolder and binds view components
         * @param itemView Root view of list item layout
         */
        public FollowerViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.follower);
            profilePic = itemView.findViewById(R.id.author_icon);
        }
    }

    /**
     * Loads and displays user profile picture using Glide
     * @param username User to load picture for
     * @param imageView Target view to display the image
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
