package com.example.donotredeem.Fragments;

import static android.text.style.TtsSpan.ARG_USERNAME;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private String username;
    private ImageView closeButton;  // Declare the close button

    public static FollowerFragment newInstance(String username) {
        FollowerFragment fragment = new FollowerFragment();
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
        View view = inflater.inflate(R.layout.fragment_followers, container, false);
        followersListView = view.findViewById(R.id.commentsRecyclerView);
        userProfileManager = new UserProfileManager();
        closeButton = view.findViewById(R.id.imageView8); // Initialize the close button
        view.setOnClickListener(v -> dismissFragment());
        // Set click listener for the close button
        closeButton.setOnClickListener(v -> dismissFragment());
        // Prevent inner layout clicks from closing
        View innerLayout = view.findViewById(R.id.inner_layout); // Add ID to your LinearLayout
        innerLayout.setOnClickListener(v -> {
            // Consume the click event
        });

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
                follower_list = user.getFollowerList();

                if (follower_list != null && !follower_list.isEmpty()) {
                    adapter = new FollowerAdapter(requireContext(), follower_list);

                    // Set LayoutManager before setting the adapter
                    followersListView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    followersListView.setAdapter(adapter);
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
    private void dismissFragment() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }
}
