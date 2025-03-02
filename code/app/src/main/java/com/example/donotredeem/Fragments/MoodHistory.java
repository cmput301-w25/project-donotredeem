package com.example.donotredeem.Fragments;

<<<<<<< Updated upstream
import android.app.Dialog;
import android.media.Image;
=======
import android.location.Location;
>>>>>>> Stashed changes
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< Updated upstream
import android.widget.Button;
import android.widget.ImageButton;
=======
import android.widget.ListView;
>>>>>>> Stashed changes

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
<<<<<<< Updated upstream

import com.example.donotredeem.MainActivity;
import com.example.donotredeem.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MoodHistory extends Fragment {
    private Button nextButton;
    Dialog dialog;
    Button filter_btn;
=======
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodEventAdapter;
import com.example.donotredeem.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoodHistory extends Fragment {
    private ListView listView;
    private MoodEventAdapter adapter;
    private FirebaseFirestore db;
    private ArrayList<MoodEvent> moodHistoryList;

>>>>>>> Stashed changes

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mood_history, container, false);
<<<<<<< Updated upstream

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
=======
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

>>>>>>> Stashed changes
