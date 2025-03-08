package com.example.donotredeem.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.R;

/**
 * Fragment representing the Activities screen.
 * Contains a button to navigate to the Recurrence activity.
 */
public class Activities extends Fragment {

    private Button nextButton;

    /**
     * Inflates the fragment layout and initializes UI components.
     *
     * @param inflater  The LayoutInflater used to inflate the layout.
     * @param container The parent ViewGroup (if applicable).
     * @param savedInstanceState The saved state of the fragment (if any).
     * @return The root View of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activities, container, false);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), Recurrence.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
