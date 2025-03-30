package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import com.example.donotredeem.Classes.NetworkUtils;
import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.Classes.Users;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.donotredeem.LogIn;
import com.example.donotredeem.MainActivity;
import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodEventAdapter;
import com.example.donotredeem.MoodJarFragment;
import com.example.donotredeem.QRCodeFragment;
import com.example.donotredeem.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Source;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that represents the user's profile page. It displays the user's recent mood history
 * and provides navigation to other sections of the app, such as editing the profile and viewing mood history.
 * This fragment interacts with Firebase Firestore to fetch user mood events and displays them in a ListView.
 */
public class ProfilePage extends Fragment {
    private ListView recent_list;
    private MoodEventAdapter adapter;
    float originalX, originalY;
    private ArrayList<MoodEvent> moodHistoryList;
    private FirebaseFirestore db;
    private String loggedInUsername;
    private ImageView profileImage;
    private String username;
    private Button Follow;
    private TextView usernameTextView, bioTextView, followersTextView, followingTextView, moodTextView;
    private LinearLayout follower, following, mood;



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

        usernameTextView = view.findViewById(R.id.textView2);
        bioTextView = view.findViewById(R.id.textView9);
        followersTextView = view.findViewById(R.id.textView5);
        followingTextView = view.findViewById(R.id.textView02);
        moodTextView = view.findViewById(R.id.textView8);
        follower = view.findViewById(R.id.followerLayout);
        following = view.findViewById(R.id.followingLayout);
        mood = view.findViewById(R.id.moodLayout);
        profileImage = view.findViewById(R.id.user_icon);
//        Follow = view.findViewById(R.id.button6);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        fetchUserData(username);

        MainActivity.past_location = "moodhistory";

        recent_list = view.findViewById(R.id.recent_history);
        db = FirebaseFirestore.getInstance();
        moodHistoryList = new ArrayList<MoodEvent>();

        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
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

        adapter = new MoodEventAdapter(requireActivity(), moodHistoryList);
        recent_list.setAdapter(adapter);

        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowerFragment followerFragment = FollowerFragment.newInstance(username);
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container, followerFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowingFragment followingFragment = FollowingFragment.newInstance(username);
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.fragment_container, followingFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment existingFragment = fragmentManager.findFragmentByTag("moodhistory");

                if (existingFragment == null) {

                    moodhistory historyFragment = new moodhistory();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, historyFragment, "moodhistory")
                            .addToBackStack(null)
                            .commit();
                } else {
                    fragmentManager.popBackStack("moodhistory", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });

        ImageView side_panel = view.findViewById(R.id.side_panel_button);

//        side_panel.setOnClickListener(v -> {
//             FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//                transaction.setCustomAnimations(R.anim.panel_slide_in, 0);
//
//                SidePanel sidePanelFragment = new SidePanel();
//                transaction.replace(R.id.profile_container, sidePanelFragment, "SidePanel");
//                transaction.addToBackStack(null);
//                transaction.commit();
//
//        });
        side_panel.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Set both enter and exit animations
            transaction.setCustomAnimations(
                    R.anim.panel_slide_in,  // Enter animation
                    0,                       // Exit animation (for current fragment)
                    0,                       // Pop enter animation
                    R.anim.panel_slide_out   // Pop exit animation
            );

            SidePanel sidePanelFragment = new SidePanel();
            transaction.replace(R.id.profile_container, sidePanelFragment, "SidePanel");
            transaction.addToBackStack(null);
            transaction.commit();
        });

//        sidePanel.findViewById(R.id.nav_analytics).setOnClickListener(v -> {
//            drawerLayout.closeDrawer(sidePanel);
//            navigateToFragment(new AnalyticsFragment(), "Analytics");
//        });


