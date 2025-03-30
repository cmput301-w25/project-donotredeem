package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class SidePanel extends Fragment {
    TextView history, profile, analytics, about_us, qr_code,mood_jar, sign_out;
    ImageButton close;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.side_panel, container, false);
        View fragmentRoot = view.findViewById(R.id.side_container);

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
            if (fragmentRoot != null) {
                Log.d("not null", "this is not null bro what the jhell");
                Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.panel_slide_out);
                fragmentRoot.startAnimation(slideOut);

                slideOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        getParentFragmentManager().popBackStack();
//                        fragmentRoot.postDelayed(() -> {
//                            getParentFragmentManager().popBackStack();
//                        }, 50);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });
            } else {
                getParentFragmentManager().popBackStack();
            }
        });


        return view;
    };
    private void logout() {
        FirebaseAuth.getInstance().signOut();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

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
