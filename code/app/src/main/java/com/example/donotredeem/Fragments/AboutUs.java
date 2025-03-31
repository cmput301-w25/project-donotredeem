package com.example.donotredeem.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.donotredeem.R;

/**
 * Fragment class representing the "About Us" page.
 * Displays information about the application or organization.
 * Includes a back button to navigate back to the previous fragment.
 */
public class AboutUs extends Fragment {
    /**
     * Inflates the layout for the About Us fragment.
     *
     * @param inflater           The LayoutInflater object to inflate the view.
     * @param container          The parent view group.
     * @param savedInstanceState The saved instance state, if any.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout from XML
        View view = inflater.inflate(R.layout.about_us, container, false);

        // Find the back button by its ID
        ImageView back = view.findViewById(R.id.about_back_button);

        // Set a click listener on the back button
        back.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });
        return view;
    }
}
