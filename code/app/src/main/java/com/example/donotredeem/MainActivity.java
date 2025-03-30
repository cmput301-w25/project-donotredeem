package com.example.donotredeem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

//import com.example.donotredeem.Fragments.AddMoodEvent;
import com.bumptech.glide.Glide;
import com.example.donotredeem.Fragments.AddMoodEvent;
import com.example.donotredeem.Fragments.MainPage;
import com.example.donotredeem.Fragments.Map;
import com.example.donotredeem.Fragments.RequestsFragment;
import com.example.donotredeem.Fragments.ProfilePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * MainActivity serves as the primary activity for the application and provides
 * navigation between various fragments using button interactions.
 *
 * The activity initializes with the MainPage fragment and supports transitions to:
 *
 *     AddMoodEvent: Opens a fragment for adding a new mood event with slide animations.
 *     Map: Displays a map fragment.
 *     MainPage: Returns to the main page fragment.
 *     Requests: Displays a fragment for viewing requests (e.g., mood-related requests).
 *     ProfilePage: Displays the user's profile page fragment.
 *
 *
 */
public class MainActivity extends AppCompatActivity {

    public static String past_location;
    private ImageButton addEvent, mapButton, homeButton, heartButton;
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    private FirebaseFirestore db;
    private String username;
    private CircleImageView profilePage;
    /**
     * Called when the activity is starting.
     *
     * This method sets up the initial fragment (MainPage) and initializes the UI components.
     * It also sets click listeners for various navigation buttons to transition between fragments.
     *
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,then this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        db = FirebaseFirestore.getInstance();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // Set cache settings (memory or disk-based)
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build()) // Enable memory cache
//                .build();
//
//        // Apply settings to Firestore
//        db.setFirestoreSettings(settings);

        // Log to confirm Firestore is initialized
        System.out.println("Firestore initialized with offline support!");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MainPage())
                .commit();

        // Handle mood selection from MoodSelectionActivity
        if (getIntent() != null && getIntent().hasExtra("SELECTED_MOOD")) {
            String selectedMood = getIntent().getStringExtra("SELECTED_MOOD");
            showAddMoodFragmentWithMood(selectedMood);
        } else {
            // Default to MainPage
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MainPage())
                    .commit();
        }
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, new MainPage())
//                .addToBackStack(null)
//                .commit();

        addEvent = findViewById(R.id.add_button);
        mapButton = findViewById(R.id.map_button);
        homeButton = findViewById(R.id.grid_button);
        heartButton = findViewById(R.id.heart_button);
        this.profilePage = findViewById(R.id.profilepage);
//        CircleImageView profilePage = findViewById(R.id.profilepage);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        loadProfilePicture(username, profilePage);


        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSavedFilters();
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment existingFragment = fragmentManager.findFragmentByTag("AddMoodEvent");

                if (existingFragment == null) { //this is checking if add mood event already exists or not
                    AddMoodEvent addMoodEvent = new AddMoodEvent(); //else we add
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                            .add(R.id.fragment_container, addMoodEvent, "AddMoodEvent")
                            .addToBackStack("AddMoodEvent") //its in backstack, so pressing back = removal
                            .commit();
                } else {
                    View fragmentView = existingFragment.getView(); //add is there and user click plus button again
                    if (fragmentView != null) { //we can see add mood ie is viewable
                        Animation slideOut = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_out_bottom);
                        fragmentView.startAnimation(slideOut); //so removal

                        slideOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                fragmentManager.popBackStack("AddMoodEvent", FragmentManager.POP_BACK_STACK_INCLUSIVE); //real
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    } else {
                        fragmentManager.popBackStack("AddMoodEvent", FragmentManager.POP_BACK_STACK_INCLUSIVE); //ok interesting if i am on other view, this thing will happen in back lmao wth
                    }
                }
            }
        });


        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAddMoodEventIfExists();
                clearSavedFilters();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Map())
                        .addToBackStack(null)
                        .commit();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAddMoodEventIfExists();
                clearSavedFilters();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MainPage())
                        .addToBackStack(null)
                        .commit();
            }
        });

//        heartButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                removeAddMoodEventIfExists();
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, new Requests())
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });

        // In MainActivity.java
        heartButton.setOnClickListener(v -> {
            removeAddMoodEventIfExists(); // Your existing cleanup method
            clearSavedFilters();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RequestsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        this.profilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAddMoodEventIfExists();
                removeHistoryIfExists();
                removeProfileIfExists();
                clearSavedFilters();

                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfilePage())
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    private void removeAddMoodEventIfExists() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment addMoodEvent = fragmentManager.findFragmentByTag("AddMoodEvent");

        if (addMoodEvent != null) {
            fragmentManager.popBackStack("AddMoodEvent", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void removeHistoryIfExists() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment moodHistory = fragmentManager.findFragmentByTag("moodhistory");

        if (moodHistory != null) {
            fragmentManager.popBackStack("moodhistory", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void removeProfileIfExists() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment editProfile = fragmentManager.findFragmentByTag("EditProfile");

        if (editProfile != null) {
            fragmentManager.popBackStack("EditProfile", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
    private void showAddMoodFragmentWithMood(String mood) {
        // Create fragment instance
        AddMoodEvent fragment = new AddMoodEvent();

        // Pass mood via Bundle
        Bundle args = new Bundle();
        args.putString("SELECTED_MOOD", mood);
        fragment.setArguments(args);

        // Replace fragment container
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                .replace(R.id.fragment_container, fragment, "AddMoodEvent")
                .addToBackStack("AddMoodEvent")
                .commit();
    }

    private void clearSavedFilters() {
        SharedPreferences sharedPreferences = getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Set filter values to null (use remove() to delete if required)
        editor.putString("keyword", null);
        editor.putString("timeFilter", null);
        editor.putString("selectedEmojis", null);

        editor.apply(); // Commit changes
    }
    private void loadProfilePicture(String username, CircleImageView imageView) {
        if (username == null || username.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_account_circle);
            return;
        }
        db.collection("User")
                .document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String pfpUrl = documentSnapshot.getString("pfp");
                        if (pfpUrl != null && !pfpUrl.isEmpty()) {
                            Glide.with(MainActivity.this)
                                    .load(pfpUrl)
                                    .placeholder(R.drawable.ic_account_circle)  // Fallback image while loading
                                    .error(R.drawable.ic_account_circle)        // Fallback if loading fails
                                    .into(imageView);
                        } else {
                            imageView.setImageResource(R.drawable.ic_account_circle);  // Fallback if no image
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_account_circle);
                    }
                })
                .addOnFailureListener(e -> imageView.setImageResource(R.drawable.ic_account_circle));
    }

}
