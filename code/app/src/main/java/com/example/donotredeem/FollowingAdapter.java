package com.example.donotredeem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.R;

import java.util.List;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder> {
    private final Context context;
    private final List<String> following_list;
    private final UserProfileManager userProfileManager = new UserProfileManager();

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
    }

    // Return the number of items in the list
    @Override
    public int getItemCount() {
        return following_list.size();
    }

    // ViewHolder Class - Holds the views for each list item
    public static class FollowingViewHolder extends RecyclerView.ViewHolder {
        TextView username;

        public FollowingViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.following);
        }
    }
}
