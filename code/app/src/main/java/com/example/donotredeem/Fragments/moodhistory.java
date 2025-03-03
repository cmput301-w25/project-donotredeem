package com.example.donotredeem.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodEventAdapter;
import com.example.donotredeem.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

public class moodhistory extends Fragment implements FilterFragment.FilterMoodListener{
    private ListView listView;
    private MoodEventAdapter adapter;
    private FirebaseFirestore db;
    private ArrayList<MoodEvent> moodHistoryList;
    MoodEvent[] moodEvents = {
            new MoodEvent("Happy", LocalDate.of(2024, 3, 2), LocalTime.of(10, 0), "Park", "Good weather"),
            new MoodEvent("Sad", LocalDate.of(2024, 3, 1), LocalTime.of(15, 30), "Home", "Bad news", "Received some disappointing news"),
            new MoodEvent("Shy", LocalDate.of(2024, 3, 3), LocalTime.of(18, 45), "Mall", "Shopping", "Bought something nice"),
            new MoodEvent("Angry", LocalDate.of(2024, 3, 4), LocalTime.of(14, 15), "Office", "Work stress"),
            new MoodEvent("Fear", LocalDate.of(2024, 3, 5), LocalTime.of(20, 0), "Beach", "Meditation", "Relaxed watching the sunset")
    };

    @Override
    public void filterMood(ArrayList<MoodEvent> filteredList) {
        Display(filteredList);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mood_history, container, false);

        listView = view.findViewById(R.id.history_list);

        db = FirebaseFirestore.getInstance();

        moodHistoryList = new ArrayList<MoodEvent>();
        moodHistoryList.addAll(Arrays.asList(moodEvents));
        Display(moodHistoryList);


        view.findViewById(R.id.cancel_history).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        ImageButton filter_btn = view.findViewById(R.id.filter_icon);

        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create FilterFragment and pass the current mood list
                FilterFragment filterFragment = new FilterFragment();
                Bundle args = new Bundle();
                args.putSerializable("moodEvents", moodHistoryList);
                filterFragment.setArguments(args);
                filterFragment.setTargetFragment(moodhistory.this, 0);
                filterFragment.show(getParentFragmentManager(), "filter");
            }
        });


        return view;
    }

    private void Display(ArrayList<MoodEvent> moodHistoryList){
        adapter = new MoodEventAdapter(requireContext(), moodHistoryList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}


