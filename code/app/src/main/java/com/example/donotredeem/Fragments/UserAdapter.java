package com.example.donotredeem.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donotredeem.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(String username);
    }

    private List<String> userList;
    private final ItemClickListener clickListener;

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
        public final ImageView iconView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.item_text);
            iconView = view.findViewById(R.id.item_icon);
        }
    }
}