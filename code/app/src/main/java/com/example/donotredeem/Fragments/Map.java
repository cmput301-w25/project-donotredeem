package com.example.donotredeem.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodEventAdapter;
import com.example.donotredeem.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Map extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public class MoodHistory extends Fragment {
        private ListView listView;
        private MoodEventAdapter adapter;
        private FirebaseFirestore db;
        private ArrayList<MoodEvent> moodHistoryList;
        Dialog dialog;
        Button filter_btn;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.mood_history, container, false);

        listView = view.findViewById(R.id.history_list);

            db = FirebaseFirestore.getInstance();

            moodHistoryList = new ArrayList<>();

            adapter = new MoodEventAdapter(requireContext(), moodHistoryList);
            listView.setAdapter(adapter);


            view.findViewById(R.id.cancel_history).setOnClickListener(v -> {
                // Navigate back to the ProfilePage
                requireActivity().getSupportFragmentManager().popBackStack();
            });

            return view;
        }
    }
}
