package com.example.donotredeem.Fragments;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donotredeem.Adapters.CalendarAdapter;
import com.example.donotredeem.R;

/**
 * ViewHolder for displaying individual calendar cells in a RecyclerView.
 * Each cell contains the day of the month and an optional emoji representing the user's mood.
 */
public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView dayOfMonth;
    public final ImageView emojiImageView;
    private final CalendarAdapter.OnItemListener onItemListener;

    /**
     * Constructor to initialize the ViewHolder.
     *
     * @param itemView         The view representing the calendar cell.
     * @param onItemListener   Listener for handling click events on the cell.
     */
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);

        // Initialize the day label and emoji image view
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        emojiImageView = itemView.findViewById(R.id.emojiImageView);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    /**
     * Handles click events on the calendar cell.
     *
     * @param view The clicked view.
     */
    @Override
    public void onClick(View view) {
        // Get the adapter position and the day text, then trigger the click listener
        onItemListener.onItemClick(getAdapterPosition(), dayOfMonth.getText().toString());
    }
}