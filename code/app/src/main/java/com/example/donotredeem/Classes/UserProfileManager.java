package com.example.donotredeem.Classes;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
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
    public FirebaseFirestore db;
    public FirebaseStorage storage;

    /**
     * Empty constructor for UserProfileManager
     */
    public UserProfileManager(){
        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }
    // Add to UserProfileManager class

    public UserProfileManager(FirebaseFirestore db) {
        this.db = db;
    }


    // Add to UserProfileManager class
    public void acceptFollowRequest(String currentUser, String requesterUsername, OnUpdateListener listener) {
        // 1. Add requester to current user's followers
        db.collection("User").document(currentUser)
                .update(
                        "followers", FieldValue.increment(1),
                        "follower_list", FieldValue.arrayUnion(requesterUsername),
                        "requests", FieldValue.arrayRemove(requesterUsername)
                )
                .addOnSuccessListener(aVoid -> {
                    // 2. Add current user to requester's following
                    db.collection("User").document(requesterUsername)
                            .update(
                                    "following", FieldValue.increment(1),
                                    "following_list", FieldValue.arrayUnion(currentUser)
                            )
                            .addOnSuccessListener(aVoid1 -> listener.onSuccess())
                            .addOnFailureListener(e -> listener.onError(e));
                })
                .addOnFailureListener(e -> listener.onError(e));
    }

    public void declineFollowRequest(String currentUser, String requesterUsername, OnUpdateListener listener) {
        db.collection("User").document(currentUser)
                .update("requests", FieldValue.arrayRemove(requesterUsername))
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e));
    }

    /**
     * Adds the logged-in user to the requests list of the target user's document.
     *
     * @param targetUsername   The username of the target user (being followed).
     * @param loggedInUsername The username of the logged-in user (who is following).
     * @param callback         Callback to handle success or error.
     */
    public void addToRequestsList(String targetUsername, String loggedInUsername, OnUpdateListener callback) {
        db.collection("User")
                .document(targetUsername)
                .update("requests", FieldValue.arrayUnion(loggedInUsername))
                .addOnSuccessListener(aVoid -> {
                    Log.d("UserProfileManager", "Request added successfully.");
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserProfileManager", "Error adding request.", e);
                    if (callback != null) {
                        callback.onError(e);
                    }
                });
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
        String pfp = document.getString("pfp");
        String dob = document.getString("birthDate");
        String contact = document.getString("phone");
        return new Users(
                username,
                password,
                email,
                dob,
                contact,
                bio,
                pfp
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
     * Adds a user to the following list of another user.
     */
    public void addToFollowingList(String username, String targetUsername, OnUpdateListener listener) {
        DocumentReference userRef = db.collection("User").document(username);
        userRef.update("following_list", FieldValue.arrayUnion(targetUsername),
                        "following", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onError);
    }

    /**
     * Adds a user to the followers list of another user.
     */
    public void addToFollowersList(String username, String targetUsername, OnUpdateListener listener) {
        DocumentReference userRef = db.collection("User").document(username);
        userRef.update("follower_list", FieldValue.arrayUnion(targetUsername),
                        "followers", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onError);
    }

    /**
     * Removes a user from the following list of another user.
     */
    public void removeFromFollowingList(String username, String targetUsername, OnUpdateListener listener) {
        DocumentReference userRef = db.collection("User").document(username);
        userRef.update("following_list", FieldValue.arrayRemove(targetUsername),
                        "following", FieldValue.increment(-1))
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onError);
    }

    /**
     * Removes a user from the followers list of another user.
     */
    public void removeFromFollowersList(String username, String targetUsername, OnUpdateListener listener) {
        DocumentReference userRef = db.collection("User").document(username);
        userRef.update("follower_list", FieldValue.arrayRemove(targetUsername),
                        "followers", FieldValue.increment(-1))
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onError);
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
        updates.put("birthDate", updatedUser.getBirthdayDate());
        updates.put("phone", updatedUser.getContact());
        updates.put("bio", updatedUser.getBio());
        updates.put("pfp", updatedUser.getProfilePictureUrl());

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
        boolean onUserProfileFetched(Users user);
        void onUserProfileFetchError(Exception e);
    }


    public interface OnUpdateListener{
        void onSuccess();
        void onError(Exception e);
    }




    public void getUserProfileWithFollowers(String username, OnUserProfileFetchListener callback) {
        db.collection("User").document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
//                            // Get followers (handle both Long and String)
//                            Object followersObj = document.get("followers");
//                            int followerCount = parseFieldToInt(followersObj);

//                            // Get following (handle both Long and String)
//                            Object followingObj = document.get("following");
//                            int followingCount = parseFieldToInt(followingObj);

                            // Extract other fields
                            String bio = document.getString("bio");
                            String pfp = document.getString("pfp");
                            List<String> followersList = (List<String>) document.get("follower_list");
                            List<String> followingList = (List<String>) document.get("following_list");
                            List<String> requestsList = (List<String>) document.get("requests");
                            List<DocumentReference> moodRefs = (List<DocumentReference>) document.get("MoodRef");
                            int moods = document.getLong("moods") != null ?
                                    document.getLong("moods").intValue() : 0;

                            // Create Users object
                            Users user = new Users(
                                    username,
                                    bio,
                                    pfp,
                                    followersList,
                                    followingList,
                                    requestsList,
                                    moodRefs,
                                    moods

                            );

                            callback.onUserProfileFetched(user);
                        } else {
                            callback.onUserProfileFetched(null);
                        }
                    } else {
                        callback.onUserProfileFetchError(task.getException());
                    }
                });
    }

    // Helper method to parse Long or String to int
    private int parseFieldToInt(Object value) {
        if (value instanceof Long) {
            return ((Long) value).intValue(); // Handle Firestore numbers
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value); // Parse strings
            } catch (NumberFormatException e) {
                Log.e("ParseError", "Invalid number format: " + value);
                return 0;
            }
        }
        return 0; // Default for unknown types
    }

    public void deleteUser(String username, OnDeleteListener listener) {
        // 1. Get user document to access moodRefs
        db.collection("User").document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        listener.onError(new Exception("User not found"));
                        return;
                    }

                    // 2. Get all mood references
                    List<DocumentReference> moodRefs = (List<DocumentReference>) documentSnapshot.get("MoodRef");
                    if (moodRefs == null || moodRefs.isEmpty()) {
                        // No moods to delete - proceed with user deletion
                        deleteUserDocument(username, listener);
                        return;
                    }

                    // 3. Delete all mood documents first
                    List<Task<Void>> deleteTasks = new ArrayList<>();
                    for (DocumentReference moodRef : moodRefs) {
                        deleteTasks.add(moodRef.delete());
                    }

                    // 4. Wait for all mood deletions to complete
                    Tasks.whenAll(deleteTasks)
                            .addOnSuccessListener(aVoid -> {
                                // 5. Delete user document after moods are deleted
                                deleteUserDocument(username, listener);
                            })
                            .addOnFailureListener(e -> {
                                listener.onError(new Exception("Failed to delete moods", e));
                            });
                })
                .addOnFailureListener(e -> listener.onError(e));
    }

    private void deleteUserDocument(String username, OnDeleteListener listener) {
        db.collection("User").document(username)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e));
    }

    // Add this interface to your existing listener interfaces
    public interface OnDeleteListener {
        void onSuccess();
        void onError(Exception e);
    }
}
