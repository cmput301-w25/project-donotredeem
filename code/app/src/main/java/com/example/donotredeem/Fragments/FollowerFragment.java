package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.Classes.Users;
import com.example.donotredeem.FollowerAdapter;
import com.example.donotredeem.R;

import java.util.List;

public class FollowerFragment extends Fragment {
    private RecyclerView followersListView;
    private List<String> follower_list;
    private FollowerAdapter adapter;
    private UserProfileManager userProfileManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followers, container, false);
        followersListView = view.findViewById(R.id.commentsRecyclerView);
        userProfileManager = new UserProfileManager();

        // Get current logged-in user
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("username", "");

        loadRequests(currentUser);
        return view;
    }

    private void loadRequests(String username) {
        userProfileManager.getUserProfileWithFollowers(username, new UserProfileManager.OnUserProfileFetchListener() {
            @Override
            public void onUserProfileFetched(Users user) {
                follower_list = user.getFollowerList();

                if (follower_list != null && !follower_list.isEmpty()) {
                    adapter = new FollowerAdapter(requireContext(), follower_list);

                    // Set LayoutManager before setting the adapter
                    followersListView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    followersListView.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "No followers found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onUserProfileFetchError(Exception e) {
                Toast.makeText(requireContext(), "Error loading followers", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
