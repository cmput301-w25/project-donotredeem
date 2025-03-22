package com.example.donotredeem.Classes;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserProfileManager
 *
 *      The <code>UserProfileManager</code> class provides methods for managing user profiles including adding, updating,
 *      deleting, and fetching user profiles in Firebase Firestore. It also provides methods for uploading, updating, and deleting
 *      user profile pictures in Firebase Storage.
 *
 * @author adityagupta
 * @see User
 */

public class UserProfileManager {
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    /**
     * Empty constructor for UserProfileManager
     */
    public UserProfileManager(){
        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }


    /**
     * Creates a <code>Users</code> object from a Firestore document snapshot.
     * @param document Firestore document snapshot containing user data.
     * @return a new Users object populated with the data from the document.
     */
    private Users createUserProfileFromDocument(DocumentSnapshot document) {
        String username = document.getId();
        String password = document.getString("password");
        String email = document.getString("email");
        String bio = document.getString("bio");
        return new Users(
                username,
                password,
                email,
                bio
        );
    }

    public void getUserProfile(String username, OnUserProfileFetchListener callback) {
        assert db != null;
        db.collection("User").document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Users user = createUserProfileFromDocument(document);
                            callback.onUserProfileFetched(user);
                        } else {
                            callback.onUserProfileFetched(null);
                        }
                    } else {
                        callback.onUserProfileFetchError(task.getException());
                    }
                });
    }



    /**
     * Updates an existing user profile in Firestore.
     * @param updatedUser the Users object to add to Firestore.
     * @param username the unique ID for the user.
     */
    public void updateUser(Users updatedUser, String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Creating a map of only the fields that need to be updated
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", updatedUser.getUsername());
        updates.put("password", updatedUser.getPassword());
        updates.put("email", updatedUser.getEmail());
        updates.put("bio", updatedUser.getBio());

        // Directly reference the document by username (document ID)
        db.collection("User").document(username)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("FirestoreUpdate", "User updated successfully"))
                .addOnFailureListener(e -> Log.e("FirestoreUpdate", "Error updating user", e));
    }

    /**
     * Interface for handling the result of fetching a user profile.
     */
    public interface OnUserProfileFetchListener {
        void onUserProfileFetched(Users user);
        void onUserProfileFetchError(Exception e);
    }


    public interface OnUpdateListener{
        void onSuccess();
        void onError(Exception e);
    }




    public void getUserProfileWithFollowers(String username, OnUserProfileFetchListener callback) {
        assert db != null;
        db.collection("User").document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Extracting required data
                            String bio = document.getString("bio");
                            int followerCount = document.getLong("followers") != null ?
                                    document.getLong("followers").intValue() : 0;
                            int followingCount = document.getLong("following") != null ?
                                    document.getLong("following").intValue() : 0;

                            @SuppressWarnings("unchecked")
                            List<String> followersList = (List<String>) document.get("followers_list");
                            @SuppressWarnings("unchecked")
                            List<String> followingList = (List<String>) document.get("following_list");
                            @SuppressWarnings("unchecked")
                            List<String> requestsList = (List<String>) document.get("requests");

                            // Handle null lists gracefully
                            if (followersList == null) followersList = new java.util.ArrayList<>();
                            if (followingList == null) followingList = new java.util.ArrayList<>();
                            if (requestsList == null) requestsList = new java.util.ArrayList<>();

                            // Create the Users object
                            Users user = new Users(
                                    username,
                                    bio,
                                    followerCount,
                                    followersList,
                                    followingCount,
                                    followingList,
                                    requestsList
                            );

                            // Pass data to callback
                            callback.onUserProfileFetched(user);
                        } else {
                            callback.onUserProfileFetched(null);
                        }
                    } else {
                        callback.onUserProfileFetchError(task.getException());
                    }
                });
    }
}
