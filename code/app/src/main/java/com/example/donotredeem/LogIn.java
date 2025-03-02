package com.example.donotredeem;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogIn extends AppCompatActivity {
    EditText editTextUsername, editTextPassword;
    Button buttonLogIn, buttonGoogleSignIn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextUsername = findViewById(R.id.etUsername);
        editTextPassword = findViewById(R.id.etPassword);
        buttonLogIn = findViewById(R.id.btnLogin);
        buttonGoogleSignIn = findViewById(R.id.btnGoogle);

        // Google Sign-In Configuration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Get OAuth 2.0 ID Token
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //  Handle Login Button Click
//        buttonLogIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String username = editTextUsername.getText().toString();
//                String password = editTextPassword.getText().toString();
//
//                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
//                    Toast.makeText(LogIn.this, "Enter username and password", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // 🔹 Check Firestore for username & password authentication
//                db.collection("users").document(username).get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            String storedPassword = document.getString("password");
//                            if (storedPassword != null && storedPassword.equals(password)) {
//                                Toast.makeText(LogIn.this, "Authentication Successful", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                                finish();
//                            } else {
//                                Toast.makeText(LogIn.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(LogIn.this, "User not found", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Log.e(TAG, "Error getting document", task.getException());
//                        Toast.makeText(LogIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username, password;
                username = String.valueOf(editTextUsername.getText().toString());
                password = String.valueOf(editTextPassword.getText().toString());

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(LogIn.this, "Enter username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LogIn.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firestore query to check if the username exists and password matches
                DocumentReference userDocRef = db.collection("users").document(username);  // Using username as document name
                userDocRef.get().addOnCompleteListener(new OnCompleteListener<org.apache.firestore.DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<org.apache.firestore.DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            org.apache.firestore.DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get the password from Firestore document
                                String storedPassword = document.getString("password");

                                // Check if the passwords match
                                if (storedPassword != null && storedPassword.equals(password)) {
                                    // Password matches, allow login
                                    Toast.makeText(LogIn.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Password does not match
                                    Toast.makeText(LogIn.this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // User document doesn't exist
                                Toast.makeText(LogIn.this, "User not found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Error fetching document
                            Toast.makeText(LogIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Handle Google Sign-In Button Click
        buttonGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 9001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (Exception e) {
                Log.w(TAG, "Google Sign-In failed", e);
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInWithCredential:success");
                FirebaseUser user = mAuth.getCurrentUser();
                Toast.makeText(LogIn.this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(LogIn.this, "Google Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
