package com.example.donotredeem.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.donotredeem.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    private final Map<String, Integer> emojiMap;

    public CalendarAdapter(ArrayList<String> daysOfMonth,
                           OnItemListener onItemListener,
                           Map<String, Integer> emojiMap) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.emojiMap = emojiMap != null ? emojiMap : new HashMap<>();
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String day = daysOfMonth.get(position);
        holder.dayOfMonth.setText(day);

        // Reset emoji visibility
        holder.emojiImageView.setVisibility(View.GONE);

        // Check if day is not empty and exists in emoji map
        if (!day.isEmpty() && emojiMap != null && emojiMap.containsKey(day)) {
            Integer emojiResId = emojiMap.get(day);

            if (emojiResId != null && emojiResId != 0) {
                holder.emojiImageView.setImageResource(emojiResId);
                holder.emojiImageView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}