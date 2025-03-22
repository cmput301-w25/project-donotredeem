package com.example.donotredeem.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.Classes.Users;
import com.example.donotredeem.R;

import java.util.List;

public class SearchedUser extends Fragment {

    private static final String ARG_USERNAME = "username";
    private String username;

    private TextView usernameTextView, bioTextView, followersTextView, followingTextView;
    private ListView followerListView, followingListView;

    public static SearchedUser newInstance(String username) {
        SearchedUser fragment = new SearchedUser();
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
        View view = inflater.inflate(R.layout.searched_user, container, false);
//
//        // Initialize TextViews
//        usernameTextView = view.findViewById(R.id.username_text);
//        bioTextView = view.findViewById(R.id.bio_text);
//        followersTextView = view.findViewById(R.id.followers_text);
//        followingTextView = view.findViewById(R.id.following_text);
//
//        // Initialize ListViews
//        followerListView = view.findViewById(R.id.follower_list);
//        followingListView = view.findViewById(R.id.following_list);
//
//        // Fetch and display user data
//        fetchUserData(username);

        return view;
    }

    private void fetchUserData(String username) {
        UserProfileManager userProfileManager = new UserProfileManager();
        userProfileManager.getUserProfileWithFollowers(username, new UserProfileManager.OnUserProfileFetchListener() {
            @Override
            public void onUserProfileFetched(Users user) {
                if (user != null) {
                    // Set user details to TextViews
                    usernameTextView.setText("Username: " + user.getUsername());
                    bioTextView.setText("Bio: " + user.getBio());
                    followersTextView.setText("Followers: " + user.getFollowers());
                    followingTextView.setText("Following: " + user.getFollowing());

                    // Display follower and following lists
                    updateListView(followerListView, user.getFollowerList(), "No followers");
                    updateListView(followingListView, user.getFollowingList(), "Not following anyone");
                } else {
                    Log.e("SearchedUser", "User not found.");
                }
            }

            @Override
            public void onUserProfileFetchError(Exception e) {
                Log.e("SearchedUser", "Error fetching user data", e);
            }
        });
    }

    private void updateListView(ListView listView, List<String> dataList, String emptyMessage) {
        if (dataList != null && !dataList.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1, dataList);
            listView.setAdapter(adapter);
        } else {
            // Show empty message if list is empty
            listView.setAdapter(new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1, new String[]{emptyMessage}));
        }
    }
}
