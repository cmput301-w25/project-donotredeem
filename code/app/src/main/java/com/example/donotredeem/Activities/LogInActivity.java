package com.example.donotredeem.Activities;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.example.donotredeem.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.Source;

import java.util.List;

/**
 * Handles user authentication through username/password credentials.
 * Manages login workflow including:
 * - Credential validation
 * - Password visibility toggling
 * - Firestore user data verification
 * - Session persistence with SharedPreferences
 * - Prefetching user data for better performance
 */
public class LogInActivity extends AppCompatActivity {
    EditText editTextUsername, editTextPassword;
    Button buttonLogIn, buttonGoogleSignIn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;
    TextView textview;

    /**
     * Checks for existing active session on activity start.
     * Redirects to MainActivity if valid session exists.
     */
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    public static final CountingIdlingResource firebaseIdlingResource =
            new CountingIdlingResource("Firebase_Call");

    /**
     * Called when the activity is first created.
     * Initializes Firebase, UI components, and configures Google Sign-In.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextUsername = findViewById(R.id.etUsername);
        editTextPassword = findViewById(R.id.etPassword);
        buttonLogIn = findViewById(R.id.btnLogin);

        TextView show_pass = findViewById(R.id.show_pass);
        ImageView pass_icon = findViewById(R.id.pass_icon);

        textview = findViewById(R.id.button4);
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        pass_icon.setImageResource(R.drawable.password_invisible);

        pass_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the password is currently visible or not
                if (editTextPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    // If it's currently hidden, make it visible
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pass_icon.setImageResource(R.drawable.password_visible);
                    show_pass.setText("Hide password");
                    // Show the eye icon (password visible)
                } else {
                    // If it's currently visible, make it hidden
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pass_icon.setImageResource(R.drawable.password_invisible);
                    show_pass.setText("Show password");// Show the eye icon (password hidden)
                }
            }
        });

        show_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the password is currently visible or not
                if (editTextPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    // If it's currently hidden, make it visible
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pass_icon.setImageResource(R.drawable.password_visible);
                    show_pass.setText("Hide password");
                    // Show the eye icon (password visible)
                } else {
                    // If it's currently visible, make it hidden
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pass_icon.setImageResource(R.drawable.password_invisible);
                    show_pass.setText("Show password");// Show the eye icon (password hidden)
                }
            }
        });

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(username)){
                    Snackbar.make(findViewById(android.R.id.content), "Please enter username", Snackbar.LENGTH_LONG).show();
                    return;}
                if (TextUtils.isEmpty(password)) {
                    Snackbar.make(findViewById(android.R.id.content), "Please enter password", Snackbar.LENGTH_LONG).show();
                    return;}

                // Firestore query to check if the username exists and password matches
                Log.d(TAG, "Fetching user: " + username);
                DocumentReference userDocRef = db.collection("User").document(username);
                userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> userTask) {
                        if (userTask.isSuccessful()) {
                            DocumentSnapshot document = userTask.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "User document found: " + document.getData());
                                String storedPassword = document.getString("password");
                                if (storedPassword != null && storedPassword.equals(password)) {
                                    // Password matches, allow login
                                    View parentLayout = findViewById(R.id.please);
                                    Snackbar.make(parentLayout, "Login successfully.", Snackbar.LENGTH_LONG).show();

                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("username", username);
                                        editor.apply();
                                        FirebaseFirestore.getInstance()
                                                .collection("User")
                                                .document(username)
                                                .get(Source.SERVER) // Force network to ensure fresh data
                                                .addOnSuccessListener(userDoc -> {
                                                    List<DocumentReference> moodRefs = (List<DocumentReference>) userDoc.get("MoodRef");
                                                    if (moodRefs != null) {
                                                        for (DocumentReference ref : moodRefs) {
                                                            // Prefetch and cache each mood event
                                                            ref.get(Source.SERVER).addOnCompleteListener(task -> {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "Prefetched mood event: " + ref.getId());
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                        startActivity(new Intent(LogInActivity.this, MoodSelectionActivity.class));
                                        finish();
                                        }, 2000);

                                } else {
                                    Log.e(TAG,"Incorrect password.");
                                    View parentLayout = findViewById(R.id.please);
                                    Snackbar.make(parentLayout, "Incorrect password.", Snackbar.LENGTH_LONG).show();
                                }
                            } else {
                                Log.e(TAG, "User not found: " + username);
                                View parentLayout = findViewById(R.id.please);
                                Snackbar.make(parentLayout, "User not found.", Snackbar.LENGTH_LONG).show();

                            }
                        } else {
                            Log.e(TAG, "Firestore error: ", userTask.getException());
                        }
                    }
                });
            }
        });

    }
}

