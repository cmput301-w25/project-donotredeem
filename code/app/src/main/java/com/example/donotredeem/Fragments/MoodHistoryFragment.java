package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.FragmentManager;

import com.example.donotredeem.Classes.NetworkUtils;
import com.example.donotredeem.Activities.LogInActivity;
import com.example.donotredeem.Classes.MoodEvent;
import com.example.donotredeem.Adapters.MoodEventAdapter;
import com.example.donotredeem.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code moodhistory} class represents a fragment that displays a history of mood events for a logged-in user.
 * This fragment retrieves mood events from Firebase Firestore and presents them in a sorted ListView.
 * It also provides functionality to filter mood events using the {@link FilterFragment}.
 */
public class MoodHistoryFragment extends Fragment implements FilterFragment.FilterMoodListener{

    private ListView listView;
    private MoodEventAdapter adapter;
    private FirebaseFirestore db;
    private String loggedInUsername;
    private ArrayList<MoodEvent> moodHistoryList;
    private SharedPreferences sharedPreferences;

    /**
     * Applies a filtered list of mood events to the display.
     *
     * @param filteredList The filtered list of mood events
     */
    @Override
    public void filterMood(ArrayList<MoodEvent> filteredList) {
        Display(filteredList);

    }

    /**
     * Inflates the fragment layout and initializes UI elements.
     * Retrieves the logged-in username and fetches mood events from Firestore.
     *
     * @param inflater LayoutInflater object to inflate the view
     * @param container Parent view that the fragment's UI will be attached to
     * @param savedInstanceState Bundle containing the saved state of the fragment
     * @return The created View for the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mood_history, container, false);


        listView = view.findViewById(R.id.history_list);
        moodHistoryList = new ArrayList<MoodEvent>();

        db = FirebaseFirestore.getInstance();

        Context context = getContext();
        if (context == null) return view;
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
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
            clearSavedFilters();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });



        ImageButton filter_btn = view.findViewById(R.id.filter_icon);

        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("espresso test", "clickedddd");

                FilterFragment filterFragment = new FilterFragment();
                Bundle args = new Bundle();
                args.putSerializable("moodEvents", moodHistoryList);
                filterFragment.setArguments(args);
                filterFragment.setTargetFragment(MoodHistoryFragment.this, 0);
                filterFragment.show(getParentFragmentManager(), "filter");
            }
        });


        return view;
    }

    /**
     * Fetches mood events for the logged-in user from Firestore.
     *
     * @param username The logged-in user's username
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
     * Fetches the mood event documents referenced by the user document.
     *
     * @param moodRefs List of Firestore document references to mood events
     */
    private void fetchMoodEvents(List<DocumentReference> moodRefs) {
        if (!isAdded()) return;

        ArrayList<MoodEvent> tempList = new ArrayList<>();
        final int[] fetchedCount = {0};
        final Source source = isNetworkAvailable() ?
                Source.DEFAULT :  // Correct value
                Source.CACHE;


        for (DocumentReference moodRef : moodRefs) {

            moodRef.get(source).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    try {
                        MoodEvent moodEvent = document.toObject(MoodEvent.class);
                        if (moodEvent != null) {
                            tempList.add(moodEvent);
                        }
                    } catch (Exception e) {
                        Log.e("moodhistory", "Error converting document", e);
                    }
                }

                fetchedCount[0]++;
                if (fetchedCount[0] == moodRefs.size()) {
                    moodHistoryList.clear();
                    moodHistoryList.addAll(tempList);

                    Display(moodHistoryList);
                }
            });

        }
    }

    /**
     * Checks network availability status.
     * @return true if device has active network connection, false otherwise
     */
    private boolean isNetworkAvailable() {
        Context context = getContext();
        return context != null && NetworkUtils.isNetworkAvailable(context);
    }


    /**
     * Sorts and displays the mood event list in reverse chronological order.
     *
     * @param moodHistoryList The list of mood events to display
     */
    private void Display(ArrayList<MoodEvent> moodHistoryList) {
        if (!isAdded()) return; //stop if fragment is not attached

        Context context = getContext();
        if (context == null) return; //no null pointer crash

        ArrayList<MoodEvent> sortedList = new ArrayList<>(moodHistoryList);
        sortedList.sort((event1, event2) -> {
            try {
                LocalDate date1 = parseStringToDate(event1.getDate());
                LocalDate date2 = parseStringToDate(event2.getDate());

                int dateCompare = date2.compareTo(date1);
                if (dateCompare != 0) return dateCompare;

                LocalTime time1 = parseStringToTime(event1.getTime());
                LocalTime time2 = parseStringToTime(event2.getTime());
                return time2.compareTo(time1);
            } catch (Exception e) {
                Log.e("Sorting", "Error comparing events", e);
                return 0;
            }
        });

        adapter = new MoodEventAdapter(context, sortedList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    /** Redirects the user to the login screen. */
    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    /**
     * Parses a date string into a {@link LocalDate}.
     *
     * @param dateString The date string in "dd/MM/yyyy" format
     * @return The parsed {@link LocalDate}, or {@link LocalDate#MIN} on error
     */
    public LocalDate parseStringToDate(String dateString) {
        try {
            // Use pattern matching for "DD-MM-YYYY" format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            Log.e("MoodHistory", "Invalid date format: " + dateString, e);
            return LocalDate.MIN;
        }
    }

    /**
     * Parses a date string into a {@link LocalDate}.
     *
     * @param timeString The date string in 'HH:mm[:ss]' format
     * @return The parsed {@link LocalTime}, or {@link LocalTime#MIN} on error
     */
    public LocalTime parseStringToTime(String timeString) {
        try {
            // Handle both with and without seconds
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm[:ss]");
            return LocalTime.parse(timeString, formatter);
        } catch (Exception e) {
            Log.e("MoodHistory", "Invalid time format: " + timeString, e);
            return LocalTime.MIN;
        }
    }

    /**
     * Clears the saved filter preferences when the dialog is closed.
     */
    private void clearSavedFilters() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Set filter values to null (use remove() to delete if required)
        editor.putString("keyword", null);
        editor.putString("timeFilter", null);
        editor.putString("selectedEmojis", null);

        editor.apply(); // Commit changes
    }

}

