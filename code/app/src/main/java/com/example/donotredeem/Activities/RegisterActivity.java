package com.example.donotredeem.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.donotredeem.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the user registration process across multiple pages.
 * Includes Firebase authentication and Firestore integration.
 */
public class RegisterActivity extends AppCompatActivity {

    private List<String> selectedActivities = new ArrayList<>();
    private Map<ImageButton, Boolean> buttonStates = new HashMap<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // User data variables
    private String name, email, phone, username, password, birthDate;

    // UI Elements
    private EditText etName, etEmail, etPhoneNo, etUsername, etPassword, etBirthDate;
    private Button btnNext;
    private ImageButton calender;

    private int currentPage = 1;
    private String reminderMood = "";
    private Spinner moodFrequencySpinner;
    private String moodRecurrence = "";

    Boolean reccurences;
    /** Activity-lifecycle method that initializes registration process */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        showPage(currentPage);
    }

    /**
     * Displays specific registration page based on current progress
     * @param page The page number to display (1-7)
     */
    private void showPage(int page) {
        switch (page) {
            case 1:
                setContentView(R.layout.sign_up_page1);
                initializePage1();
                setupBackButton(true);
                break;
            case 2:
                setContentView(R.layout.sign_up_page2);
                initializePage2();
                setupBackButton(false);
                break;
            case 3:
                setContentView(R.layout.birthday);
                initializePage3();
                setupBackButton(false);
                break;
            case 4:
                setContentView(R.layout.activities);
                initializeActivitiesPage();
                setupBackButton(false);
                break;
            case 5:
                setContentView(R.layout.remindermood);
                setupReminderButtons();
                setupBackButton(false);
                break;
            case 6:
                setContentView(R.layout.mood_recurrence); // Your XML name
                setupMoodRecurrence();
                setupBackButton(false);
                break;
            case 7:
                registerUser();
                return;
        }

        setupNavigation();
    }

    /**
     * Configures back button behavior with conditional exit logic
     * @param exitOnBack Determines if back button exits registration on first page
     */
    private void setupBackButton(boolean exitOnBack) {
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (exitOnBack) {
                    finish(); // Exit registration on first page
                } else {
                    if ((currentPage == 7) && (reccurences == false)){currentPage = currentPage - 2;}
                    else {currentPage--;}
                    showPage(currentPage); // Navigate to previous page
                }
            });
        }
    }


    /** Initializes first page with personal information fields */
    private void initializePage1() {
        etName = findViewById(R.id.sign_up_name_text);
        etEmail = findViewById(R.id.sign_up_email_text);
        etPhoneNo = findViewById(R.id.sign_up_phone_number_text);
        btnNext = findViewById(R.id.sign_up_done);
        etName.setText(name != null ? name : "");
        etEmail.setText(email != null ? email : "");
        etPhoneNo.setText(phone != null ? phone : "");
    }

    /** Initializes second page with credential input fields */
    private void initializePage2() {
        etUsername = findViewById(R.id.sign_up_username_text);
        etPassword = findViewById(R.id.sign_up_password_text);
        btnNext = findViewById(R.id.sign_up_done);
        etUsername.setText(username != null ? username : "");
        etPassword.setText(password != null ? password : "");
    }

    /** Initializes third page with date picker for birthdate selection */
    private void initializePage3() {
        etBirthDate = findViewById(R.id.editTextDate);
        calender = findViewById(R.id.calendar_bday);
        btnNext = findViewById(R.id.next_button_bday);
        etBirthDate.setText(birthDate != null ? birthDate : "");
        calender.setOnClickListener(v ->{
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH); //january is 0
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view1, selectedYear, selectedMonth, selectedDay) -> {

                String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                etBirthDate.setText(selectedDate);

            }, year, month, day
            );

            datePickerDialog.show();
        });
    }

    /** Initializes fourth page with activity selection buttons */
    private void initializeActivitiesPage() {
    btnNext = findViewById(R.id.next_button_activities);
    setupActivityButtons();
    updateNextButtonState();

    // Restore selected activities
    int[] buttonIds = {
            R.id.music_button, R.id.dancer_button, R.id.joystick_button,
            R.id.walk_button, R.id.playing_button, R.id.travel_button,
            R.id.relationship_button, R.id.art_button, R.id.cooking_button,
            R.id.reading_button
    };

    for (int buttonId : buttonIds) {
        ImageButton button = findViewById(buttonId);
        if (button != null) {
            String activityName = activityMap.get(buttonId);
            boolean isSelected = selectedActivities.contains(activityName);
            buttonStates.put(button, isSelected);
            button.setBackgroundTintList(ColorStateList.valueOf(
                    isSelected ? Color.parseColor("#4CAF50") : Color.parseColor("#D9D9D9")
            ));
        }
    }
}

    /** Configures mood reminder selection buttons with visual feedback */
    private void setupReminderButtons() {
    Button yesButton = findViewById(R.id.yes_remindermood);
    Button noButton = findViewById(R.id.no_remindermood);

    // Restore previous selection
    if (reminderMood.equals("YES")) {
        yesButton.setBackgroundColor(Color.parseColor("#4CAF50"));
    } else if (reminderMood.equals("NO")) {
        noButton.setBackgroundColor(Color.parseColor("#4CAF50"));
    }

    View.OnClickListener reminderListener = v -> {
        // Reset colors
        yesButton.setBackgroundColor(Color.TRANSPARENT);
        noButton.setBackgroundColor(Color.TRANSPARENT);

        if (v.getId() == R.id.yes_remindermood) {
            reminderMood = "YES";
            yesButton.setBackgroundColor(Color.parseColor("#4CAF50"));
            currentPage++;
            reccurences = true;
        } else {
            reminderMood = "NO";
            noButton.setBackgroundColor(Color.parseColor("#4CAF50"));
            currentPage = currentPage + 2;
            reccurences = false;
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            storeData(currentPage);
            showPage(currentPage);
        }, 200);
    };

    yesButton.setOnClickListener(reminderListener);
    noButton.setOnClickListener(reminderListener);
}

    /** Initializes mood recurrence frequency spinner with options */
    private void setupMoodRecurrence() {
        moodFrequencySpinner = findViewById(R.id.mood_frequency);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mood_frequency_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodFrequencySpinner.setAdapter(adapter);

        // Restore previous selection
        if (!moodRecurrence.isEmpty()) {
            int position = adapter.getPosition(moodRecurrence);
            moodFrequencySpinner.setSelection(position);
        } else {
            moodFrequencySpinner.setSelection(0);
        }

        moodFrequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                moodRecurrence = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                moodRecurrence = "";
            }
        });

        btnNext = findViewById(R.id.next_button);
    }

    /** Sets up navigation button and page progression logic */
    private void setupNavigation() {
        btnNext.setOnClickListener(v -> {
            storeData(currentPage);
            currentPage++;
            showPage(currentPage);
        });
    }

    /**
     * Stores user input data when progressing between pages
     * @param page The current page number being stored
     */
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
            case 3:
                birthDate = etBirthDate.getText().toString().trim();
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
        }
    }

    /** Configures activity selection buttons with toggle behavior */
    private void setupActivityButtons() {
        int[] buttonIds = {
                R.id.music_button, R.id.dancer_button, R.id.joystick_button,
                R.id.walk_button, R.id.playing_button, R.id.travel_button,
                R.id.relationship_button, R.id.art_button, R.id.cooking_button,
                R.id.reading_button
        };

        Map<Integer, String> activityMap = new HashMap<Integer, String>() {{
            put(R.id.music_button, "Music");
            put(R.id.dancer_button, "Dancing");
            put(R.id.joystick_button, "Gaming");
            put(R.id.walk_button, "Walking");
            put(R.id.playing_button, "Sports");
            put(R.id.travel_button, "Traveling");
            put(R.id.relationship_button, "Socializing");
            put(R.id.art_button, "Art");
            put(R.id.cooking_button, "Cooking");
            put(R.id.reading_button, "Reading");
        }};

        for (int buttonId : buttonIds) {
            ImageButton button = findViewById(buttonId);
            if (button == null) {
                Log.e("Register", "Button not found: " + buttonId);
                continue;
            }

            buttonStates.put(button, false);
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D9D9D9")));

            button.setOnClickListener(v -> {
                boolean isSelected = !buttonStates.get(button);
                String activityName = activityMap.get(v.getId());

                if (activityName == null) {
                    Log.e("Register", "No mapping found for button ID: " + v.getId());
                    return;
                }

                if (isSelected) {
                    selectedActivities.add(activityName);
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                } else {
                    selectedActivities.remove(activityName);
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D9D9D9")));
                }

                buttonStates.put(button, isSelected);
                updateNextButtonState();
            });
        }
    }

    /** Updates next button state based on activity selections */
    private void updateNextButtonState() {
        boolean hasSelections = !selectedActivities.isEmpty();
        btnNext.setEnabled(hasSelections);
        btnNext.setAlpha(hasSelections ? 1.0f : 0.5f);
    }

    /** Main registration method handling Firebase user creation */
    private void registerUser() {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            showSnackbar("Email, Password, and Username are required!");
            return;
        }

        DocumentReference userDocRef = db.collection("User").document(username);
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    handleUsernameTaken();
                } else {
                    createFirebaseUser();
                }
            } else {
                showSnackbar("Error checking username uniqueness!");
            }
        });
    }

    /**
     * Creates Firebase user account with email/password
     * Handles success/failure cases
     */
    private void createFirebaseUser() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserData(firebaseUser);
                        }
                    } else {
                        showSnackbar("Registration failed: " + task.getException().getMessage());
                    }
                });
    }

    /**
     * Saves complete user profile to Firestore
     * @param firebaseUser Authenticated Firebase user reference
     */
    private void saveUserData(FirebaseUser firebaseUser) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("phone", phone);
        user.put("password", password);
        user.put("username", username);
        user.put("birthDate", birthDate);
        user.put("activities", selectedActivities);
        user.put("reminderMood", reminderMood);
        user.put("moodRecurrence", moodRecurrence);
        user.put("bio", "");  // Initialize bio as empty
        user.put("follower_list", new java.util.ArrayList<>());  // Empty follower list
        user.put("following_list", new java.util.ArrayList<>());  // Empty following list
        user.put("requests", new java.util.ArrayList<>()); // Initialize requests as empty
        user.put("requested", new java.util.ArrayList<>()); //Initialize requests as empty
        user.put("moods", 0);

        db.collection("User").document(username)
                .set(user)
                .addOnSuccessListener(aVoid -> handleRegistrationSuccess())
                .addOnFailureListener(e -> handleRegistrationFailure(e));
    }


    /** Handles successful registration and launches next activity */
    private void handleRegistrationSuccess() {
        saveUsernameToPreferences();
        showSnackbar("Registration Successful!");

        // Directly launch MoodSelectionActivity
        startActivity(new Intent(RegisterActivity.this, MoodSelectionActivity.class));
        finish();
    }

    /** Persists username in SharedPreferences for session management */
    private void saveUsernameToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("username", username).apply();
    }

    /** Handles Firestore save failures with error logging */
    private void handleRegistrationFailure(Exception e) {
        Log.e("Register", "Error saving user data", e);
        showSnackbar("Error saving user data!");
    }

    /** Handles username conflict case by returning to credential page */
    private void handleUsernameTaken() {
        showSnackbar("Username already taken!");
        currentPage = 2;
        showPage(currentPage);
    }

    /**
     * Displays user feedback messages
     * @param message The message to display in snackbar
     */
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    /** Maps button to certain activities */
    private final Map<Integer, String> activityMap = new HashMap<Integer, String>() {{
        put(R.id.music_button, "Music");
        put(R.id.dancer_button, "Dancing");
        put(R.id.joystick_button, "Gaming");
        put(R.id.walk_button, "Walking");
        put(R.id.playing_button, "Sports");
        put(R.id.travel_button, "Traveling");
        put(R.id.relationship_button, "Socializing");
        put(R.id.art_button, "Art");
        put(R.id.cooking_button, "Cooking");
        put(R.id.reading_button, "Reading");
    }};
}
