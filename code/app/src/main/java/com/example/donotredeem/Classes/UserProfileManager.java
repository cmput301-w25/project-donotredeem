package com.example.donotredeem.Classes;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages user profile operations including Firestore interactions for follow requests,
 * profile updates, and user data retrieval.
 */
public class UserProfileManager {
    // Firestore database instance
    public FirebaseFirestore db;
    // Firebase Storage instance for profile pictures
    public FirebaseStorage storage;

    /**
     * Constructs a UserProfileManager with default Firestore and Storage instances.
     */
    public UserProfileManager(){
        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Constructs a UserProfileManager with a custom Firestore instance (for testing).
     * @param db Custom Firestore instance
     */
    public UserProfileManager(FirebaseFirestore db) {
        this.db = db;
    }

    /**
     * Accepts a follow request and updates both users' relationship data.
     * @param currentUser The username of the user accepting the request
     * @param requesterUsername The username of the user who sent the request
     * @param listener Callback to handle operation success/failure
     */
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

    /**
     * Declines a follow request from another user.
     * @param currentUser The username of the user declining the request
     * @param requesterUsername The username of the requester
     * @param listener Callback to handle operation success/failure
     */
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
     * Creates a Users object from a Firestore document snapshot.
     *
     * @param document Firestore document snapshot containing user data
     * @return Users object populated with Firestore data
     */
    private User createUserProfileFromDocument(DocumentSnapshot document) {
        String username = document.getId();
        String password = document.getString("password");
        String email = document.getString("email");
        String bio = document.getString("bio");
        String pfp = document.getString("pfp");
        String dob = document.getString("birthDate");
        String contact = document.getString("phone");
        return new User(
                username,
                password,
                email,
                dob,
                contact,
                bio,
                pfp
        );
    }

    /**
     * Retrieves a user profile from Firestore.
     *
     * @param username Username of the user to fetch
     * @param callback Callback to handle success or failure
     */
    public void getUserProfile(String username, OnUserProfileFetchListener callback) {
        assert db != null;
        db.collection("User").document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = createUserProfileFromDocument(document);
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
     * Removes a user from the following list of another user.
     *
     * @param username       The username performing the action
     * @param targetUsername The username being unfollowed
     * @param listener       Callback to handle success or failure
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
     *
     * @param username       The username performing the action
     * @param targetUsername The username being unfollowed
     * @param listener       Callback to handle success or failure
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
    public void updateUser(User updatedUser, String username) {
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
        boolean onUserProfileFetched(User user);
        void onUserProfileFetchError(Exception e);
    }

    /**
     * Interface for handling update operations.
     */
    public interface OnUpdateListener{
        void onSuccess();
        void onError(Exception e);
    }

    /**
     * Retrieves a user profile along with their followers, following list, requests, and mood references.
     *
     * @param username The username of the user whose profile is being fetched.
     * @param callback Callback to handle the result (success or failure).
     */
    public void getUserProfileWithFollowers(String username, OnUserProfileFetchListener callback) {
        db.collection("User").document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

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
                            User user = new User(
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


    /**
     * Deletes a user profile along with all associated mood references.
     *
     * @param username The username of the user to be deleted.
     * @param listener Callback to handle success or failure.
     */
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

    /**
     * Deletes a user document from Firestore.
     *
     * @param username The username of the user to delete.
     * @param listener Callback to handle success or failure.
     */
    private void deleteUserDocument(String username, OnDeleteListener listener) {
        db.collection("User").document(username)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e));
    }

    /**
     * Interface for handling delete operations.
     */
    public interface OnDeleteListener {
        void onSuccess();
        void onError(Exception e);
    }
}
