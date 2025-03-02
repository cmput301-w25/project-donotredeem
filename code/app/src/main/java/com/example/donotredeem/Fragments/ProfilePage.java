package com.example.donotredeem.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.donotredeem.Fragments.MoodHistory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.donotredeem.R;

public class ProfilePage extends Fragment {





    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        DrawerLayout drawerLayout = view.findViewById(R.id.drawer_layout);
        LinearLayout sidePanel = view.findViewById(R.id.side_panel);

        view.findViewById(R.id.side_panel_button).setOnClickListener(v -> {
            // Open the drawer (side panel)
            drawerLayout.openDrawer(sidePanel);
        });

        sidePanel.findViewById(R.id.panel_close).setOnClickListener(v -> {
            drawerLayout.closeDrawer(sidePanel);
        });

        sidePanel.findViewById(R.id.nav_history).setOnClickListener(v -> {
            drawerLayout.closeDrawer(sidePanel);

            MoodHistory historyFragment = new MoodHistory();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.profile_fragment_container, new MoodHistory())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
