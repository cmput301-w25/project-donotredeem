package com.example.donotredeem;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.donotredeem.LogIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class LogInTest {

    @Mock
    private FirebaseAuth mAuth;

    @Mock
    private FirebaseUser mockUser;

    @Mock
    private FirebaseFirestore db;

    @Mock
    private DocumentReference userDocRef;

    @Mock
    private Task<DocumentSnapshot> mockTask;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoginWithValidCredentials() {
        ActivityScenario<LogIn> scenario = ActivityScenario.launch(LogIn.class);
        scenario.onActivity(activity -> {
            // Mock Firestore behavior
            when(db.collection("User").document("testUser")).thenReturn(userDocRef);
            when(userDocRef.get()).thenReturn(mockTask);
            when(mockTask.isSuccessful()).thenReturn(true);
            DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
            when(mockTask.getResult()).thenReturn(documentSnapshot);
            when(documentSnapshot.exists()).thenReturn(true);
            when(documentSnapshot.getString("password")).thenReturn("testPass");

            // Simulate user input
            EditText usernameField = activity.findViewById(R.id.etUsername);
            EditText passwordField = activity.findViewById(R.id.etPassword);
            Button loginButton = activity.findViewById(R.id.btnLogin);

            usernameField.setText("testUser");
            passwordField.setText("testPass");

            loginButton.performClick();

            // Verify Firestore interactions
            verify(db.collection("User").document("testUser")).get();
        });
    }

    @Test
    public void testLoginWithInvalidPassword() {
        ActivityScenario<LogIn> scenario = ActivityScenario.launch(LogIn.class);
        scenario.onActivity(activity -> {
            when(db.collection("User").document("testUser")).thenReturn(userDocRef);
            when(userDocRef.get()).thenReturn(mockTask);
            when(mockTask.isSuccessful()).thenReturn(true);
            DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
            when(mockTask.getResult()).thenReturn(documentSnapshot);
            when(documentSnapshot.exists()).thenReturn(true);
            when(documentSnapshot.getString("password")).thenReturn("wrongPass");

            EditText usernameField = activity.findViewById(R.id.etUsername);
            EditText passwordField = activity.findViewById(R.id.etPassword);
            Button loginButton = activity.findViewById(R.id.btnLogin);

            usernameField.setText("testUser");
            passwordField.setText("testPass");

            loginButton.performClick();

            // Verify Firestore interactions and failed login
            verify(db.collection("User").document("testUser")).get();
        });
    }
}
