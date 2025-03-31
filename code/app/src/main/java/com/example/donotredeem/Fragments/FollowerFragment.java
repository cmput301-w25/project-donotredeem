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
import com.example.donotredeem.Classes.User;
import com.example.donotredeem.Adapters.FollowerAdapter;
import com.example.donotredeem.R;

import java.util.List;

/**
 * Fragment displaying a user's follower list with dismissal functionality.
 * Handles asynchronous loading of social connections through a dedicated profile manager.
 *
 * <p>Key features:
 * <ul>
 * <li>Displays scrollable list of follower usernames</li>
 * <li>Implements safe fragment dismissal mechanics</li>
 * <li>Handles data loading states and errors</li>
 * <li>Maintains separation between UI and data layer</li>
 * </ul>
 */
public class FollowerFragment extends Fragment {
    private RecyclerView followersListView;
    private List<String> follower_list;
    private FollowerAdapter adapter;
    private UserProfileManager userProfileManager;
    private String username;
    private ImageView closeButton;  // Declare the close button

    /**
     * Factory method for fragment instantiation
     * @param username User identifier to load followers for
     * @return Pre-configured fragment instance
     */
    public static FollowerFragment newInstance(String username) {
        FollowerFragment fragment = new FollowerFragment();
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
     * Configures fragment UI components and data flow:
     * - Inflates follower list layout
     * - Initializes RecyclerView and adapter
     * - Sets up click listeners for dismissal
     * - Triggers follower data loading
     *
     * @param inflater Layout inflation service
     * @param container Parent view container
     * @param savedInstanceState Persisted state bundle
     * @return Configured view hierarchy
     */
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

        loadRequests(username);
        return view;
    }

    /**
     * Loads follower relationships through UserProfileManager
     * @param username Target user identifier
     */
    private void loadRequests(String username) {
        userProfileManager.getUserProfileWithFollowers(username, new UserProfileManager.OnUserProfileFetchListener() {

            /**
             * Handles successful follower data loading
             * @param user Complete user profile with followers
             * @return Always returns false indicating no custom handling
             */
            @Override
            public boolean onUserProfileFetched(User user) {
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

            /**
             * Manages profile load failures
             * @param e Exception containing error details
             */
            @Override
            public void onUserProfileFetchError(Exception e) {
                Toast.makeText(requireContext(), "Error loading followers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Cleans up fragment resources and removes from navigation stack
     */
    private void dismissFragment() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }
}
