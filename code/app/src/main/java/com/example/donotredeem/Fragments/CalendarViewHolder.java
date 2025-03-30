package com.example.donotredeem.Fragments;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donotredeem.R;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView dayOfMonth;
    public final ImageView emojiImageView;
    private final CalendarAdapter.OnItemListener onItemListener;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        emojiImageView = itemView.findViewById(R.id.emojiImageView);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onItemListener.onItemClick(getAdapterPosition(), dayOfMonth.getText().toString());
    }
}