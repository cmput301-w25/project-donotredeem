package com.example.donotredeem.Fragments;

import static android.text.style.TtsSpan.ARG_USERNAME;

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

public class FollowingFragment extends Fragment {
    private RecyclerView followingListView;
    private List<String> following_list;
    private FollowerAdapter adapter;
    private UserProfileManager userProfileManager;
    private String username;

    public static FollowingFragment newInstance(String username) {
        FollowingFragment fragment = new FollowingFragment();
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
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        followingListView = view.findViewById(R.id.commentsRecyclerView);
        userProfileManager = new UserProfileManager();

//        // Get current logged-in user
//        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
//        String currentUser = sharedPreferences.getString("username", "");

        loadRequests(username);
        return view;
    }

    private void loadRequests(String username) {
        userProfileManager.getUserProfileWithFollowers(username, new UserProfileManager.OnUserProfileFetchListener() {
            @Override
            public boolean onUserProfileFetched(Users user) {
                following_list = user.getFollowingList();

                if (following_list != null && !following_list.isEmpty()) {
                    adapter = new FollowerAdapter(requireContext(), following_list);

                    // Set LayoutManager before setting the adapter
                    followingListView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    followingListView.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "No followers found.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public void onUserProfileFetchError(Exception e) {
                Toast.makeText(requireContext(), "Error loading followers", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
