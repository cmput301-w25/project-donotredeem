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
            Display(moodHistoryList); // Assuming you want to display it after adding
        }

//        moodHistoryList.addAll(Arrays.asList(moodEvents));
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


    private void fetchUserMoodEvents(String username) {
        if (username == null) {
            Log.e("MoodHistory", "No username found in SharedPreferences");
            return;
        }

        db.collection("User")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        Log.d("MoodHistory", "User found: " + userDoc.getData()); // Log user document details

                        // Get the MoodREF field which is a DocumentReference
                        DocumentReference moodRef = userDoc.getDocumentReference("MoodREF");

                        if (moodRef != null) {
                            Log.d("MoodHistory", "MoodREF found: " + moodRef.getPath()); // Log the MoodREF path

                            String References = moodRef.getPath();
                            String[] refArray = References.split(","); // Split the string by commas

                            // Create a list to store the DocumentReferences
                            List<DocumentReference> moodRefsList = new ArrayList<>();

                            for (String ref : refArray) {
                                ref = ref.trim();  // Remove leading/trailing whitespace
                                DocumentReference documentReference = db.document(ref); // Create a new DocumentReference for each
                                moodRefsList.add(documentReference);  // Add it to the list
                                Log.d("MoodHistory", "Document Reference: " + documentReference.getPath()); // Log each reference
                            }

                            // Fetch the mood events using the list of references
                            fetchMoodEvents(moodRefsList);
                        } else {
                            Log.e("MoodHistory", "MoodREF is null or not a reference");
                        }
                    } else {
                        Log.e("MoodHistory", "No user found with username: " + username);
                    }
                })
                .addOnFailureListener(e -> Log.e("MoodHistory", "Error fetching user data", e));
    }

    private void fetchMoodEvents(List<DocumentReference> moodRefs) {
        for (DocumentReference moodRef : moodRefs) {
            moodRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            try {
                                // Retrieve fields from the document snapshot
                                String emotionalState = (String) documentSnapshot.get("mood");
                                String dateString = (String) documentSnapshot.get("date"); // Assuming it's stored
                                String trigger = (String) documentSnapshot.get("trigger");
                                String explainText = (String) documentSnapshot.get("description");
                                String timeString = (String) documentSnapshot.get("time");
                                Log.e("MoodHistory", "Time string:" + timeString);
                                String situation = (String) documentSnapshot.get("socialSituation");
                                String location = (String) documentSnapshot.get("location");
                                String picture_string = (String)documentSnapshot.get("imageUrl");
                                Log.e("MoodHistory", "  Picture string:" + picture_string);



                                LocalDate date = parseStringToDate(dateString);
                                LocalTime time = parseStringToTime(timeString);

                                MoodEvent moodEvent;
                                if (picture_string == null){
                                    moodEvent = new MoodEvent(emotionalState, date, time, location, situation, trigger, explainText);

                                }
                                else {
                                moodEvent = new MoodEvent(emotionalState, date, time, location, situation, trigger, explainText,picture_string);}

                                // You can also add this to a list or display it as needed
                                moodHistoryList.add(moodEvent);
                                Display(moodHistoryList);


                            } catch (Exception e) {
                                Log.e("MoodHistory", "Error creating MoodEvent from document snapshot", e);
                            }
                        } else {
                            Log.e("MoodHistory", "No document found at reference: " + moodRef.getPath());
                        }

                    })
                    .addOnFailureListener(e -> Log.e("MoodHistory", "Error fetching mood event", e));
        }
    }

    // Helper method to parse date string to LocalDate
    private LocalDate parseStringToDate(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Adjust format based on your date format
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            Log.e("MoodHistory", "Invalid date format", e);
            return LocalDate.now(); // Default to current date if parsing fails
        }
    }



    private void Display(ArrayList<MoodEvent> moodHistoryList){
        adapter = new MoodEventAdapter(requireContext(), moodHistoryList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private LocalTime parseStringToTime(String timeString) {
        // Log the raw string received
        Log.d("MoodHistory", "Parsing time string: " + timeString);

        if (timeString == null || timeString.isEmpty()) {
            Log.e("MoodHistory", "Time string is null or empty");
            return LocalTime.now(); // Default to current time if parsing fails
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // 24-hour format
            LocalTime parsedTime = LocalTime.parse(timeString, formatter);

            // Log the parsed time
            Log.d("MoodHistory", "Parsed time: " + parsedTime);

            return parsedTime;
        } catch (DateTimeParseException e) {
            Log.e("MoodHistory", "Error parsing time: " + timeString, e);
            return LocalTime.now(); // Default to current time if parsing fails
        }
    }







    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LogIn.class);
        startActivity(intent);
        requireActivity().finish();
    }

}


