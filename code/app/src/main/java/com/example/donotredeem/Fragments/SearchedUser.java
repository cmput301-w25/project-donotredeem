package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.Classes.Users;
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
import java.util.List;

public class SearchedUser extends Fragment {

    private static final String ARG_USERNAME = "username";
    private String username;
    private Button Follow;
    private TextView usernameTextView, bioTextView, followersTextView, followingTextView;

    private ListView followerListView, followingListView, recent_list;
    private MoodEventAdapter adapter;
    private ArrayList<MoodEvent> moodHistoryList;
    private FirebaseFirestore db;


    public static SearchedUser newInstance(String username) {
        SearchedUser fragment = new SearchedUser();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.searched_user, container, false);
        Log.d("MyTag", "This is a debug message 111111111111.");

        recent_list = view.findViewById(R.id.recent_history);
        db = FirebaseFirestore.getInstance();
        moodHistoryList = new ArrayList<MoodEvent>();

        // Initialize TextViews
        usernameTextView = view.findViewById(R.id.textView2);
        bioTextView = view.findViewById(R.id.textView9);
        followersTextView = view.findViewById(R.id.textView7);
        followingTextView = view.findViewById(R.id.textView8);
        Follow = view.findViewById(R.id.button6);
        Log.d("MyTag", "This is a debug message 222222222222.");

//        // Initialize ListViews
//        followerListView = view.findViewById(R.id.follower_list);
//        followingListView = view.findViewById(R.id.following_list);

        // Fetch and display user data
        fetchUserData(username);


//        Follow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
//                String loggedInUsername = sharedPreferences.getString("username", null);
//
//                if (loggedInUsername == null) {
//                    Log.e("SearchedUser", "No user is logged in.");
//                    return;
//                }
//
//                UserProfileManager userProfileManager = new UserProfileManager();
//
//                // Check if the user is already following the searched user
//                userProfileManager.getUserProfileWithFollowers(loggedInUsername, new UserProfileManager.OnUserProfileFetchListener() {
//                    @Override
//                    public void onUserProfileFetched(Users currentUser) {
//                        if (currentUser != null) {
//                            List<String> followingList = currentUser.getFollowingList();
//
//                            if (followingList.contains(username)) {
//                                // User is already following the searched user -> Unfollow them
//                                unfollowUser(loggedInUsername, username);
//                            } else {
//                                // User is not following -> Send follow request or follow
//                                sendFollowRequest(loggedInUsername, username);
//                            }
//                        } else {
//                            Log.e("SearchedUser", "Error fetching current user data.");
//                        }
//                    }
//
//                    @Override
//                    public void onUserProfileFetchError(Exception e) {
//                        Log.e("SearchedUser", "Error fetching logged-in user data.", e);
//                    }
//                });
//            }
//        });
        Follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                final String loggedInUsername = sharedPreferences.getString("username", null);

                if (loggedInUsername == null) {
                    Log.e("SearchedUser", "No user is logged in.");
                    return;
                }

                final UserProfileManager userProfileManager = new UserProfileManager();

