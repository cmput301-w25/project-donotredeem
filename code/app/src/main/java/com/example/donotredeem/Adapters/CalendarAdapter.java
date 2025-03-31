package com.example.donotredeem.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donotredeem.Fragments.CalendarViewHolder;
import com.example.donotredeem.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for displaying a calendar grid in a RecyclerView.
 * It shows the days of the month along with mood emojis.
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    private final Map<String, Integer> emojiMap;

    /**
     * Constructor to initialize the adapter with days of the month, click listener, and emoji map.
     *
     * @param daysOfMonth    List of day numbers or empty slots for the current month.
     * @param onItemListener Listener for handling click events on calendar cells.
     * @param emojiMap       Map containing day-to-emoji mappings.
     */
    public CalendarAdapter(ArrayList<String> daysOfMonth,
                           OnItemListener onItemListener,
                           Map<String, Integer> emojiMap) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.emojiMap = emojiMap != null ? emojiMap : new HashMap<>();
    }

    /**
     * Creates and returns a new CalendarViewHolder by inflating the calendar cell layout.
     *
     * @param parent   The parent view group.
     * @param viewType The type of view (not used here).
     * @return A new CalendarViewHolder instance.
     */
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    /**
     * Binds the day and emoji to the calendar cell at the given position.
     *
     * @param holder   The ViewHolder containing the calendar cell view.
     * @param position The position of the cell in the grid.
     */
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

    /**
     * Returns the total number of items (days) in the calendar grid.
     *
     * @return The total number of calendar cells.
     */
    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    /**
     * Interface for handling calendar cell click events.
     */
    public interface OnItemListener {
        /**
         * Called when a calendar cell is clicked.
         *
         * @param position The position of the clicked cell.
         * @param dayText  The text representing the day of the month.
         */
        void onItemClick(int position, String dayText);
    }
}