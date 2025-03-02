package com.example.donotredeem.Fragments;

import android.app.Dialog;
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

import java.util.ArrayList;

public class moodhistory extends Fragment {
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
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        ImageButton filter_btn = view.findViewById(R.id.filter_icon);
        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        return view;
    }

    private void showDialog(){
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.filter_mood);
        ImageButton close = dialog.findViewById(R.id.filer_closeButton);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}


