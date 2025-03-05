package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.LogIn;
import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodEventAdapter;
import com.example.donotredeem.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class moodhistory extends Fragment implements FilterFragment.FilterMoodListener{
    private ListView listView;
    private MoodEventAdapter adapter;
    private FirebaseFirestore db;
    private String loggedInUsername;
    private ArrayList<MoodEvent> moodHistoryList;

    private SharedPreferences sharedPreferences;

    @Override
    public void filterMood(ArrayList<MoodEvent> filteredList) {
        Display(filteredList);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mood_history, container, false);

        listView = view.findViewById(R.id.history_list);
        moodHistoryList = new ArrayList<MoodEvent>();

        db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("username")) {
            Log.e("MainPageDebug", "Username not found in SharedPreferences, redirecting to login");
            redirectToLogin();
        } else {
            loggedInUsername = sharedPreferences.getString("username", null);
            Log.d("HistoryDebug", "Retrieved username: " + loggedInUsername);
        }

        if (loggedInUsername == null) {
            // No user logged in, redirect to login screen
            redirectToLogin();
        } else {
            // Fetch user details from Firestore
            Log.d("HistoryDebug", "Fetching user details for: " + loggedInUsername);
            fetchUserMoodEvents(loggedInUsername);
        }

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

    private void fetchUserMoodEvents(String username) {
        if (username == null) {
            Log.e("MoodHistory", "No username found in SharedPreferences");
            return;
        }

        db.collection("User")
                .whereEqualTo("username", username)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        Log.d("MoodHistory", "User found: " + userDoc.getData());
                        List<DocumentReference> moodRefsList = (List<DocumentReference>) userDoc.get("MoodRef");
                        if (moodRefsList != null && !moodRefsList.isEmpty()) {
                            fetchMoodEvents(moodRefsList); // Fetch the referenced mood events
                        }
                        else {
                            Log.d("MoodHistory", "No mood events found.");
                        }
                    }

                    else {
                        Log.e("MoodHistory", "No user found with username: " + username);
                    }
                });
    }
    private void fetchMoodEvents(List<DocumentReference> moodRefs) {
        ArrayList<MoodEvent> tempList = new ArrayList<>();
        final int[] fetchedCount = {0};

        for (DocumentReference moodRef : moodRefs) {
            moodRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    try {
                        MoodEvent moodEvent = documentSnapshot.toObject(MoodEvent.class);
                        if (moodEvent != null) {
                            tempList.add(moodEvent);
                        }
                    } catch (Exception e) {
                        Log.e("MoodHistory", "Error converting document", e);
                    }
                }

                fetchedCount[0]++;
                if (fetchedCount[0] == moodRefs.size()) {
                    moodHistoryList.clear();
                    moodHistoryList.addAll(tempList);
                    Display(moodHistoryList); // Only sort once when all data is loaded
                }
            }).addOnFailureListener(e -> {
                Log.e("MoodHistory", "Error fetching document", e);
                fetchedCount[0]++;
            });
        }
    }

    private void Display(ArrayList<MoodEvent> moodHistoryList) {
        // Create a defensive copy to avoid ConcurrentModificationException
        ArrayList<MoodEvent> sortedList = new ArrayList<>(moodHistoryList);

        sortedList.sort((event1, event2) -> {
            try {
                LocalDate date1 = parseStringToDate(event1.getDate());
                LocalDate date2 = parseStringToDate(event2.getDate());

                // First compare dates
                int dateCompare = date2.compareTo(date1); // Reverse chronological
                if (dateCompare != 0) return dateCompare;

                // If dates equal, compare times
                LocalTime time1 = parseStringToTime(event1.getTime());
                LocalTime time2 = parseStringToTime(event2.getTime());
                return time2.compareTo(time1); // Reverse chronological
            } catch (Exception e) {
                Log.e("Sorting", "Error comparing events", e);
                return 0;
            }
        });

        // Update adapter with sorted list
        adapter = new MoodEventAdapter(requireContext(), sortedList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LogIn.class);
        startActivity(intent);
        requireActivity().finish();
    }


    private LocalDate parseStringToDate(String dateString) {
        try {
            // Use ISO format if dates are stored like "2024-03-15"
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            Log.e("MoodHistory", "Invalid date format: " + dateString, e);
            return LocalDate.MIN; // Use MIN instead of current date for error handling
        }
    }

    private LocalTime parseStringToTime(String timeString) {
        try {
            // Handle both with and without seconds
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm[:ss]");
            return LocalTime.parse(timeString, formatter);
        } catch (Exception e) {
            Log.e("MoodHistory", "Invalid time format: " + timeString, e);
            return LocalTime.MIN;
        }
    }
}





//    private void fetchMoodEvents(List<DocumentReference> moodRefs) {
//        for (DocumentReference moodRef : moodRefs) {
//            moodRef.get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            try {
//
//                                MoodEvent moodEvent;
//                                moodEvent = documentSnapshot.toObject(MoodEvent.class);
////
//                                // You can also add this to a list or display it as needed
//                                moodHistoryList.add(moodEvent);
//                                Display(moodHistoryList);
//
//
//                            } catch (Exception e) {
//                                Log.e("MoodHistory", "Error creating MoodEvent from document snapshot", e);
//                            }
//                        } else {
//                            Log.e("MoodHistory", "No document found at reference: " + moodRef.getPath());
//                        }
//
//                    })
//                    .addOnFailureListener(e -> Log.e("MoodHistory", "Error fetching mood event", e));
//        }
//    }

//    private void Display(ArrayList<MoodEvent> moodHistoryList){
//        adapter = new MoodEventAdapter(requireContext(), moodHistoryList);
//        listView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//    }

