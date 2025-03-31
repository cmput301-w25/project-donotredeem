package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.donotredeem.Classes.User;
import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.MainPageAdapter;
import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SearchedUser extends Fragment {

    private static final String ARG_USERNAME = "username";
    private String username;
    private Button Follow;
    private TextView usernameTextView, bioTextView, followersTextView, followingTextView, moodTextView;
    private ImageView profileImage, cancel_button;

    private ListView followerListView, followingListView, recent_list;
    private LinearLayout follower, following;
    private MainPageAdapter adapter;
    private ArrayList<MoodEvent> moodHistoryList;
    private FirebaseFirestore db;
    private ListenerRegistration userDataListener; // Add this line


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

        recent_list = view.findViewById(R.id.recent_history);
        db = FirebaseFirestore.getInstance();
        moodHistoryList = new ArrayList<MoodEvent>();

        // Initialize TextViews
        usernameTextView = view.findViewById(R.id.textView2);
        bioTextView = view.findViewById(R.id.textView10);
        followersTextView = view.findViewById(R.id.textView7);
        followingTextView = view.findViewById(R.id.textView8);
        moodTextView = view.findViewById(R.id.textView9);
        Follow = view.findViewById(R.id.button6);
        follower = view.findViewById(R.id.followerLayout);
        following = view.findViewById(R.id.followingLayout);
        profileImage = view.findViewById(R.id.user_icon);
        cancel_button = view.findViewById(R.id.imageView9);


        cancel_button.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        // Fetch and display user data
        fetchUserData(username);



        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfUserFollows(username, new OnFollowCheckListener() {
                    @Override
                    public void onFollowCheck(boolean isFollowing) {
                        if (isFollowing) {
                            FollowerFragment followerFragment = FollowerFragment.newInstance(username);
                            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                            transaction.add(R.id.fragment_container, followerFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    }
                });

            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfUserFollows(username, new OnFollowCheckListener() {
                    @Override
                    public void onFollowCheck(boolean isFollowing) {
                        if (isFollowing) {
                            FollowingFragment followingFragment = FollowingFragment.newInstance(username);
                            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                            transaction.add(R.id.fragment_container, followingFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    }
                });
            }
        });


        Follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAdded()) return; // Add this check
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
                    public boolean onUserProfileFetched(User targetUser) {
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
                                    userProfileManager.declineFollowRequest(username, loggedInUsername,
                                            new UserProfileManager.OnUpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    Follow.setText("Follow");
                                                    Follow.setEnabled(true);
                                                }
                                                @Override
                                                public void onError(Exception e) {
                                                    handleError("Error removing request", e);
                                                }
                                            });

                                } else {
                                    // Send follow request
                                    userProfileManager.addToRequestsList(username, loggedInUsername,
                                            new UserProfileManager.OnUpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    Follow.setText("Requested");
                                                    Follow.setEnabled(true);
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
                        return false;
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
    if (db == null) {
        db = FirebaseFirestore.getInstance(); // Reinitialize if null
    }
    DocumentReference userRef = db.collection("User").document(username);

    // Add metadata listener for offline support
    userDataListener = userRef.addSnapshotListener(MetadataChanges.INCLUDE, (documentSnapshot, error) -> {
        if (!isAdded()) return; // Critical check here
        if (error != null) {
            Log.e("ProfilePage", "Listen error", error);
            return;
        }

        if (documentSnapshot != null && documentSnapshot.exists()) {
            boolean isFromCache = documentSnapshot.getMetadata().isFromCache();

            // Parse with new constructor
            int moods = documentSnapshot.getLong("moods") != null ?
                    documentSnapshot.getLong("moods").intValue() : 0;

            List<DocumentReference> moodRefs = (List<DocumentReference>)
                    documentSnapshot.get("MoodRef");

            User user = new User(
                    documentSnapshot.getId(),
                    documentSnapshot.getString("bio"),
                    documentSnapshot.getString("pfp"),
                    (List<String>) documentSnapshot.get("follower_list"),
                    (List<String>) documentSnapshot.get("following_list"),
                    (List<String>) documentSnapshot.get("requests"),
                    (List<DocumentReference>) documentSnapshot.get("MoodRef"),
                    moods

            );

            // Update UI
            usernameTextView.setText(user.getUsername());
            bioTextView.setText(user.getBio());
            // Inside onUserProfileFetched in fetchUserData:
            followersTextView.setText(String.valueOf(user.getFollowers())); // Convert int to String
            Log.d("MyTag", "User following count: " + user.getFollowing());
            followingTextView.setText(String.valueOf(user.getFollowing()));

            String profilePicUrl = user.getProfilePictureUrl();
            Log.d("ProfilePicUrl", "URL: " + profilePicUrl);

            moodTextView.setText(String.valueOf(user.getMoods()));
            if (profilePicUrl != null  && !profilePicUrl.isEmpty()) {
                Log.d("pls", "onUserProfileFetched: bro this is not null");
                Context context = getContext();
                if (context != null) {
                    Glide.with(requireContext())
                            .load(user.getProfilePictureUrl())
                            //                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(new RequestOptions().circleCrop())
                            .into(profileImage);}
            } else {
                profileImage.setImageResource(R.drawable.user);
            }
            if (isAdded()) {
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
                            fetchUserMoodEvents(username, 1);
                            Follow.setEnabled(true);
                        } else if (requestsList.contains(loggedInUsername)) {
                            // Request pending
                            Follow.setText("Requested");
                            Follow.setEnabled(true);
                        } else {
                            // Not following
                            Follow.setText("Follow");
                            Follow.setEnabled(true);
                            fetchUserMoodEvents(username, 0);
                        }
                    });
                }
            }
        }
    });
}


    // Helper method to update UI after unfollow
    private void updateUIAfterUnfollow() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Follow.setText("Follow");
                Follow.setEnabled(true);
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


    private void fetchUserMoodEvents(String username, int display) {
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
                            fetchMoodEvents(moodRefsList, display); // Fetch the referenced mood events
                        } else {
                            Log.d("MoodHistory", "No mood events found.");
                        }
                    } else {
                        Log.e("MoodHistory", "No user found with username: " + username);
                    }
                });
    }

    private void fetchMoodEvents(List<DocumentReference> moodRefs, int display) {
        ArrayList<MoodEvent> tempList = new ArrayList<>();
        final int[] fetchedCount = {0};

        for (DocumentReference moodRef : moodRefs) {
            moodRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    try {
                        MoodEvent moodEvent = document.toObject(MoodEvent.class);
                        if (moodEvent != null) {
                            tempList.add(moodEvent);
                        }
                    } catch (Exception e) {
                        Log.e("ProfilePage", "Error converting document", e);
                    }
                }

                fetchedCount[0]++;
                if (fetchedCount[0] == moodRefs.size()) {
                    moodHistoryList.clear();
                    if(display == 1) {
                        moodHistoryList.addAll(tempList);
                        sortMoodEvents();
                    }
                    if (isAdded() && getContext() != null) { // Check fragment attachment
                        if (moodHistoryList.size() > 2) {
                            ArrayList<MoodEvent> RecentHistoryList = new ArrayList<>(moodHistoryList.subList(0, 2));
                            Display(RecentHistoryList);
                        } else {
                            Display(moodHistoryList);
                        }
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
        if (!isAdded() || getContext() == null) {
            // Fragment is not attached to an activity
            return;
        }

        // Create a defensive copy and sort
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
                return 0;
            }
        });

        // Update adapter with valid context
        adapter = new MainPageAdapter(requireActivity(), moodHistoryList);
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
     * Callback to handle follow check result.
     */
    public interface OnFollowCheckListener {
        void onFollowCheck(boolean isFollowing);
    }


    /**
     * Checks if the logged-in user follows the target user asynchronously.
     *
     * @param targetUsername The username of the target user.
     * @param callback       A callback that returns true if the user is following, false otherwise.
     */
    /**
     * Checks if the logged-in user follows the target user asynchronously.
     *
     * @param targetUsername The username of the target user.
     * @param callback       A callback that returns true if the user is following, false otherwise.
     */
    private void checkIfUserFollows(String targetUsername, final OnFollowCheckListener callback) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        final String loggedInUsername = sharedPreferences.getString("username", null);

        if (loggedInUsername == null) {
            Log.e("SearchedUser", "No user is logged in.");
            callback.onFollowCheck(false); // Return false if no user is logged in
            return;
        }

        final UserProfileManager userProfileManager = new UserProfileManager();

        userProfileManager.getUserProfileWithFollowers(targetUsername, new UserProfileManager.OnUserProfileFetchListener() {
            @Override
            public boolean onUserProfileFetched(User targetUser) {
                if (targetUser != null && targetUser.getFollowerList() != null) {
                    boolean isFollowing = targetUser.getFollowerList().contains(loggedInUsername);
                    callback.onFollowCheck(isFollowing); // Return true/false via callback
                } else {
                    callback.onFollowCheck(false);
                }
                return false;
            }

            @Override
            public void onUserProfileFetchError(Exception e) {
                Log.e("SearchedUser", "Error fetching user data", e);
                callback.onFollowCheck(false); // Return false on error
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userDataListener != null) {
            userDataListener.remove();
            userDataListener = null;
        }
    }
}
