package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.Classes.Users;
import com.example.donotredeem.R;
import com.google.firebase.firestore.auth.User;

/**
 * Fragment for editing the user's profile details such as username, email, phone number, and bio.
 * Retrieves user data from SharedPreferences and Firestore, allowing updates and saving changes.
 */
public class EditProfile extends Fragment {

    String username;

    private EditText editUsername;
    private EditText editPassword;
    private EditText editPhoneNumber;
    private EditText editEmail;
    private EditText bio;
    private Button done;
    private ImageButton cancel;
    Users userProfile = new Users();

    /**
     * Inflates the fragment layout, initializes UI components, and retrieves user details.
     *
     * @param inflater  The LayoutInflater used to inflate the layout.
     * @param container The parent ViewGroup (if applicable).
     * @param savedInstanceState The saved state of the fragment (if any).
     * @return The root View of the fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        done =view.findViewById(R.id.button2);
        editUsername = view.findViewById(R.id.username_desc);
        editPassword = view.findViewById(R.id.password_desc);
        editEmail = view.findViewById(R.id.email_desc);
        bio = view.findViewById(R.id.bio_desc);
        cancel= view.findViewById(R.id.closeButton);
        UserProfileManager userProfileManager = new UserProfileManager();
        userProfileManager.getUserProfile(username, new UserProfileManager.OnUserProfileFetchListener() {
            @Override
            public void onUserProfileFetched(Users user) {
                setDetails(user);
                userProfile = user;
            }
            @Override
            public void onUserProfileFetchError(Exception e) {
                Toast.makeText(getContext(), "Failed to fetch profile", Toast.LENGTH_SHORT).show();
            }
        });

        done.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (editUsername.getText().toString().isEmpty() || editEmail.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter name and email", Toast.LENGTH_SHORT).show();
                }
                userProfile.setUsername(editUsername.getText().toString());
                userProfile.setPassword(editPassword.getText().toString());
                userProfile.setEmail(editEmail.getText().toString());
                userProfile.setBio(bio.getText().toString());
                userProfileManager.updateUser(userProfile, username);
            }
        });

        cancel.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }

    /**
     * Populates the UI fields with the user's existing details.
     *
     * @param user The user object containing profile details.
     */
    public void setDetails(Users user){
        editUsername.setText(user.getUsername());
        editPassword.setText(user.getPassword());
        editEmail.setText(user.getEmail());
        bio.setText(user.getBio());
    }
}
