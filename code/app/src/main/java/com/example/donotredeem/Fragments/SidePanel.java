package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.donotredeem.LogIn;
import com.example.donotredeem.MoodJarFragment;
import com.example.donotredeem.QRCodeFragment;
import com.example.donotredeem.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Fragment representing the application's navigation side panel.
 *
 * Handles:
 * - Navigation between major app sections
 * - User authentication state management
 * - Fragment transaction management
 * - Session termination functionality
 *
 * Contains menu options for:
 * - Mood History
 * - User Profile
 * - Analytics
 * - About Us
 * - QR Code
 * - Mood Jar
 * - Sign Out
 */
public class SidePanel extends Fragment {
    TextView history, profile, analytics, about_us, qr_code,mood_jar, sign_out;
    ImageButton close;

    /**
     * Creates and configures the side panel navigation view
     *
     * @param inflater LayoutInflater to inflate XML layout
     * @param container Parent view group for fragment's UI
     * @param savedInstanceState Saved state bundle
     * @return Configured view for the side panel
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.side_panel, container, false);

        history = view.findViewById(R.id.nav_history);
        profile = view.findViewById(R.id.nav_profile);
        analytics = view.findViewById(R.id.nav_analytics);
        about_us = view.findViewById(R.id.nav_about_us);
        qr_code = view.findViewById(R.id.nav_qr_code);
        mood_jar = view.findViewById(R.id.nav_mood_jar);
        sign_out = view.findViewById(R.id.Sign_out);

        history.setOnClickListener(v->{
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
        });

        profile.setOnClickListener(v->{
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment existingFragment = fragmentManager.findFragmentByTag("EditProfile");

            if (existingFragment == null) {
                EditProfile profileFragment = new EditProfile();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, profileFragment, "EditProfile")
                        .addToBackStack(null)
                        .commit();
            } else {
                fragmentManager.popBackStack("EditProfile", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        analytics.setOnClickListener(v->{
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment existingFragment = fragmentManager.findFragmentByTag("Analytics");

            if (existingFragment == null) {
                AnalyticsFragment analyticsFragment = new AnalyticsFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, analyticsFragment, "Analytics")
                        .addToBackStack(null)
                        .commit();
            } else {
                fragmentManager.popBackStack("EditProfile", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });


        about_us.setOnClickListener(v->{
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment existingFragment = fragmentManager.findFragmentByTag("About Us");

            if (existingFragment == null) {
                AboutUs aboutusFragment = new AboutUs();

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, aboutusFragment, "About Us")
                        .addToBackStack(null)
                        .commit();
            } else {
                fragmentManager.popBackStack("EditProfile", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

        });

        qr_code.setOnClickListener(v->{

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment existingFragment = fragmentManager.findFragmentByTag("QRCode");

            if (existingFragment == null) {
                QRCodeFragment profileFragment = new QRCodeFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, profileFragment, "QRCode")
                        .addToBackStack(null)
                        .commit();
            } else {
                fragmentManager.popBackStack("QRCode", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

    });


        mood_jar.setOnClickListener(v->{
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment existingFragment = fragmentManager.findFragmentByTag("MoodJar");

            if (existingFragment == null) {
                MoodJarFragment profileFragment = new MoodJarFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, profileFragment, "MoodJar")
                        .addToBackStack(null)
                        .commit();
            } else {
                fragmentManager.popBackStack("MoodJar", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

        });

        sign_out.setOnClickListener(v -> logout());

        close = view.findViewById(R.id.imageButton);

        close.setOnClickListener(v -> {
            // Simply pop the back stack - animation will be handled automatically
            getParentFragmentManager().popBackStack();
        });


        return view;
    };

    /**
     * Terminates user session and clears authentication data
     */
    private void logout() {
        FirebaseAuth.getInstance().signOut();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        redirectToLogin();
    }

    /**
     * Redirects to login screen with clean activity stack
     *
     * Uses Intent flags to prevent back navigation:
     * - NEW_TASK: Creates new activity stack
     * - CLEAR_TASK: Clears any existing activities
     *
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
