package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.donotredeem.LogIn;
import com.example.donotredeem.MainPageAdapter;
import com.example.donotredeem.MoodEvent;

import com.example.donotredeem.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying a feed of followed users' public mood events with filtering
 * and pagination capabilities. Handles real-time updates from Firestore and
 * maintains multiple list states for UI interactions.
 */
public class MainPage extends Fragment implements FilterFragment.FilterMoodListener, Serializable {

    private FirebaseFirestore db;
    private ListView Main_list;
    private MainPageAdapter main_page_adapter;
    private String loggedInUsername;
    private ArrayList<MoodEvent> originalMoodList = new ArrayList<>();
    private ArrayList<MoodEvent> displayedMoodList = new ArrayList<>();
    private ArrayList<MoodEvent> backupMoodList = new ArrayList<>();

    private TextView viewMoreBtn, viewLessBtn, filterBtn;
    private ImageView searchBtn;

    /**
     * Applies mood filters from FilterFragment and updates UI state
     * @param filteredList Filtered mood events to display
     */
    @Override
    public void filterMood(ArrayList<MoodEvent> filteredList) {

        backupMoodList.clear();
        backupMoodList.addAll(filteredList);

        main_page_adapter = new MainPageAdapter(getContext(), backupMoodList);
        Main_list.setAdapter(main_page_adapter);

        if (!backupMoodList.equals(displayedMoodList)) {
            viewMoreBtn.setVisibility(View.GONE);
            viewLessBtn.setVisibility(View.GONE);
        } else {
            viewMoreBtn.setVisibility(displayedMoodList.size() == 3 ? View.VISIBLE : View.GONE);
            viewLessBtn.setVisibility(displayedMoodList.size() > 3 ? View.VISIBLE : View.GONE);
        }

    }

