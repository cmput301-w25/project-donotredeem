package com.example.donotredeem;

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

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder> {
    private final Context context;
    private final List<String> following_list;
    private final UserProfileManager userProfileManager = new UserProfileManager();
    private FirebaseFirestore db;

    // Constructor
    public FollowingAdapter(Context context, List<String> following_list) {
        this.context = context;
        this.following_list = following_list;
    }

    // Create ViewHolder - Inflate the item layout
    @NonNull
    @Override
    public FollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.follower_item, parent, false);
        return new FollowingViewHolder(view);
    }

    // Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull FollowingViewHolder holder, int position) {
        String followingUsername = following_list.get(position);
        holder.username.setText(followingUsername);
        db = FirebaseFirestore.getInstance();
        loadProfilePicture(followingUsername, holder.profilePic);
    }

    // Return the number of items in the list
    @Override
    public int getItemCount() {
        return following_list.size();
    }

    // ViewHolder Class - Holds the views for each list item
    public static class FollowingViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        public CircleImageView profilePic;

        public FollowingViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.following);
            profilePic = itemView.findViewById(R.id.author_icon);
        }
    }
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