//        DrawerLayout drawerLayout = view.findViewById(R.id.drawer_layout);
//        LinearLayout sidePanel = view.findViewById(R.id.side_panel);
//
//        view.findViewById(R.id.side_panel_button).setOnClickListener(v -> {
//            // Open the drawer (side panel)
//            drawerLayout.openDrawer(sidePanel); //side panel box
//        });
//
//        sidePanel.findViewById(R.id.panel_close).setOnClickListener(v -> {
//            drawerLayout.closeDrawer(sidePanel); //side panel closing
//        });
//
//        sidePanel.findViewById(R.id.nav_history).setOnClickListener(v -> {
//            drawerLayout.closeDrawer(sidePanel);
//
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            Fragment existingFragment = fragmentManager.findFragmentByTag("moodhistory");
//
//            if (existingFragment == null) {
//
//                moodhistory historyFragment = new moodhistory();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, historyFragment, "moodhistory")
//                        .addToBackStack(null)
//                        .commit();
//            } else {
//                fragmentManager.popBackStack("moodhistory", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            }
//        });
//
//        sidePanel.findViewById(R.id.nav_profile).setOnClickListener(v -> {
//            drawerLayout.closeDrawer(sidePanel); //edit profile in panel
//
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            Fragment existingFragment = fragmentManager.findFragmentByTag("EditProfile");
//
//            if (existingFragment == null) {
//                EditProfile profileFragment = new EditProfile();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, profileFragment, "EditProfile")
//                        .addToBackStack(null)
//                        .commit();
//            } else {
//                fragmentManager.popBackStack("EditProfile", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            }
//        });
//
//        // Add QR Code Navigation
//        sidePanel.findViewById(R.id.nav_qr_code).setOnClickListener(v -> {
//            drawerLayout.closeDrawer(sidePanel);
//            navigateToFragment(new QRCodeFragment(), "QRCode");
//        });
//
//        sidePanel.findViewById(R.id.nav_mood_jar).setOnClickListener(v -> {
//            drawerLayout.closeDrawer(sidePanel);
//            navigateToFragment(new MoodJarFragment(), "MoodJar");
//        });
//
//
        return view;
    }

