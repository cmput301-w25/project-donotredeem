//package com.example.donotredeem;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.res.ColorStateList;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.material.snackbar.Snackbar;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
///**
// * Activity for registering a new user.
// *
// * This activity guides the user through multiple registration pages,
// * collecting essential details such as name, email, phone number, username, and password.
// * It uses Firebase Authentication to create a new account and Firestore to store user data.
// * The registration process also checks for username uniqueness and saves the username locally
// * in SharedPreferences upon successful registration.
// *
// */
//public class Register extends AppCompatActivity {
//
//    private List<String> selectedActivities = new ArrayList<>();
//    private Map<ImageButton, Boolean> buttonStates = new HashMap<>();
//
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//
//    // User data variables
//    private String name, email, phone, username, password, birthDate, activity, moodRecurrence, reminderMood, test4Data;
//
//    // UI Elements
//    private EditText etName, etEmail, etPhoneNo, etUsername, etPassword, etBirthDate, etActivity, etMoodRecurrence, etReminderMood, etTest4Data;
//    private Button btnNext;
//
//    private int currentPage = 1; // Keeps track of the page user is on
//
//    /**
//     * Called when the activity is first created.
//     * Initializes Firebase Authentication and Firestore, and displays the first registration page.
//     *
//     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied. Otherwise, it is null.
//     */
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        showPage(currentPage);
//    }
//
//    /**
//     * Displays the appropriate registration page based on the given page number.
//     * <p>
//     * Depending on the current page, different layouts are set.
//     * When the last page is reached, user registration is initiated.
//     *
//     * @param page The registration page number to display.
//     */
//    private void showPage(int page) {
//        switch (page) {
//            case 1:
//                setContentView(R.layout.sign_up_page1);
//                etName = findViewById(R.id.sign_up_name_text);
//                etEmail = findViewById(R.id.sign_up_email_text);
//                etPhoneNo = findViewById(R.id.sign_up_phone_number_text);
//                btnNext = findViewById(R.id.sign_up_done);
//                break;
//            case 2:
//                setContentView(R.layout.sign_up_page2);
//                etUsername = findViewById(R.id.sign_up_username_text);
//                etPassword = findViewById(R.id.sign_up_password_text);
//                btnNext = findViewById(R.id.sign_up_done);
//                break;
//            case 3:
//                setContentView(R.layout.birthday);
//                etBirthDate = findViewById(R.id.editTextDate);
//                btnNext = findViewById(R.id.next_button_bday);
//                break;
////            case 4:
////                setContentView(R.layout.activities);
////                etActivity = findViewById(R.id.etActivity);
////                btnNext = findViewById(R.id.next_button_activities);
////                break;
//            case 4:
//                setContentView(R.layout.activities);
//                btnNext = findViewById(R.id.next_button_activities);
//                setupActivityButtons();
//                break;
//
////            case 5:
////                setContentView(R.layout.mood_recurrence);
////                etMoodRecurrence = findViewById(R.id.etMoodRecurrence);
////                btnNext = findViewById(R.id.btnNext);
////                break;
////            case 6:
////                setContentView(R.layout.remindermood);
////                etReminderMood = findViewById(R.id.etReminderMood);
////                btnNext = findViewById(R.id.btnNext);
////                break;
////            case 7:
////                setContentView(R.layout.test4);
////                etTest4Data = findViewById(R.id.etTest4Data);
////                btnNext = findViewById(R.id.btnNext);
////                break;
////            case 8:
////                registerUser();
////                return;
//            case 5:
//                registerUser();
//                return;
//        }
//
//        // Set button click listener for navigation
//        btnNext.setOnClickListener(v -> {
//            storeData(currentPage);
//            currentPage++;
//            showPage(currentPage);
//        });
//    }
//
//    /**
//     * Stores the user input data from the current registration page.
//     *
//     * @param page The current registration page number.
//     */
//    private void storeData(int page) {
//        switch (page) {
//            case 1:
//                name = etName.getText().toString().trim();
//                email = etEmail.getText().toString().trim();
//                phone = etPhoneNo.getText().toString().trim();
//                break;
//            case 2:
//                username = etUsername.getText().toString().trim();
//                password = etPassword.getText().toString().trim();
//                break;
//            case 3:
//                birthDate = etBirthDate.getText().toString().trim();
//                break;
////            case 4:
////                activity = etActivity.getText().toString().trim();
////                break;
////            case 5:
////                moodRecurrence = etMoodRecurrence.getText().toString().trim();
////                break;
////            case 6:
////                reminderMood = etReminderMood.getText().toString().trim();
////                break;
////            case 7:
////                test4Data = etTest4Data.getText().toString().trim();
////                break;
//        }
//    }
//
//    /**
//     * Checks if the username is unique and registers the user.
//     * <p>
//     * The method first checks Firestore to see if the username already exists.
//     * If the username is unique, it creates a new user using Firebase Authentication and then saves the user's data in Firestore. Upon success, the username is stored in SharedPreferences.
//     */
//    private void registerUser() {
//        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
//            //Toast.makeText(this, "Email, Password, and Username are required!", Toast.LENGTH_LONG).show();
//            Snackbar.make(findViewById(android.R.id.content), "Email, Password, and Username are required!", Snackbar.LENGTH_LONG).show();
//
//            return;
//        }
//
//        DocumentReference userDocRef = db.collection("User").document(username);
//        userDocRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    // Username already exists
//                    //Toast.makeText(Register.this, "Username already taken!", Toast.LENGTH_LONG).show();
//                    Snackbar.make(findViewById(android.R.id.content), "Username already taken!", Snackbar.LENGTH_LONG).show();
//
//                    currentPage = 2;
//                    showPage(currentPage);
//                } else {
//                    // Username is unique
//                    mAuth.createUserWithEmailAndPassword(email, password)
//                            .addOnCompleteListener(Register.this, authTask -> {
//                                if (authTask.isSuccessful()) {
//                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                                    if (firebaseUser != null) {
//                                        saveUserData(firebaseUser);
//                                    }
//                                } else {
//                                    Log.e("Register", "Registration failed: " + authTask.getException().getMessage());
//                                    //Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_SHORT).show();
//                                    Snackbar.make(findViewById(android.R.id.content), "Registration failed.", Snackbar.LENGTH_LONG).show();
//
//                                }
//                            });
//                }
//            } else {
//                //Toast.makeText(Register.this, "Error checking username uniqueness!", Toast.LENGTH_SHORT).show();
//                Snackbar.make(findViewById(android.R.id.content), "Error checking username uniqueness!", Snackbar.LENGTH_LONG).show();
//
//            }
//        });
//    }
//
//    /**
//     * Saves the registered user's data to Firestore.
//     * <p>
//     * The user data is stored in a Map and written to a Firestore document under the \"User\" collection.
//     * Additionally, the username is saved locally using SharedPreferences.
//     * Upon success, the user is navigated to the MainActivity.
//     *
//     * @param firebaseUser The FirebaseUser object representing the newly registered user.
//     */
//    private void saveUserData(FirebaseUser firebaseUser) {
//        Map<String, Object> user = new HashMap<>();
//        user.put("name", name);
//        user.put("email", email);
//        user.put("phone", phone);
//        user.put("password", password);
//        user.put("username", username);
//        user.put("birthDate", birthDate);
////        user.put("activity", activity);
//        user.put("activities", selectedActivities);
////        user.put("moodRecurrence", moodRecurrence);
////        user.put("reminderMood", reminderMood);
////        user.put("test4Data", test4Data);
//
//        DocumentReference userDoc = db.collection("User").document(username);
//        userDoc.set(user)
//                .addOnSuccessListener(aVoid -> {
//                    Log.d("Register", "User data successfully written to Firestore");
//
//                    //SharedPreferences kinda need to do this
//                    SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("username", username);
//                    editor.apply();
//
//                    //Toast.makeText(Register.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
//                    Snackbar.make(findViewById(android.R.id.content), "Registration Successful!", Snackbar.LENGTH_LONG).show();
//
//                    startActivity(new Intent(Register.this, MainActivity.class));
//                    finish();
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("Register", "Error writing document", e);
//                    //Toast.makeText(Register.this, "Error saving user data!", Toast.LENGTH_SHORT).show();
//                    Snackbar.make(findViewById(android.R.id.content), "Error saving user data!", Snackbar.LENGTH_LONG).show();
//
//                });
//    }
//
//    private void setupActivityButtons() {
//        int[] buttonIds = {
//                R.id.music_button, R.id.dancer_button, R.id.joystick_button,
//                R.id.walk_button, R.id.playing_button, R.id.travel_button,
//                R.id.relationship_button, R.id.art_button, R.id.cooking_button,
//                R.id.reading_button
//        };
//
//        Map<Integer, String> activityMap = new HashMap<Integer, String>() {{
//            put(R.id.music_button, "Music");
//            put(R.id.dancer_button, "Dancing");
//            put(R.id.joystick_button, "Gaming");
//            put(R.id.walk_button, "Walking");
//            put(R.id.playing_button, "Sports");
//            put(R.id.travel_button, "Traveling");
//            put(R.id.relationship_button, "Socializing");
//            put(R.id.art_button, "Art");
//            put(R.id.cooking_button, "Cooking");
//            put(R.id.reading_button, "Reading");
//        }};
//
//        for (int buttonId : buttonIds) {
//            ImageButton button = findViewById(buttonId);
//            buttonStates.put(button, false);
//
//            button.setOnClickListener(v -> {
//                boolean isSelected = Boolean.FALSE.equals(buttonStates.get(button));
//                String activityName = activityMap.get(buttonId);
//
//                if (isSelected) {
//                    selectedActivities.add(activityName);
//                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Green
//                } else {
//                    selectedActivities.remove(activityName);
//                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D9D9D9"))); // Original color
//                }
//
//                buttonStates.put(button, isSelected);
//                updateNextButtonState();
//            });
//        }
//
//    }
//    private void updateNextButtonState () {
//        btnNext.setEnabled(!selectedActivities.isEmpty());
//        btnNext.setAlpha(selectedActivities.isEmpty() ? 0.5f : 1.0f);
//    }
//}

