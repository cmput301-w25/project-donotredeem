package com.example.donotredeem;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
/**
 * LogIn activity for the application.
 *
 * This activity allows users to log in using either a username/password combination or Google Sign-In.
 * It verifies user credentials by querying Firestore and, on a successful login, saves the username in SharedPreferences.
 * After authentication, the user is directed to the MainActivity.
 *
 */
public class LogIn extends AppCompatActivity {
    EditText editTextUsername, editTextPassword;
    Button buttonLogIn, buttonGoogleSignIn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;
    TextView textview;

    /**
     * Called when the activity is starting.
     * Checks if a user is already signed in; if so, proceeds to MainActivity.
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

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextUsername = findViewById(R.id.etUsername);
        editTextPassword = findViewById(R.id.etPassword);
        buttonLogIn = findViewById(R.id.btnLogin);
        buttonGoogleSignIn = findViewById(R.id.btnGoogle);

        textview = findViewById(R.id.button4);
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        // Google Sign-In Configuration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Get OAuth 2.0 ID Token
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    return;
                }


                // Firestore query to check if the username exists and password matches
                Log.d(TAG, "Fetching user: " + username);
                DocumentReference userDocRef = db.collection("User").document(username);
                userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "User document found: " + document.getData());
                                String storedPassword = document.getString("password");
                                if (storedPassword != null && storedPassword.equals(password)) {
                                    // Password matches, allow login
//                                    findViewById(android.R.id.content).post(() ->
//                                            Snackbar.make(findViewById(android.R.id.content), "Login successfully.", Snackbar.LENGTH_LONG).show()
//                                    );


                                    View parentLayout = findViewById(R.id.please);
                                    Snackbar.make(parentLayout, "Login successfully.", Snackbar.LENGTH_LONG).show();

                                    SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("username", username);
                                    editor.apply();

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Log.e(TAG,"Incorrect password.");
                                }
                            } else {
                                Log.e(TAG, "User not found: " + username);
//                                Toast.makeText(LogIn.this, "User not found.", Toast.LENGTH_LONG).show();
                                Snackbar.make(findViewById(android.R.id.content), "User not found.", Snackbar.LENGTH_LONG).show();

                            }
                        } else {
                            Log.e(TAG, "Firestore error: ", task.getException());
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
    /**
     * Initiates the Google Sign-In process by launching the sign-in intent.
     */
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 9001);
    }
    /**
     * Called when an activity you launched exits.
     * Processes the Google Sign-In result.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (varies by activity).
     */
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
    /**
     * Authenticates with Firebase using the Google ID token.
     *
     * @param idToken The Google ID token obtained from Google Sign-In.
     *
     */
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

