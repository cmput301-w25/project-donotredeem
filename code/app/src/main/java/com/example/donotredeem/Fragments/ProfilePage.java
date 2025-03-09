package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.donotredeem.Fragments.moodhistory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.donotredeem.LogIn;
import com.example.donotredeem.MainActivity;
import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodEventAdapter;
import com.example.donotredeem.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment that represents the user's profile page. It displays the user's recent mood history
 * and provides navigation to other sections of the app, such as editing the profile and viewing mood history.
 * This fragment interacts with Firebase Firestore to fetch user mood events and displays them in a ListView.
 */
public class ProfilePage extends Fragment {
    private ListView recent_list;
    private MoodEventAdapter adapter;
    private ArrayList<MoodEvent> moodHistoryList;
    private FirebaseFirestore db;
    private String loggedInUsername;

    private TextView displayname;
    private TextView follower;
    private TextView following;
    private TextView bio;



    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * Initializes views, fetches mood events for the logged-in user,
     * and sets up navigation for the side panel.
     *
     * @param inflater The LayoutInflater object to inflate the layout
     * @param container The parent view group to contain the inflated view
     * @param savedInstanceState A Bundle containing the saved state (if available)
     * @return The root view of the fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        MainActivity.past_location = "moodhistory";

        recent_list = view.findViewById(R.id.recent_history);
        db = FirebaseFirestore.getInstance();
        moodHistoryList = new ArrayList<MoodEvent>();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("username")) {
            Log.e("MainPageDebug", "Username not found in SharedPreferences, redirecting to login");
            redirectToLogin();
        } else {
            loggedInUsername = sharedPreferences.getString("username", null);
            Log.d("HistoryDebug", "Retrieved username: " + loggedInUsername);
        }

        if (loggedInUsername == null) {
            redirectToLogin();
        } else {
            fetchUserMoodEvents(loggedInUsername);
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


        //testing heer

        sidePanel.findViewById(R.id.nav_history).setOnClickListener(v -> {
            drawerLayout.closeDrawer(sidePanel);

//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            Fragment existingFragment = fragmentManager.findFragmentByTag("moodhistory");
//
//            if (existingFragment == null) { // Only add if not already in stack
//                moodhistory historyFragment = new moodhistory();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, historyFragment, "moodhistory")
//                        .addToBackStack(null)
//                        .commit();
//            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager(); // Get the FragmentManager
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // Begin transaction on fragmentManager
            List<Fragment> fragments = fragmentManager.getFragments(); // Get the current fragments

            // Get and remove each fragment
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    fragmentTransaction.remove(fragment); // Remove each fragment
                }
            }

            fragmentTransaction.commit(); // Commit the transaction

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new moodhistory())
                    .addToBackStack(null)
                    .commit();
        });

        sidePanel.findViewById(R.id.nav_profile).setOnClickListener(v -> {
            drawerLayout.closeDrawer(sidePanel);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager(); // Get the FragmentManager
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // Begin transaction on fragmentManager
            List<Fragment> fragments = fragmentManager.getFragments(); // Get the current fragments

            // Get and remove each fragment
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    fragmentTransaction.remove(fragment); // Remove each fragment
                }
            }

            fragmentTransaction.commit(); // Commit the transaction

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new EditProfile())
                    .addToBackStack(null)
                    .commit();

//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            Fragment existingFragment = fragmentManager.findFragmentByTag("EditProfile");
//
//            if (existingFragment == null) { // Only add if not already in stack
//                EditProfile profileFragment = new EditProfile();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, profileFragment, "EditProfile")
//                        .addToBackStack(null)
//                        .commit();
//            }
        });



        return view;
    }

    /**
     * Fetches mood events for the logged-in user from Firebase Firestore.
     *
     * @param username The username of the logged-in user
     */
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

    /**
     * Fetches mood events from the provided list of document references.
     * The events are then added to the moodHistoryList and displayed.
     *
     * @param moodRefs The list of document references for mood events
     */
    private void fetchMoodEvents(List<DocumentReference> moodRefs) {
        ArrayList<MoodEvent> tempList = new ArrayList<>();
        final int[] fetchedCount = {0}; // Counter to track fetched events

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
                    // All events fetched, now sort and display
                    moodHistoryList.clear();
                    moodHistoryList.addAll(tempList);

                    // Sort first
                    sortMoodEvents();

                    // Keep only the 2 most recent
                    if (moodHistoryList.size() >2) {
                        moodHistoryList = new ArrayList<>(moodHistoryList.subList(0, 2));
                    }


                    Display( moodHistoryList);
                }
            }).addOnFailureListener(e -> {
                Log.e("MoodHistory", "Error fetching document", e);
                fetchedCount[0]++;
            });
        }
    }

    /**
     * Sorts the mood events list by date and time in reverse chronological order.
     */
    private void sortMoodEvents() {
        moodHistoryList.sort((event1, event2) -> {
            try {
                LocalDate date1 = parseStringToDate(event1.getDate());
                LocalDate date2 = parseStringToDate(event2.getDate());

                int dateCompare = date2.compareTo(date1); // Reverse chronological order
                if (dateCompare != 0) return dateCompare;

                LocalTime time1 = parseStringToTime(event1.getTime());
                LocalTime time2 = parseStringToTime(event2.getTime());

                return time2.compareTo(time1); // Reverse chronological order
            } catch (Exception e) {
                Log.e("Sorting", "Error comparing events", e);
                return 0;
            }
        });
    }

    /**
     * Displays the mood events in the list view, either by creating a new adapter
     * or updating the existing one.
     */
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
        recent_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Parses a string representing a date in "dd/MM/yyyy" format into a LocalDate object.
     *
     * @param dateString The date string to be parsed
     * @return The LocalDate representation of the string, or LocalDate.MIN if parsing fails
     */
    private LocalDate parseStringToDate(String dateString) {
        try {
            // Use pattern matching for "DD-MM-YYYY" format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            Log.e("Profile Page", "Invalid date format: " + dateString, e);
            return LocalDate.MIN;
        }
    }

    /**
     * Parses a string representing a time in "HH:mm[:ss]" format into a LocalTime object.
     *
     * @param timeString The time string to be parsed
     * @return The LocalTime representation of the string, or LocalTime.MIN if parsing fails
     */
    private LocalTime parseStringToTime(String timeString) {
        try {
            // Handle both with and without seconds
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm[:ss]");
            return LocalTime.parse(timeString, formatter);
        } catch (Exception e) {
            Log.e("Profile Page", "Invalid time format: " + timeString, e);
            return LocalTime.MIN;
        }
    }

    /**
     * Redirects the user to the login page if the username is not found in SharedPreferences.
     */
    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LogIn.class);
        startActivity(intent);
        requireActivity().finish();
    }
}