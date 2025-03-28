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
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.FollowerViewHolder> {
    private final Context context;
    private final List<String> follower_list;
    private final UserProfileManager userProfileManager = new UserProfileManager();
    private FirebaseFirestore db;

    // Constructor
    public FollowerAdapter(Context context, List<String> follower_list) {
        this.context = context;
        this.follower_list = follower_list;
    }

    // Create ViewHolder - Inflate the item layout
    @NonNull
    @Override
    public FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.follower_item, parent, false);
        return new FollowerViewHolder(view);
    }

    // Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull FollowerViewHolder holder, int position) {
        String followerUsername = follower_list.get(position);
        holder.username.setText(followerUsername);
        db = FirebaseFirestore.getInstance();
        loadProfilePicture(followerUsername, holder.profilePic);
    }

    // Return the number of items in the list
    @Override
    public int getItemCount() {
        return follower_list.size();
    }

    // ViewHolder Class - Holds the views for each list item
    public static class FollowerViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        public CircleImageView profilePic;

        public FollowerViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.follower);
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
