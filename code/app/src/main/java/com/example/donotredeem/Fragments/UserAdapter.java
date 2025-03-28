package com.example.donotredeem.Fragments;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donotredeem.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(String username);
    }

    private List<String> userList;
    private final ItemClickListener clickListener;
    private FirebaseFirestore db;

    public UserAdapter(List<String> userList, ItemClickListener clickListener) {
        this.userList = userList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String username = userList.get(position);
        holder.textView.setText(username);
        db = FirebaseFirestore.getInstance();

        loadProfilePicture(username, holder.profilePic);


        // You can set up the icon here if needed
        // holder.iconView.setImageResource(R.drawable.ic_default_item);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(username);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<String> newList) {
        // Create a new list instance
        this.userList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        public CircleImageView profilePic;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.item_text);
            profilePic = view.findViewById(R.id.item_icon);
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