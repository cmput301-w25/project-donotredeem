package com.example.donotredeem.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.donotredeem.Fragments.AddMoodEventFragment;
import com.example.donotredeem.Fragments.MainPageFragment;
import com.example.donotredeem.Fragments.MapFragment;
import com.example.donotredeem.Fragments.RequestsFragment;
import com.example.donotredeem.Fragments.ProfilePageFragment;
import com.example.donotredeem.R;

import androidx.fragment.app.Fragment;

/**
 * Main activity handling core navigation and fragment management for the application.
        * Manages transitions between key app sections with animated fragment transactions
 * and backstack management. Supported features include:
        *
        * - Mood event creation with slide animations
 * - Map visualization of mood events
 * - Main feed navigation
 * - Friend requests management
 * - User profile editing
 * - Fragment cleanup and state management
 */
public class MainActivity extends AppCompatActivity {

    public static String past_location;
    private ImageButton addEvent, mapButton, homeButton, heartButton, profilePage;

    /**
     * Initializes activity layout and fragment container. Handles:
     * - Initial fragment setup (MainPage)
     * - Navigation button configuration
     * - Intent-based mood selection handling
     * - SharedPreferences cleanup for filters
     *
     * @param savedInstanceState Persisted state bundle or null
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Log to confirm Firestore is initialized
        System.out.println("Firestore initialized with offline support!");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MainPageFragment())
                .commit();

        // Handle mood selection from MoodSelectionActivity
        if (getIntent() != null && getIntent().hasExtra("SELECTED_MOOD")) {
            String selectedMood = getIntent().getStringExtra("SELECTED_MOOD");
            showAddMoodFragmentWithMood(selectedMood);
        } else {
            // Default to MainPage
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MainPageFragment())
                    .commit();
        }

        addEvent = findViewById(R.id.add_button);
        mapButton = findViewById(R.id.map_button);
        homeButton = findViewById(R.id.grid_button);
        heartButton = findViewById(R.id.heart_button);
        profilePage = findViewById(R.id.profilepage);


        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSavedFilters();
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment existingFragment = fragmentManager.findFragmentByTag("AddMoodEvent");

                if (existingFragment == null) { //this is checking if add mood event already exists or not
                    AddMoodEventFragment addMoodEventFragment = new AddMoodEventFragment(); //else we add
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                            .add(R.id.fragment_container, addMoodEventFragment, "AddMoodEvent")
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
                        .replace(R.id.fragment_container, new MapFragment())
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
                        .replace(R.id.fragment_container, new MainPageFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });


        // In MainActivity.java
        heartButton.setOnClickListener(v -> {
            removeAddMoodEventIfExists(); // Your existing cleanup method
            clearSavedFilters();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RequestsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        profilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAddMoodEventIfExists();
                removeHistoryIfExists();
                removeProfileIfExists();
                clearSavedFilters();

                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfilePageFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    /**
     * Cleans up AddMoodEvent fragment from backstack
     */
    private void removeAddMoodEventIfExists() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment addMoodEvent = fragmentManager.findFragmentByTag("AddMoodEvent");

        if (addMoodEvent != null) {
            fragmentManager.popBackStack("AddMoodEvent", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     * Cleans up mood history fragment from backstack
     */
    private void removeHistoryIfExists() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment moodHistory = fragmentManager.findFragmentByTag("moodhistory");

        if (moodHistory != null) {
            fragmentManager.popBackStack("moodhistory", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     * Cleans up profile editing fragment from backstack
     */
    private void removeProfileIfExists() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment editProfile = fragmentManager.findFragmentByTag("EditProfile");

        if (editProfile != null) {
            fragmentManager.popBackStack("EditProfile", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     * Presents AddMoodEvent fragment with preselected mood
     * @param mood Preselected emotional state value
     */
    private void showAddMoodFragmentWithMood(String mood) {
        // Create fragment instance
        AddMoodEventFragment fragment = new AddMoodEventFragment();

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

    /**
     * Resets filter preferences in SharedPreferences
     * Clears: search terms, time filters, emoji selections
     */
    private void clearSavedFilters() {
        SharedPreferences sharedPreferences = getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Set filter values to null (use remove() to delete if required)
        editor.putString("keyword", null);
        editor.putString("timeFilter", null);
        editor.putString("selectedEmojis", null);

        editor.apply(); // Commit changes
    }


}