                // First check current follow status
                userProfileManager.getUserProfileWithFollowers(username, new UserProfileManager.OnUserProfileFetchListener() {
                    @Override
                    public void onUserProfileFetched(Users targetUser) {
                        if (targetUser != null) {
                            // Check if logged-in user is already following
                            if (targetUser.getFollowerList() != null &&
                                    targetUser.getFollowerList().contains(loggedInUsername)) {

                                // Unfollow logic
                                userProfileManager.removeFromFollowingList(loggedInUsername, username,
                                        new UserProfileManager.OnUpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                // Remove from target's followers
                                                userProfileManager.removeFromFollowersList(username, loggedInUsername,
                                                        new UserProfileManager.OnUpdateListener() {
                                                            @Override
                                                            public void onSuccess() {
                                                                updateUIAfterUnfollow();
                                                                fetchUserData(username); // Refresh data
                                                            }

                                                            @Override
                                                            public void onError(Exception e) {
                                                                handleError("Error removing from followers", e);
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                handleError("Error removing from following", e);
                                            }
                                        });
                            } else {
                                // Check if request already exists
                                if (targetUser.getRequests() != null &&
                                        targetUser.getRequests().contains(loggedInUsername)) {
                                    Follow.setText("Requested");
                                    Follow.setEnabled(false);
                                } else {
                                    // Send follow request
                                    userProfileManager.addToRequestsList(username, loggedInUsername,
                                            new UserProfileManager.OnUpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    Follow.setText("Requested");
                                                    Follow.setEnabled(false);
                                                    fetchUserData(username); // Refresh data
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    handleError("Error sending request", e);
                                                }
                                            });
                                }
                            }
                        }
                    }

                    @Override
                    public void onUserProfileFetchError(Exception e) {
                        handleError("Error fetching user data", e);
                    }
                });
            }
        });


        return view;
    }

    private void fetchUserData(String username) {
        if (username == null || username.isEmpty()) {
            Log.e("SearchedUser", "Username is null/empty");
            return;
        }
        Log.d("MyTag", "This is a debug message 333333333333.");


        UserProfileManager userProfileManager = new UserProfileManager();
        Log.d("MyTag", "This is a debug message44444444444444444.");
        userProfileManager.getUserProfileWithFollowers(username, new UserProfileManager.OnUserProfileFetchListener() {
            @Override
            public void onUserProfileFetched(Users user) {
                if (user != null) {
                    Log.d("MyTag", "This is a debug message5555555555555.");
                    // Set user details to TextViews
                    usernameTextView.setText(user.getUsername());
                    bioTextView.setText(user.getBio());
                    // Inside onUserProfileFetched in fetchUserData:
                    followersTextView.setText(String.valueOf(user.getFollowers())); // Convert int to String
                    followingTextView.setText(String.valueOf(user.getFollowing()));
                    Log.d("MyTag", "This is a debug message666666666666666.");
                    // Get logged-in user from SharedPreferences
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                    String loggedInUsername = sharedPreferences.getString("username", null);
                    Log.d("SearchedUser", "Logged-in username: " + loggedInUsername);


                    if (loggedInUsername != null) {
                        // Check follow status
                        List<String> followerList = user.getFollowerList() != null ? user.getFollowerList() : new ArrayList<>();
                        List<String> requestsList = user.getRequests() != null ? user.getRequests() : new ArrayList<>();
                        Log.d("SearchedUser", "Follower List: " + followerList.toString());

                        requireActivity().runOnUiThread(() -> {
                            if (followerList.contains(loggedInUsername)) {
                                Log.d("MyTag", "This is a debug message77777777777777777.");
                                // Already following
                                Follow.setText("Following");
                                fetchUserMoodEvents(username);
                                Follow.setEnabled(true);
                            } else if (requestsList.contains(loggedInUsername)) {
                                // Request pending
                                Follow.setText("Requested");
                                Follow.setEnabled(false);
                            } else {
                                // Not following
                                Follow.setText("Follow");
                                Follow.setEnabled(true);
                            }
                        });
                    }

//                    // Display follower and following lists
//                    updateListView(followerListView, user.getFollowerList(), "No followers");
//                    updateListView(followingListView, user.getFollowingList(), "Not following anyone");
                } else {
                    Log.e("SearchedUser", "User not found.");
                }
            }

            @Override
            public void onUserProfileFetchError(Exception e) {
                Log.e("SearchedUser", "Error fetching user data", e);
            }
        });
    }


    // Helper method to update UI after unfollow
    private void updateUIAfterUnfollow() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Follow.setText("Follow");
                // Update counts
                int currentFollowers = Integer.parseInt(followersTextView.getText().toString());
                followersTextView.setText(String.valueOf(currentFollowers - 1));
            });
        }
    }

    // Generic error handler
    private void handleError(String message, Exception e) {
        Log.e("SearchedUser", message, e);
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                // Show error to user if needed
                Follow.setEnabled(true);
            });
        }
    }


    private void fetchUserMoodEvents(String username) {
        if (username == null) {
            Log.e("MoodHistory", "No username found in SharedPreferences");
            return;
        }

        db.collection("User")
                .whereEqualTo("username", username)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        Log.d("MoodHistory", "User found: " + userDoc.getData());
                        List<DocumentReference> moodRefsList = (List<DocumentReference>) userDoc.get("MoodRef");
                        if (moodRefsList != null && !moodRefsList.isEmpty()) {
                            fetchMoodEvents(moodRefsList); // Fetch the referenced mood events
                        } else {
                            Log.d("MoodHistory", "No mood events found.");
                        }
                    } else {
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


                    sortMoodEvents();

                    // Keep only the 2 most recent
                    if (!moodHistoryList.isEmpty()) {
                        Log.e("PROFILE LIST", "fetchMoodEvents: not emprt" );
                        if (moodHistoryList.size() > 2) {
                            ArrayList<MoodEvent> RecentHistoryList = new ArrayList<MoodEvent>(moodHistoryList.subList(0, 2));
                            Display(RecentHistoryList);
                        }
                        else {Display(moodHistoryList);}}
                    else {
                        Log.e("EMPTY_LIST", "fetchMoodEvents: LIST IS EMPTY");
                    }




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
        adapter = new MoodEventAdapter(getContext(), sortedList);
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

//    // Method to send a follow request or follow the user
//    private void sendFollowRequest(String loggedInUsername, String targetUsername) {
//        UserProfileManager userProfileManager = new UserProfileManager();
//
//        // Add the target user to following_list of the current user
//        userProfileManager.addToFollowingList(loggedInUsername, targetUsername, new UserProfileManager.OnUpdateListener() {
//            @Override
//            public void onSuccess() {
//                // Add the current user to followers_list of the target user
//                userProfileManager.addToFollowersList(targetUsername, loggedInUsername, new UserProfileManager.OnUpdateListener() {
//                    @Override
//                    public void onSuccess() {
//                        Log.d("SearchedUser", "Successfully followed the user.");
//                        Follow.setText("Following"); // Update button text
//                        Follow.setEnabled(true);
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        Log.e("SearchedUser", "Error adding to followers list.", e);
//                    }
//                });
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e("SearchedUser", "Error adding to following list.", e);
//            }
//        });
//    }
//
//    // Method to unfollow the user
//    private void unfollowUser(String loggedInUsername, String targetUsername) {
//        UserProfileManager userProfileManager = new UserProfileManager();
//
//        // Remove target user from the following_list of the current user
//        userProfileManager.removeFromFollowingList(loggedInUsername, targetUsername, new UserProfileManager.OnUpdateListener() {
//            @Override
//            public void onSuccess() {
//                // Remove current user from followers_list of the target user
//                userProfileManager.removeFromFollowersList(targetUsername, loggedInUsername, new UserProfileManager.OnUpdateListener() {
//                    @Override
//                    public void onSuccess() {
//                        Log.d("SearchedUser", "Successfully unfollowed the user.");
//                        Follow.setText("Follow"); // Update button text
//                        Follow.setEnabled(true);
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        Log.e("SearchedUser", "Error removing from followers list.", e);
//                    }
//                });
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e("SearchedUser", "Error removing from following list.", e);
//            }
//        });
//    }
//
//    private void updateListView(ListView listView, List<String> dataList, String emptyMessage) {
//        if (dataList != null && !dataList.isEmpty()) {
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
//                    android.R.layout.simple_list_item_1, dataList);
//            listView.setAdapter(adapter);
//        } else {
//            // Show empty message if list is empty
//            listView.setAdapter(new ArrayAdapter<>(requireContext(),
//                    android.R.layout.simple_list_item_1, new String[]{emptyMessage}));
//        }
//    }
}
