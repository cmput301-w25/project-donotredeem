package com.example.donotredeem.Classes;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
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

//    /**
//     * Checks if a user exists in Firestore by their username.
//     * @param username the username used to check for an existing user profile.
//     * @param listener the listener to handle the result of the check.
//     */
//    public void checkUserExist(String username, OnUserCheckListener listener){
//        db.collection("User")
//                .document(username)// Query for a specific username
//                .get()
//                .addOnCompleteListener(task -> {
//                   if (task.isSuccessful()) {
//                       DocumentSnapshot document = task.getResult();
//                       if (document.exists()) {
//                           //create user object from document
//                           Users user = createUserProfileFromDocument(document);
//                           listener.onUserExists(user);
//                       }else {
//                           // User does not exist
//                           listener.onUserNotExists();
//                       }
//                   } else {
//                       // Handle any error from the task
//                       listener.onError(task.getException());
//                   }
//                });
//    }

    /**
     * Creates a <code>Users</code> object from a Firestore document snapshot.
     * @param document Firestore document snapshot containing user data.
     * @return a new Users object populated with the data from the document.
     */
    private Users createUserProfileFromDocument(DocumentSnapshot document) {
        String username = document.getId();
        String password = document.getString("password");
//        String MoodRef = document.getString("MoodREF");
        String email = document.getString("email");
//        String phoneNumber = document.getString("phone Number");
//        String birthdayDate = document.getString("birthday");
        String bio = document.getString("bio");
//        String Notifications = document.getString("Notifications"); //daily
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

//    public void updateUser(Users user, String username){
//        assert db != null;
//        db.collection("User").document(username) // Use username as the document ID
//                .set(user);
//    }

//    /**
//     * Delete a user profile from Firestore using the device ID.
//     * @param username the unique ID for the user to delete
//     */
//    public void deleteUserProfile(String username){
//        assert db != null;
//
//    }


//    /**
//     * Interface for handling the result of user existence check.
//     */
//    public interface OnUserCheckListener {
//        void onUserExists(Users user); // case when user exists
//        void onUserNotExists(); // case when user doesn't exists
//        void onError(Exception e); // Handle errors
//    }
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
}