//    private void fetchUserData(String username) {
//        if (username == null || username.isEmpty()) {
//            Log.e("SearchedUser", "Username is null/empty");
//            return;
//        }
//        Log.d("MyTag", "This is a debug message 333333333333.");
//
//
//        UserProfileManager userProfileManager = new UserProfileManager();
//        Log.d("MyTag", "This is a debug message44444444444444444.");
//        userProfileManager.getUserProfileWithFollowers(username, new UserProfileManager.OnUserProfileFetchListener() {
//            @Override
//            public boolean onUserProfileFetched(Users user) {
//                if (user != null) {
//                    Log.d("MyTag", "This is a debug message5555555555555.");
//                    // Set user details to TextViews
//                    usernameTextView.setText(user.getUsername());
//                    bioTextView.setText(user.getBio());
//                    // Inside onUserProfileFetched in fetchUserData:
//                    followersTextView.setText(String.valueOf(user.getFollowers())); // Convert int to String
//                    Log.d("MyTag", "User following count: " + user.getFollowing());
//                    followingTextView.setText(String.valueOf(user.getFollowing()));
//                    moodTextView.setText(String.valueOf(user.getMoods()));
//
//                    String profilePicUrl = user.getProfilePictureUrl();
//                    Log.d("ProfilePicUrl", "URL: " + profilePicUrl);
//
//
//                    if (profilePicUrl != null  && !profilePicUrl.isEmpty()) {
//                        Log.d("pls", "onUserProfileFetched: bro this is not null");
//                        Context context = getContext();
//                        if (context != null) {
//                        Glide.with(requireContext())
//                                .load(user.getProfilePictureUrl())
////                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .apply(new RequestOptions().circleCrop())
//                                .into(profileImage);}
//                    } else {
//                        profileImage.setImageResource(R.drawable.user);
//                    }
//
////                    final boolean[] isExpanded = {false};
////
////                    profileImage.setOnClickListener(v -> {
////                        if (!isExpanded[0]) {
////                            // Bring the profileImage to the front
////                            profileImage.bringToFront();
////
////                            // Get the center of the screen
////                            int screenWidth = requireActivity().getWindow().getDecorView().getWidth();
////                            int screenHeight = requireActivity().getWindow().getDecorView().getHeight();
////                            int centerX = (screenWidth - profileImage.getWidth()) / 2;
////                            int centerY = (screenHeight - profileImage.getHeight()) / 2;
////
////                            // Move the imageView to the center
////                            profileImage.animate()
////                                    .x(centerX)
////                                    .y(centerY)
////                                    .setDuration(300)
////                                    .start();
////
////                            Animation zoomIn = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_in);
////                            profileImage.startAnimation(zoomIn);
////
////                            isExpanded[0] = true;
////                        }
////                    });
////
////                    View rootView = requireActivity().findViewById(R.id.rootView);
////
////                    rootView.setOnClickListener(v -> {
////                        if (isExpanded[0]) {
////                            // Move the image back to its original position
////                            profileImage.animate()
////                                    .x(originalX)
////                                    .y(originalY)
////                                    .setDuration(300)
////                                    .start();
////
////
////                            Animation zoomOut = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_out);
////                            profileImage.startAnimation(zoomOut);
////
////                            // Reset state
////                            isExpanded[0] = false;
////                        }
////                    });
//
//
//
//                    Log.d("MyTag", "This is a debug message666666666666666.");
//
////                    // Display follower and following lists
////                    updateListView(followerListView, user.getFollowerList(), "No followers");
////                    updateListView(followingListView, user.getFollowingList(), "Not following anyone");
//                } else {
//                    Log.e("SearchedUser", "User not found.");
//                }
//                return false;
//            }
//
//            @Override
//            public void onUserProfileFetchError(Exception e) {
//                Log.e("SearchedUser", "Error fetching user data", e);
//            }
//        });
//    }
    private void fetchUserData(String username) {
        if (db == null) {
            db = FirebaseFirestore.getInstance(); // Reinitialize if null
        }
        DocumentReference userRef = db.collection("User").document(username);

        // Add metadata listener for offline support
        userRef.addSnapshotListener(MetadataChanges.INCLUDE, (documentSnapshot, error) -> {
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

                Users user = new Users(
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

    //            if (isFromCache) {
    //                Toast.makeText(getContext(),
    //                        "Offline - showing cached data",
    //                        Toast.LENGTH_SHORT).show();
    //            }
                if (isAdded() && getContext() != null) {
                    if (isFromCache) {
                        Toast.makeText(requireContext(),
                                "Offline - cached data",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Fetches mood events for the logged-in user from Firebase Firestore.
     *
     * @param username The username of the logged-in user
     */
//    private void fetchUserMoodEvents(String username) {
//        if (username == null) {
//            Log.e("MoodHistory", "No username found in SharedPreferences");
//            return;
//        }
//
//        db.collection("User")
//                .whereEqualTo("username", username)
//                .addSnapshotListener((querySnapshot, error) -> {
//                    if (!querySnapshot.isEmpty()) {
//                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
//                        Log.d("MoodHistory", "User found: " + userDoc.getData());
//                        List<DocumentReference> moodRefsList = (List<DocumentReference>) userDoc.get("MoodRef");
//                        if (moodRefsList != null && !moodRefsList.isEmpty()) {
//
//                            fetchMoodEvents(moodRefsList); // Fetch the referenced mood events
//                        }
//                        else {
//                            Log.d("MoodHistory", "No mood events found.");
//                        }
//                    }
//
//                    else {
//                        Log.e("MoodHistory", "No user found with username: " + username);
//                    }
//                });
//
//    }

    /**
     * Fetches mood events from the provided list of document references.
     * The events are then added to the moodHistoryList and displayed.
     *
     * @param moodRefs The list of document references for mood events
     */
//    private void fetchMoodEvents(List<DocumentReference> moodRefs) {
//        ArrayList<MoodEvent> tempList = new ArrayList<>();
//        final int[] fetchedCount = {0}; // Counter to track fetched events
//
//        for (DocumentReference moodRef : moodRefs) {
//            moodRef.get().addOnSuccessListener(documentSnapshot -> {
//                if (documentSnapshot.exists()) {
//                    try {
//                        MoodEvent moodEvent = documentSnapshot.toObject(MoodEvent.class);
//                        if (moodEvent != null) {
//                            tempList.add(moodEvent);
//                        }
//                    } catch (Exception e) {
//                        Log.e("MoodHistory", "Error converting document", e);
//                    }
//                }
//
//                fetchedCount[0]++;
//                if (fetchedCount[0] == moodRefs.size()) {
//                    // All events fetched, now sort and display
//                    moodHistoryList.clear();
//                    moodHistoryList.addAll(tempList);
//
//                    // Sort first
//                    sortMoodEvents();
//
//                    // Keep only the 2 most recent
//                    if (moodHistoryList.size() >2) {
//                        moodHistoryList = new ArrayList<>(moodHistoryList.subList(0, 2));
//                    }
//
//
//                    Display( moodHistoryList);
//                }
//            }).addOnFailureListener(e -> {
//                Log.e("MoodHistory", "Error fetching document", e);
//                fetchedCount[0]++;
//            });
//        }
//    }

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
            // Fragment is not attached, skip UI updates
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
        if (isAdded() && getContext() != null) {
        adapter = new MoodEventAdapter(requireContext(), sortedList);
        recent_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        } else {
            Log.w("ProfilePage", "Fragment not attached during display update");
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

//    private void fetchMoodEvents(List<DocumentReference> moodRefs) {
//        ArrayList<MoodEvent> tempList = new ArrayList<>();
//        final int[] fetchedCount = {0};
//
//        for (DocumentReference moodRef : moodRefs) {
//            moodRef.get().addOnSuccessListener(documentSnapshot -> {
//                if (documentSnapshot.exists()) {
//                    try {
//                        MoodEvent moodEvent = documentSnapshot.toObject(MoodEvent.class);
//                        if (moodEvent != null) {
//                            tempList.add(moodEvent);
//                        }
//                    } catch (Exception e) {
//                        Log.e("MoodHistory", "Error converting document", e);
//                    }
//                }
//
//                fetchedCount[0]++;
//                // Inside fetchMoodEvents
//                if (fetchedCount[0] == moodRefs.size()) {
//                    moodHistoryList.clear();
//                    moodHistoryList.addAll(tempList);
//                    sortMoodEvents();
//
//                    if (isAdded() && getContext() != null) { // Check fragment attachment
//                        if (moodHistoryList.size() > 3) {
//                            ArrayList<MoodEvent> RecentHistoryList = new ArrayList<>(moodHistoryList.subList(0, 3));
//                            Display(RecentHistoryList);
//                        } else {
//                            Display(moodHistoryList);
//                        }
//                    }
//                }
//            }).addOnFailureListener(e -> {
//                Log.e("MoodHistory", "Error fetching document", e);
//                fetchedCount[0]++;
//            });
//        }
//    }
//private void fetchMoodEvents(List<DocumentReference> moodRefs) {
//    ArrayList<MoodEvent> tempList = new ArrayList<>();
//    final int[] fetchedCount = {0};
//    final boolean isOnline = isNetworkAvailable();
//
//    for (DocumentReference moodRef : moodRefs) {
//        Source source = isOnline ? Source.SERVER : Source.CACHE;
//
//        moodRef.get(source).addOnCompleteListener(task -> {
//            if (task.isSuccessful() && task.getResult() != null) {
//                DocumentSnapshot document = task.getResult();
//                try {
//                    MoodEvent moodEvent = document.toObject(MoodEvent.class);
//                    if (moodEvent != null) {
//                        tempList.add(moodEvent);
//                    }
//                } catch (Exception e) {
//                    Log.e("ProfilePage", "Error converting document", e);
//                }
//            }
//
//            fetchedCount[0]++;
//            if (fetchedCount[0] == moodRefs.size()) {
//                moodHistoryList.clear();
//                moodHistoryList.addAll(tempList);
//                sortMoodEvents();
//
//                if (isAdded() && getContext() != null) {
//                    if (!isOnline) {
//                        Toast.makeText(getContext(),
//                                "Offline - showing cached data",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    if (moodHistoryList.size() > 3) {
//                        Display(new ArrayList<>(moodHistoryList.subList(0, 3)));
//                    } else {
//                        Display(moodHistoryList);
//                    }
//                }
//            }
//        });
//    }
//}
    private void fetchMoodEvents(List<DocumentReference> moodRefs) {
        ArrayList<MoodEvent> tempList = new ArrayList<>();
        final int[] fetchedCount = {0};
        final boolean isOnline = isNetworkAvailable();

        for (DocumentReference moodRef : moodRefs) {
            Source source = isOnline ? Source.DEFAULT : Source.CACHE;

            moodRef.get(source).addOnCompleteListener(task -> {
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
                    moodHistoryList.addAll(tempList);
                    sortMoodEvents();

                    if (isAdded() && getContext() != null) {
                        if (!isOnline) {
                            Toast.makeText(getContext(),
                                    "Offline - showing cached data",
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (moodHistoryList.size() > 3) {
                            Display(new ArrayList<>(moodHistoryList.subList(0, 3)));
                        } else {
                            Display(moodHistoryList);
                        }
                    }
                }
            });
        }
    }

//    private boolean isNetworkAvailable() {
//        ConnectivityManager cm = (ConnectivityManager) getActivity()
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        return activeNetwork != null && activeNetwork.isConnected();
//    }
//    private boolean isNetworkAvailable() {
//        Context context = getContext();
//        return context != null && NetworkUtils.isNetworkAvailable(context);
//    }
    private boolean isNetworkAvailable() {
        Context context = getContext();
        if (context == null) return false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

//    private void Display(ArrayList<MoodEvent> moodHistoryList) {
//        // Update the adapter with sorted list
//        if (adapter == null) {
//            adapter = new MoodEventAdapter(requireContext(), moodHistoryList);
//            recent_list.setAdapter(adapter);
//        } else {
//            // Just update the existing adapter if it already exists
//            adapter.clear();
//            adapter.addAll(moodHistoryList);
//            adapter.notifyDataSetChanged();
//        }
//    }


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
//        Intent intent = new Intent(getActivity(), LogIn.class);
//        startActivity(intent);
//        requireActivity().finish();
        if (isAdded()) {  // Ensure fragment is attached
            Intent intent = new Intent(requireActivity(), LogIn.class);
            startActivity(intent);
            requireActivity().finish();
        } else {
            Log.w("ProfilePage", "Redirect attempted when fragment detached");
        }
    }
    private void navigateToFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);

        if (existingFragment == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .addToBackStack(null)
                    .commit();
        } else {
            fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}