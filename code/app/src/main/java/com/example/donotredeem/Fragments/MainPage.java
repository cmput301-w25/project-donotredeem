package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.LogIn;
import com.example.donotredeem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The {@code MainPage} class represents the main screen of the app after login.
 * It displays the logged-in user's name and provides a sign-out option.
 */
public class MainPage extends Fragment {

    private FirebaseFirestore db;
    private String loggedInUsername;

    /**
     * Called to instantiate the fragment's view.
     *
     * @param inflater  The LayoutInflater used to inflate the layout.
     * @param container The parent view that this fragment's UI should be attached to.
     * @param savedInstanceState If non-null, the fragment is being re-constructed from a previous state.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_page, container, false);

        db = FirebaseFirestore.getInstance();
        Button button = view.findViewById(R.id.temp_sign_out);
        TextView textView = view.findViewById(R.id.user);

        // Retrieve the username from SharedPreferences (saved during login)
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("username")) {
            Log.e("MainPageDebug", "Username not found in SharedPreferences, redirecting to login");
            redirectToLogin();
            return view; // Early return to stop further processing
        } else {
            loggedInUsername = sharedPreferences.getString("username", null);
            Log.d("MainPageDebug", "Retrieved username: " + loggedInUsername);
        }

        if (loggedInUsername == null) {
            // No user logged in, redirect to login screen
            redirectToLogin();
            return view; // Early return
        } else {
            // Fetch user details from Firestore
            Log.d("MainPageDebug", "Fetching user details for: " + loggedInUsername);
            fetchUserDetails(textView);
        }

        button.setOnClickListener(view1 -> logout());

        return view;
    }

    /**
     * Fetches the user details from Firestore and updates the UI with the logged-in username.
     *
     * @param textView The TextView where the username should be displayed.
     */
    private void fetchUserDetails(TextView textView) {
        if (loggedInUsername == null) {
            Log.e("MainPageDebug", "loggedInUsername is null, redirecting to login");
            redirectToLogin();
            return;
        }

        DocumentReference userDocRef = db.collection("User").document(loggedInUsername);
        userDocRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        textView.setText(loggedInUsername);
                    } else {
                        Log.e("MainPageDebug", "User not found in Firestore");
                        redirectToLogin();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainPageDebug", "Firestore error: " + e.getMessage());
                    redirectToLogin();
                });
    }

    /**
     * Logs out the user by signing out from Firebase and clearing login data.
     * Redirects the user to the login screen.
     */
    private void logout() {
        FirebaseAuth.getInstance().signOut();

        // Clear stored login data
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to login screen
        redirectToLogin();
    }

    /**
     * Redirects the user to the login screen and clears the activity stack.
     */
    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LogIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
