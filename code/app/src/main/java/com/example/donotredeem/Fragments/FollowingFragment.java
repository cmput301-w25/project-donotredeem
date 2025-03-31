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

import com.example.donotredeem.Classes.User;
import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.FollowerAdapter;
import com.example.donotredeem.R;

import java.util.List;

/**
 * Fragment displaying a user's following list with interactive dismissal capabilities.
 * Handles both UI presentation and data loading through a dedicated UserProfileManager.
 */
public class FollowingFragment extends Fragment {
    private RecyclerView followingListView;
    private List<String> following_list;
    private FollowerAdapter adapter;
    private UserProfileManager userProfileManager;
    private String username;
    private ImageView closeButton;  // Declare the close button

    /**
     * Factory method for creating fragment instances with username argument
     * @param username User identifier to load following list for
     * @return Configured fragment instance
     */
    public static FollowingFragment newInstance(String username) {
        FollowingFragment fragment = new FollowingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes fragment with username argument
     * @param savedInstanceState Persisted state bundle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
        }
    }

    /**
     * Configures fragment UI components and data loading:
     * - Inflates following list layout
     * - Initializes RecyclerView and adapter
     * - Sets up click listeners for dismissal
     * - Triggers following list loading
     *
     * @param inflater Layout inflater service
     * @param container Parent view container
     * @param savedInstanceState Persisted state bundle
     * @return Configured view hierarchy
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        followingListView = view.findViewById(R.id.commentsRecyclerView);
        userProfileManager = new UserProfileManager();
        closeButton = view.findViewById(R.id.imageView7); // Initialize the close button - imageView7
        view.setOnClickListener(v -> dismissFragment());
        // Set click listener for the close button
        closeButton.setOnClickListener(v -> dismissFragment());
        // Prevent inner layout clicks from closing
        View innerLayout = view.findViewById(R.id.inner_layout); // Add ID to your LinearLayout
        innerLayout.setOnClickListener(v -> {
            // Consume the click event
        });


        loadRequests(username);
        return view;
    }

    /**
     * Loads following relationships through UserProfileManager
     * @param username Target user identifier
     */
    private void loadRequests(String username) {
        userProfileManager.getUserProfileWithFollowers(username, new UserProfileManager.OnUserProfileFetchListener() {
            @Override
            public boolean onUserProfileFetched(User user) {
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

            /**
             * Handles user profile fetch failures by displaying user-friendly notifications.
             *
             * @param e The exception containing technical details of the failure.
             *          While not currently logged, available for debugging enhancements.
             */
            @Override
            public void onUserProfileFetchError(Exception e) {
                Toast.makeText(requireContext(), "Error loading followers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Cleans up fragment resources and removes from back stack
     */
    private void dismissFragment() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }
}