package com.example.donotredeem;

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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {

    private List<String> selectedActivities = new ArrayList<>();
    private Map<ImageButton, Boolean> buttonStates = new HashMap<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // User data variables
    private String name, email, phone, username, password, birthDate;

    // UI Elements
    private EditText etName, etEmail, etPhoneNo, etUsername, etPassword, etBirthDate;
    private Button btnNext;

    private int currentPage = 1;
    private String reminderMood = "";
    private Spinner moodFrequencySpinner;
    private String moodRecurrence = "";

    Boolean reccurences;

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


    private void initializePage1() {
        etName = findViewById(R.id.sign_up_name_text);
        etEmail = findViewById(R.id.sign_up_email_text);
        etPhoneNo = findViewById(R.id.sign_up_phone_number_text);
        btnNext = findViewById(R.id.sign_up_done);
        etName.setText(name != null ? name : "");
        etEmail.setText(email != null ? email : "");
        etPhoneNo.setText(phone != null ? phone : "");
    }

    private void initializePage2() {
        etUsername = findViewById(R.id.sign_up_username_text);
        etPassword = findViewById(R.id.sign_up_password_text);
        btnNext = findViewById(R.id.sign_up_done);
        etUsername.setText(username != null ? username : "");
        etPassword.setText(password != null ? password : "");
    }

    private void initializePage3() {
        etBirthDate = findViewById(R.id.editTextDate);
        btnNext = findViewById(R.id.next_button_bday);
        etBirthDate.setText(birthDate != null ? birthDate : "");
    }

//    private void initializeActivitiesPage() {
//        btnNext = findViewById(R.id.next_button_activities);
//        setupActivityButtons();
//        updateNextButtonState();
//    }
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

//    private void setupReminderButtons() {
//        Button yesButton = findViewById(R.id.yes_remindermood);
//        Button noButton = findViewById(R.id.no_remindermood);
//
//        if (yesButton == null || noButton == null) {
//            Log.e("Register", "Reminder buttons not found!");
//            return;
//        }
//
//        View.OnClickListener reminderListener = v -> {
//            // Set reminder value
//            if (v.getId() == R.id.yes_remindermood) {
//                reminderMood = "YES";
//            } else {
//                reminderMood = "NO";
//            }
//
//            // Add slight delay for visual feedback
//            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                storeData(currentPage);
//                currentPage++;
//                showPage(currentPage);
//            }, 200);
//        };
//
//        yesButton.setOnClickListener(reminderListener);
//        noButton.setOnClickListener(reminderListener);
//    }
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
//    private void setupMoodRecurrence() {
//        moodFrequencySpinner = findViewById(R.id.mood_frequency);
//
//        // Create adapter using string array
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.mood_frequency_options, android.R.layout.simple_spinner_item);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        moodFrequencySpinner.setAdapter(adapter);
//
//        // Set default selection
//        moodFrequencySpinner.setSelection(0);
//
//        // Store selection when changed
//        moodFrequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                moodRecurrence = parent.getItemAtPosition(position).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                moodRecurrence = "";
//            }
//        });
//
//        btnNext = findViewById(R.id.next_button); // Add this button to your XML
//    }
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


    private void setupNavigation() {
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

    private void updateNextButtonState() {
        boolean hasSelections = !selectedActivities.isEmpty();
        btnNext.setEnabled(hasSelections);
        btnNext.setAlpha(hasSelections ? 1.0f : 0.5f);
    }

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
        user.put("followers", 0);  // No followers initially
        user.put("following", 0);  // Not following anyone initially
        user.put("followers_list", new java.util.ArrayList<>());  // Empty follower list
        user.put("following_list", new java.util.ArrayList<>());  // Empty following list
        user.put("requests", new java.util.ArrayList<>()); // Initialize requests as empty
        user.put("moods", 0);

        db.collection("User").document(username)
                .set(user)
                .addOnSuccessListener(aVoid -> handleRegistrationSuccess())
                .addOnFailureListener(e -> handleRegistrationFailure(e));
    }

//    private void handleRegistrationSuccess() {
//        saveUsernameToPreferences();
//        showSnackbar("Registration Successful!");
//        startActivity(new Intent(Register.this, MainActivity.class));
//        finish();
//    }
    private void handleRegistrationSuccess() {
        saveUsernameToPreferences();
        showSnackbar("Registration Successful!");

        // Directly launch MoodSelectionActivity
        startActivity(new Intent(Register.this, MoodSelectionActivity.class));
        finish();
    }

    private void saveUsernameToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("username", username).apply();
    }

    private void handleRegistrationFailure(Exception e) {
        Log.e("Register", "Error saving user data", e);
        showSnackbar("Error saving user data!");
    }

    private void handleUsernameTaken() {
        showSnackbar("Username already taken!");
        currentPage = 2;
        showPage(currentPage);
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

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
