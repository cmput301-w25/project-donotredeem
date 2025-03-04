package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.donotredeem.Fragments.moodhistory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodEventAdapter;
import com.example.donotredeem.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

public class ProfilePage extends Fragment {
    private ListView recent_list;
    private MoodEventAdapter adapter;
    private ArrayList<MoodEvent> moodHistoryList;


    MoodEvent[] moodEvents = {
            new MoodEvent("Happy", "12/12/2023", "10:00", "Park", "Alone", null, "Good weather", null),
            new MoodEvent("Sad", "12/12/2023", "10:00", "Home", "Crowd", "Bad news", null, null),
            new MoodEvent("Sad", "12/12/2023", "10:00", "Home", "Pair","Bad news", "Received some disappointing news", null),
            new MoodEvent("Shy", "12/12/2023", "10:00", "Mall", null,null,null,"Shopping"),
            new MoodEvent("Angry", "12/12/2023", "10:00", "Office", null, null,"A difficult meeting happened",null),
            new MoodEvent("Fear", "12/12/2023", "10:00", "Beach", null, null,"Relaxed watching the sunset",null)};
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        recent_list = view.findViewById(R.id.recent_history);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String loggedInUsername = sharedPreferences.getString("username", null);

        moodHistoryList = new ArrayList<MoodEvent>();
        moodHistoryList.addAll(Arrays.asList(moodEvents));

        if (moodHistoryList.size() > 3 ){
            moodHistoryList = new ArrayList<>(moodHistoryList.subList(0, 3));
        }

        adapter = new MoodEventAdapter(requireContext(), moodHistoryList);
        recent_list.setAdapter(adapter);

        DrawerLayout drawerLayout = view.findViewById(R.id.drawer_layout);
        LinearLayout sidePanel = view.findViewById(R.id.side_panel);

        view.findViewById(R.id.side_panel_button).setOnClickListener(v -> {
            // Open the drawer (side panel)
            drawerLayout.openDrawer(sidePanel);
        });

        sidePanel.findViewById(R.id.panel_close).setOnClickListener(v -> {
            drawerLayout.closeDrawer(sidePanel);
        });

        sidePanel.findViewById(R.id.nav_history).setOnClickListener(v -> {
            drawerLayout.closeDrawer(sidePanel);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment existingFragment = fragmentManager.findFragmentByTag("moodhistory");

            if (existingFragment == null) { // Only add if not already in stack
                moodhistory historyFragment = new moodhistory();
                fragmentManager.beginTransaction()
                        .add(R.id.profile_fragment_container, historyFragment, "moodhistory")
                        .addToBackStack(null)
                        .commit();
            }
        });

        sidePanel.findViewById(R.id.nav_profile).setOnClickListener(v -> {
            drawerLayout.closeDrawer(sidePanel);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment existingFragment = fragmentManager.findFragmentByTag("EditProfile");

            if (existingFragment == null) { // Only add if not already in stack
                EditProfile profileFragment = new EditProfile();
                fragmentManager.beginTransaction()
                        .add(R.id.profile_fragment_container, profileFragment, "EditProfile")
                        .addToBackStack(null)
                        .commit();
            }
        });



        return view;
    }
}