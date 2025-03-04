package com.example.donotredeem.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.Classes.Users;
import com.example.donotredeem.R;
import com.google.firebase.firestore.auth.User;

/**
 * edit_user
 * this fragment updates the details of the user such as name , email, phone number etc.
 */

public class EditProfile extends Fragment {

    String username;

    private EditText editUsername;
    private EditText editPassword;
    private EditText editPhoneNumber;
    private EditText editEmail;
    private EditText bio;
    private Button done;
    Users userProfile = new Users();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile, container, false);
        done =view.findViewById(R.id.button2);
        editUsername = view.findViewById(R.id.username_desc);
        editPassword = view.findViewById(R.id.password_desc);
        editEmail = view.findViewById(R.id.email_desc);
        bio = view.findViewById(R.id.bio_desc);
        UserProfileManager userProfileManager = new UserProfileManager();
        userProfileManager.getUserProfile(username, new UserProfileManager.OnUserProfileFetchListener() {
            @Override
            public void onUserProfileFetched(Users user) {
                setDetails(user);
                userProfile = user;
            }
            @Override
            public void onUserProfileFetchError(Exception e) {
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
        return view;
    }

    public void setDetails(Users user){
        editPassword.setText(user.getPassword());
        editEmail.setText(user.getEmail());
        bio.setText(user.getBio());
    }
}