    /**
     * Initializes fragment view and core functionality:
     * - Sets up ListView and adapters
     * - Configures button click handlers
     * - Loads user authentication state
     * - Initiates mood event loading process
     *
     * @param inflater Layout inflater service
     * @param container Parent view container
     * @param savedInstanceState Persisted state bundle
     * @return Configured view hierarchy
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_page, container, false);

        db = FirebaseFirestore.getInstance();

        Main_list =  view.findViewById(R.id.main_listView);
        searchBtn = view.findViewById(R.id.imageView4);
        viewMoreBtn = view.findViewById(R.id.view_more_button);
        viewLessBtn = view.findViewById(R.id.view_less_button);
        filterBtn = view.findViewById(R.id.advanced_options);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("username")) {
            Log.e("MainPageDebug", "Username not found in SharedPreferences, redirecting to login");
            redirectToLogin();
            return view; // Early return to stop further processing
        } else {
            loggedInUsername = sharedPreferences.getString("username", null);
            Log.d("MainPageDebug", "Retrieved username: " + loggedInUsername);
        }

        if (loggedInUsername == null) {
            redirectToLogin();
            return view;

        } else {
            Log.d("MainPageDebug", "Fetching user details for: " + loggedInUsername);
            FetchFollowingUsers(loggedInUsername);
        }


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .hide(MainPage.this)
                        .add(R.id.fragment_container, new Explore())
                        .addToBackStack(null)
                        .commit();

            }
        });

        viewMoreBtn.setOnClickListener(view1 ->  {

            displayedMoodList.clear();
            displayedMoodList.addAll(originalMoodList);
            updateDisplayedMoods();

        });

        viewLessBtn.setOnClickListener(view1 -> {

            displayedMoodList.clear();
            displayedMoodList.addAll(originalMoodList.subList(0,3));
            updateDisplayedMoods();

        });


        filterBtn.setOnClickListener(view1 -> {


            if (displayedMoodList.size() <= 3) {
                displayedMoodList.clear();
                displayedMoodList.addAll(originalMoodList.subList(0, Math.min(3, originalMoodList.size())));

            } else {
                displayedMoodList.clear();
                displayedMoodList.addAll(originalMoodList);
            }

            FilterFragment filterFragment = new FilterFragment();
            Bundle args = new Bundle();
            args.putSerializable("moodEvents", displayedMoodList);
            filterFragment.setArguments(args);
            filterFragment.setTargetFragment(MainPage.this, 0);
            filterFragment.show(getParentFragmentManager(), "filter");

        });

        return view;
    }

    /**
     * Fetches followed users list from Firestore
     * @param username Current user's identifier
     */
    private void FetchFollowingUsers(String username) {
        if (username == null) {
            Log.e("Main Page", "No username found in SharedPreferences");
            return;
        }

        db.collection("User")
                .whereEqualTo("username", username)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        Log.d("Main Page", "User found: " + userDoc.getData());
                        List<String> FollowedUsers = (List<String>) userDoc.get("following_list");
                        if (FollowedUsers != null && !FollowedUsers.isEmpty()) {
                            FetchPublicEvents(FollowedUsers);
                        } else {
                            Log.d("Main Page", "No followed users.");
                            Display(new ArrayList<>());
                        }
                    } else {
                        Log.e("Main Page", "No user found with username: " + username);
                    }
                });
    }

    /**
     * Retrieves public mood events from followed users
     * @param FollowedUsers List of usernames to fetch events from
     */
    private void FetchPublicEvents(List<String> FollowedUsers) {
        if (!isAdded()) return; // Stop if fragment is not attached

        ArrayList<MoodEvent> tempList = new ArrayList<>();
        final int[] fetchedCount = {0}; // track total moods fetched

        if (FollowedUsers.isEmpty()) {
            Display(tempList);
            return;
        }

        for (String FollowedUser : FollowedUsers) {
            db.collection("User")
                    .whereEqualTo("username", FollowedUser)
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                            List<DocumentReference> moodRefsList = (List<DocumentReference>) userDoc.get("MoodRef");

                            if (moodRefsList != null && !moodRefsList.isEmpty()) {
                                FetchMoods(moodRefsList, tempList, FollowedUsers.size(), fetchedCount);
                            } else {
                                fetchedCount[0]++;
                                if (fetchedCount[0] == FollowedUsers.size()) {
                                    Display(tempList);
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Processes mood references into MoodEvent objects
     * @param moodRefs Firestore document references
     * @param tempList Temporary event storage
     * @param totalUsers Total followed users count
     * @param fetchedCount Completion tracker
     */
    private void FetchMoods(List<DocumentReference> moodRefs, ArrayList<MoodEvent> tempList, int totalUsers, int[] fetchedCount) {
        ArrayList<MoodEvent> userMoodEvents = new ArrayList<>();
        final int[] moodsFetched = {0}; // Moods of this user

        if (moodRefs.isEmpty()) {
            fetchedCount[0]++;
            if (fetchedCount[0] == totalUsers) {
                Display(tempList);
            }
            return;
        }

        for (DocumentReference moodRef : moodRefs) {
            moodRef.addSnapshotListener((documentSnapshot, error) -> {
                if (!isAdded()) return;

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    try {
                        MoodEvent moodEvent = documentSnapshot.toObject(MoodEvent.class);
                        if (moodEvent != null && !moodEvent.getPrivacy()) {
                            userMoodEvents.add(moodEvent);
                        }
                    } catch (Exception e) {
                        Log.e("MoodHistory", "Error converting document", e);
                    }
                }

                moodsFetched[0]++;

                if (moodsFetched[0] == moodRefs.size()) {
                    tempList.addAll(userMoodEvents);
                    fetchedCount[0]++;

                    if (fetchedCount[0] == totalUsers) {
                        Display(tempList);
                    }
                }
            });
        }
    }

    /**
     * Updates UI with processed mood events:
     * - Sorts events chronologically
     * - Maintains original/filtered states
     * - Manages list pagination
     * @param moodHistoryList Raw list of mood events
     */
    private void Display(ArrayList<MoodEvent> moodHistoryList) {
        if (!isAdded()) return;

        Context context = getContext();
        if (context == null) return; // no null pointer crash

        ArrayList<MoodEvent> sortedList = new ArrayList<>(moodHistoryList);
        sortedList.sort((event1, event2) -> {
            try {
                LocalDate date1 = parseStringToDate(event1.getDate());
                LocalDate date2 = parseStringToDate(event2.getDate());

                int dateCompare = date2.compareTo(date1);
                if (dateCompare != 0) return dateCompare;

                LocalTime time1 = parseStringToTime(event1.getTime());
                LocalTime time2 = parseStringToTime(event2.getTime());
                return time2.compareTo(time1); // Latest time first
            } catch (Exception e) {
                Log.e("Sorting", "Error comparing events", e);
                return 0; // If any error occurs, return 0 (no change)
            }
        });

        if (originalMoodList.isEmpty()){
            originalMoodList.addAll(sortedList);

        }


        displayedMoodList.clear();
        displayedMoodList.addAll(originalMoodList.subList(0, Math.min(3, originalMoodList.size())));

        updateDisplayedMoods();
    }

    /**
     * Converts date string to LocalDate object
     * @param dateString Date in "dd/MM/yyyy" format
     * @return Parsed date or MIN on error
     */
    public LocalDate parseStringToDate(String dateString) {
        try {
            // Use pattern matching for "DD-MM-YYYY" format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            return LocalDate.MIN;
        }
    }

    /**
     * Converts time string to LocalTime object
     * @param timeString Time in "HH:mm[:ss]" format
     * @return Parsed time or MIN on error
     */
    public LocalTime parseStringToTime(String timeString) {
        try {
            // Handle both with and without seconds
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm[:ss]");
            return LocalTime.parse(timeString, formatter);
        } catch (Exception e) {
            return LocalTime.MIN;
        }
    }

    /**
     * Handles authentication failure by clearing credentials
     * and redirecting to login screen
     */
    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LogIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * Synchronizes UI with current mood event display state:
     * - Updates list adapter
     * - Toggles pagination controls
     */
    private void updateDisplayedMoods() {

        main_page_adapter = new MainPageAdapter(getContext(), displayedMoodList);
        Main_list.setAdapter(main_page_adapter);

        viewMoreBtn.setVisibility(displayedMoodList.size() == 3 ? View.VISIBLE : View.GONE);
        viewLessBtn.setVisibility(displayedMoodList.size() > 3 ? View.VISIBLE : View.GONE);
    }

    /**
     * Fragment lifecycle callback that cleans up filter preferences when the view is destroyed.
     * Ensures stale filters don't persist between fragment instances by:
     * - Clearing SharedPreferences filter values
     * - Resetting keyword, time, and emoji filters
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearSavedFilters();
    }

    /**
     * Clears persisted filter preferences on fragment destruction
     */
    private void clearSavedFilters() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("keyword", null);
        editor.putString("timeFilter", null);
        editor.putString("selectedEmojis", null);

        editor.apply();

    }
}
