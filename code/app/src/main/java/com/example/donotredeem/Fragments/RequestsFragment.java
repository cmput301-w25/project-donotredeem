package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.Classes.User;
import com.example.donotredeem.R;
import com.example.donotredeem.RequestAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;

import java.util.List;

/**
 * Fragment displaying incoming follow requests with real-time updates.
 *
 * <p>Features:
 * <ul>
 * <li>Shows list of users who requested to follow the current user</li>
 * <li>Maintains real-time sync with Firestore database</li>
 * <li>Handles offline data persistence</li>
 * <li>Automatically updates UI when requests change</li>
 * </ul>
 */
public class RequestsFragment extends Fragment {
    private ListView requestsListView;
    private List<String> requestsList;
    private RequestAdapter adapter;
    private UserProfileManager userProfileManager;
    private FirebaseFirestore db;
    private ListenerRegistration userDataListener;

    /**
     * Creates and configures the fragment's view hierarchy
     *
     * @return Root view for the requests interface
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.requests, container, false);
        requestsListView = view.findViewById(R.id.requestsListView);
        userProfileManager = new UserProfileManager();

        // Get current logged-in user
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("username", "");

        loadRequests(currentUser);
        return view;
    }

    /**
     * Initializes real-time request listener for the specified user
     *
     * @param username Current logged-in user's username
     */
    private void loadRequests(String username) {
        if (db == null) {
            db = FirebaseFirestore.getInstance(); // Reinitialize if null
        }
        DocumentReference userRef = db.collection("User").document(username);
        // Add metadata listener for offline support
        userDataListener = userRef.addSnapshotListener(MetadataChanges.INCLUDE, (documentSnapshot, error) -> {
            if (!isAdded()) return; // Critical check here
            if (error != null) {
                Log.e("ProfilePage", "Listen error", error);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {

                // Parse with new constructor
                int moods = documentSnapshot.getLong("moods") != null ?
                        documentSnapshot.getLong("moods").intValue() : 0;

                List<DocumentReference> moodRefs = (List<DocumentReference>)
                        documentSnapshot.get("MoodRef");

                User user = new User(
                        documentSnapshot.getId(),
                        documentSnapshot.getString("bio"),
                        documentSnapshot.getString("pfp"),
                        (List<String>) documentSnapshot.get("follower_list"),
                        (List<String>) documentSnapshot.get("following_list"),
                        (List<String>) documentSnapshot.get("requests"),
                        (List<DocumentReference>) documentSnapshot.get("MoodRef"),
                        moods

                );
                requestsList = user.getRequests();
                adapter = new RequestAdapter(requireContext(), requestsList);
                requestsListView.setAdapter(adapter);
            }
        });
    }
}
