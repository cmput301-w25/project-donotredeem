package com.example.donotredeem.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.donotredeem.LogIn;
import com.example.donotredeem.MainPageAdapter;
import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodEventAdapter;
import com.example.donotredeem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@code MainPage} class represents the main screen of the app after login.
 * It displays the logged-in user's name and provides a sign-out option.
 */
public class MainPage extends Fragment {

    private FirebaseFirestore db;
    private ListView Main_list;
    private MainPageAdapter main_page_adapter;
    private String loggedInUsername;
    private List<MoodEvent> MainMoodList;
    private ImageView searchBtn;

    /**
     * Called to instantiate the fragment's view.
     *
     * @param inflater  The LayoutInflater used to inflate the layout.
     * @param container The parent view that this fragment's UI should be attached to.
     * @param savedInstanceState If non-null, the fragment is being re-constructed from a previous state.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_page, container, false);

        db = FirebaseFirestore.getInstance();
        Button button = view.findViewById(R.id.temp_sign_out);
        //TextView textView = view.findViewById(R.id.user);
        MainMoodList = new ArrayList<MoodEvent>();
        Main_list =  view.findViewById(R.id.main_listView);
        searchBtn = view.findViewById(R.id.imageView4);

        // Retrieve the username from SharedPreferences (saved during login)
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
            // No user logged in, redirect to login screen
            redirectToLogin();
            return view; // Early return
        } else {
            // Fetch user details from Firestore
            Log.d("MainPageDebug", "Fetching user details for: " + loggedInUsername);
            FetchFollowingUsers(loggedInUsername);
        }

        button.setOnClickListener(view1 -> logout());

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new Explore())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }


//    private void FetchFollowingUsers(String username) {
//        if (username == null) {
//            Log.e("Main Page", "No username found in SharedPreferences");
//            return;
//        }
//
//        db.collection("User")
//                .whereEqualTo("username", username)
//                .addSnapshotListener((querySnapshot, error) -> {
//                    if (!querySnapshot.isEmpty()) {
//                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
//                        Log.d("Main Page", "User found: " + userDoc.getData());
//                        List<String> FollowedUsers = (List<String>) userDoc.get("following_list");
//                        if (FollowedUsers != null && !FollowedUsers.isEmpty()) {
//                            FetchPublicEvents(FollowedUsers);
//                        } else {
//                            Log.d("Main Page", "No followed users.");
//                            Display(new ArrayList<>());
//                        }
//                    } else {
//                        Log.e("Main Page", "No user found with username: " + username);
//                    }
//                });
//    }

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

//    private void FetchPublicEvents(List<String> FollowedUsers) {
//        if (!isAdded()) return; // Stop if fragment is not attached
//
//        ArrayList<MoodEvent> tempList = new ArrayList<>();
//        final int[] fetchedCount = {0}; // track total moods fetched
//
//        if (FollowedUsers.isEmpty()) {
//            Display(tempList);
//            return;
//        }
//
//        for (String FollowedUser : FollowedUsers) {
//            db.collection("User")
//                    .whereEqualTo("username", FollowedUser)
//                    .addSnapshotListener((querySnapshot, error) -> {
//                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                            DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
//                            List<DocumentReference> moodRefsList = (List<DocumentReference>) userDoc.get("MoodRef");
//
//                            if (moodRefsList != null && !moodRefsList.isEmpty()) {
//                                FetchMoods(moodRefsList, tempList, FollowedUsers.size(), fetchedCount);
//                            } else {
//                                fetchedCount[0]++;
//                                if (fetchedCount[0] == FollowedUsers.size()) {
//                                    Display(tempList);
//                                }
//                            }
//                        }
//                    });
//        }
//    }

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

                if (moodsFetched[0] == moodRefs.size()) { // All moods for this user are fetched
                    tempList.addAll(userMoodEvents);
                    fetchedCount[0]++;

                    if (fetchedCount[0] == totalUsers) {
                        Display(tempList);
                    }
                }
            });
        }
    }

//    private void FetchMoods(List<DocumentReference> moodRefs, ArrayList<MoodEvent> tempList, int totalUsers, int[] fetchedCount) {
//        ArrayList<MoodEvent> userMoodEvents = new ArrayList<>();
//        final int[] moodsFetched = {0}; // Moods of this user
//
//        if (moodRefs.isEmpty()) {
//            fetchedCount[0]++;
//            if (fetchedCount[0] == totalUsers) {
//                Display(tempList);
//            }
//            return;
//        }
//
//        for (DocumentReference moodRef : moodRefs) {
//            moodRef.addSnapshotListener((documentSnapshot, error) -> {
//                if (!isAdded()) return;
//
//                if (documentSnapshot != null && documentSnapshot.exists()) {
//                    try {
//                        MoodEvent moodEvent = documentSnapshot.toObject(MoodEvent.class);
//                        if (moodEvent != null && !moodEvent.getPrivacy()) {
//                            userMoodEvents.add(moodEvent);
//                        }
//                    } catch (Exception e) {
//                        Log.e("MoodHistory", "Error converting document", e);
//                    }
//                }
//
//                moodsFetched[0]++;
//
//                if (moodsFetched[0] == moodRefs.size()) { // All moods for this user are fetched
//                    tempList.addAll(userMoodEvents);
//                    fetchedCount[0]++;
//
//                    if (fetchedCount[0] == totalUsers) {
//                        Display(tempList);
//                    }
//                }
//            });
//        }
//    }


    /**
     * Fetches the user details from Firestore and updates the UI with the logged-in username.
     *
     * @param textView The TextView where the username should be displayed.
     */
    private void fetchUserDetails(TextView textView) {
        if (loggedInUsername == null) {
            Log.e("MainPageDebug", "loggedInUsername is null, redirecting to login");
            redirectToLogin();
            return;
        }

        DocumentReference userDocRef = db.collection("User").document(loggedInUsername);
        userDocRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        textView.setText(loggedInUsername);
                    } else {
                        Log.e("MainPageDebug", "User not found in Firestore");
                        redirectToLogin();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainPageDebug", "Firestore error: " + e.getMessage());
                    redirectToLogin();
                });
    }


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

        sortedList = new ArrayList<>(sortedList.subList(0, Math.min(3, sortedList.size())));


        main_page_adapter = new MainPageAdapter(context, sortedList);
        Main_list.setAdapter(main_page_adapter);
        main_page_adapter.notifyDataSetChanged();
    }


    private LocalDate parseStringToDate(String dateString) {
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

    /**
     * Logs out the user by signing out from Firebase and clearing login data.
     * Redirects the user to the login screen.
     */
    private void logout() {
        FirebaseAuth.getInstance().signOut();

        // Clear stored login data
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to login screen
        redirectToLogin();
    }

    /**
     * Redirects the user to the login screen and clears the activity stack.
     */
    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LogIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
