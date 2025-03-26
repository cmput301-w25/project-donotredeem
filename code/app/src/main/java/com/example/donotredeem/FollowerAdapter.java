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

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.FollowerViewHolder> {
    private final Context context;
    private final List<String> follower_list;
    private final UserProfileManager userProfileManager = new UserProfileManager();

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
    }

    // Return the number of items in the list
    @Override
    public int getItemCount() {
        return follower_list.size();
    }

    // ViewHolder Class - Holds the views for each list item
    public static class FollowerViewHolder extends RecyclerView.ViewHolder {
        TextView username;

        public FollowerViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.follower);
        }
    }
}
