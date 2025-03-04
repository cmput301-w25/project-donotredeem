package com.example.donotredeem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // User data variables
    private String name, email, phone, username, password, birthDate, activity, moodRecurrence, reminderMood, test4Data;

    // UI Elements
    private EditText etName, etEmail, etPhoneNo, etUsername, etPassword, etBirthDate, etActivity, etMoodRecurrence, etReminderMood, etTest4Data;
    private Button btnNext;

    private int currentPage = 1; // Keeps track of the page user is on

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        showPage(currentPage);
    }

    private void showPage(int page) {
        switch (page) {
            case 1:
                setContentView(R.layout.sign_up_page1);
                etName = findViewById(R.id.sign_up_name_text);
                etEmail = findViewById(R.id.sign_up_email_text);
                etPhoneNo = findViewById(R.id.sign_up_phone_number_text);
                btnNext = findViewById(R.id.sign_up_done);
                break;
            case 2:
                setContentView(R.layout.sign_up_page2);
                etUsername = findViewById(R.id.sign_up_username_text);
                etPassword = findViewById(R.id.sign_up_password_text);
                btnNext = findViewById(R.id.sign_up_done);
                break;
//            case 3:
//                setContentView(R.layout.birthday);
//                etBirthDate = findViewById(R.id.editTextDate);
//                btnNext = findViewById(R.id.next_button_bday);
//                break;
//            case 4:
//                setContentView(R.layout.activities);
//                etActivity = findViewById(R.id.etActivity);
//                btnNext = findViewById(R.id.next_button_activities);
//                break;
//            case 5:
//                setContentView(R.layout.mood_recurrence);
//                etMoodRecurrence = findViewById(R.id.etMoodRecurrence);
//                btnNext = findViewById(R.id.btnNext);
//                break;
//            case 6:
//                setContentView(R.layout.remindermood);
//                etReminderMood = findViewById(R.id.etReminderMood);
//                btnNext = findViewById(R.id.btnNext);
//                break;
//            case 7:
//                setContentView(R.layout.test4);
//                etTest4Data = findViewById(R.id.etTest4Data);
//                btnNext = findViewById(R.id.btnNext);
//                break;
//            case 8:
//                registerUser();
//                return;
            case 3:
                registerUser();
                return;
        }

        // Set button click listener for navigation
        btnNext.setOnClickListener(v -> {
            storeData(currentPage);
            currentPage++;
            showPage(currentPage);
        });
    }

    private void storeData(int page) {
        switch (page) {
            case 1:
                name = etName.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                phone = etPhoneNo.getText().toString().trim();
                break;
            case 2:
                username = etUsername.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                break;
//            case 3:
//                birthDate = etBirthDate.getText().toString().trim();
//                break;
//            case 4:
//                activity = etActivity.getText().toString().trim();
//                break;
//            case 5:
//                moodRecurrence = etMoodRecurrence.getText().toString().trim();
//                break;
//            case 6:
//                reminderMood = etReminderMood.getText().toString().trim();
//                break;
//            case 7:
//                test4Data = etTest4Data.getText().toString().trim();
//                break;
        }
    }

    private void registerUser() {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Email, Password, and Username are required!", Toast.LENGTH_LONG).show();
            return;
        }

        DocumentReference userDocRef = db.collection("User").document(username);
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Username already exists
                    Toast.makeText(Register.this, "Username already taken!", Toast.LENGTH_LONG).show();
                    currentPage = 2;
                    showPage(currentPage);
                } else {
                    // Username is unique
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Register.this, authTask -> {
                                if (authTask.isSuccessful()) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        saveUserData(firebaseUser);
                                    }
                                } else {
                                    Log.e("Register", "Registration failed: " + authTask.getException().getMessage());
                                    Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(Register.this, "Error checking username uniqueness!", Toast.LENGTH_SHORT).show();
            }
        });
    }


        private void saveUserData(FirebaseUser firebaseUser) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("phone", phone);
        user.put("password", password);
//        user.put("birthDate", birthDate);
//        user.put("activity", activity);
//        user.put("moodRecurrence", moodRecurrence);
//        user.put("reminderMood", reminderMood);
//        user.put("test4Data", test4Data);

        DocumentReference userDoc = db.collection("User").document(username);
        userDoc.set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Register", "User data successfully written to Firestore");

                    //SharedPreferences kinda need to do this
                    SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.apply();

                    Toast.makeText(Register.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("Register", "Error writing document", e);
                    Toast.makeText(Register.this, "Error saving user data!", Toast.LENGTH_SHORT).show();
                });
    }
}
