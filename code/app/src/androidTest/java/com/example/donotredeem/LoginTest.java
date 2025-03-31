package com.example.donotredeem;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.donotredeem.Activities.LogInActivity;
import com.example.donotredeem.Classes.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
/**
 * Instrumented test class for testing login and sign-up functionality.
 * Uses Firebase Firestore emulator for database interactions.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {
    /**
     * Launches the LogIn activity before each test.
     */
    @Rule
    public ActivityScenarioRule<LogInActivity> scenario = new ActivityScenarioRule<>(LogInActivity.class);

    /**
     * Sets up Firebase Firestore emulator for testing.
     */
    @BeforeClass
    public static void setup() {
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);

    }

    /**
     * Disables system animations to ensure UI tests run consistently.
     */
    @BeforeClass
    public static void disableAnimations(){
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global window_animation_scale 0"
        );
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global transition_animation_scale 0"
        );
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global animator_duration_scale 0"
        );
    }
    /**
     * Seeds the Firestore database with test users before each test case.
     */
    @Before
    public void seedDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("User");
        User[] user_data = {
                new User("User1", "Password1", "user1@gmail.com", "This is my bio."),
                new User("User2", "Password2", "user2@gmail.com", "This is not my bio")
        };
        for (User users : user_data) {
            usersRef.document(users.getUsername()).set(users);
        }
        Thread.sleep(2000);
    }
    /**
     * Cleans up Firestore database after each test case.
     */
    @After
    public void tearDown() {

        String projectId = "login-register-de540";
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Response Code: " + response);
        } catch (IOException exception) {
            Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }}
    /**
     * Tests successful login when the user exists.
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    @Test
    public void SuccessfulLoginBecauseUserExists() throws InterruptedException {
        // Enter username and password
        Thread.sleep(2000);
        onView(withId(R.id.etUsername)).perform(typeText("User1"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Password1"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(500);
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Login successfully.")))
                .check(matches(isDisplayed()));
        Thread.sleep(2000);
    }
    /**
     * Tests login failure when the user does not exist.
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    @Test
    public void UserDoesNotExist() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.etUsername)).perform(typeText("UserDoesNotExist"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Password3"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("User not found.")))
                .check(matches(isDisplayed()));
        Thread.sleep(2000);
    }

    /**
     * Tests sign-up failure when the username is already in use.
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    @Test
    public void SignUpWithAUsernameAlreadyInUse() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.button4)).perform(click());
        onView(withId(R.id.sign_up_id)).check(matches(isDisplayed()));

        onView(withId(R.id.sign_up_name_text)).perform(typeText("New name"), closeSoftKeyboard());
        onView(withId(R.id.sign_up_email_text)).perform(typeText("email@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.sign_up_phone_number_text)).perform(typeText("9876543210"), closeSoftKeyboard());
        onView(withId(R.id.sign_up_done)).perform(click());

        onView(withId(R.id.sign_up_id_2)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_up_username_text)).perform(typeText("User1"), closeSoftKeyboard());
        onView(withId(R.id.sign_up_password_text)).perform(typeText("New password"), closeSoftKeyboard());
        onView(withId(R.id.sign_up_done)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.birthday)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextDate)).perform(ViewActions.typeText("26/02/2005")).perform(closeSoftKeyboard());
        onView(withId(R.id.next_button_bday)).perform(click());

        onView(withId(R.id.activities)).check(matches(isDisplayed()));
        onView(isRoot()).perform(closeSoftKeyboard());
        onView(withId(R.id.music_button))
                .perform(scrollTo(), click());
        onView(withId(R.id.art_button))
                .perform(scrollTo(), click());
        onView(withId(R.id.next_button_activities))
                .perform(scrollTo(), click());

        onView(withId(R.id.remindermood)).check(matches(isDisplayed()));
        onView(withId(R.id.yes_remindermood)).perform(click());

        onView(withId(R.id.mood_recurrence)).check(matches(isDisplayed()));
        onView(withId(R.id.mood_frequency)).perform(click());
        onData(allOf(
                Matchers.is(instanceOf(String.class)),
                Matchers.is("Daily")
        )).perform(click());
        onView(withId(R.id.next_button)).perform(click());

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Username already taken!")))
                .check(matches(isDisplayed()));
        Thread.sleep(2000);
    }
    /**
     * Tests sign-up failure when the email is already in use.
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    @Test
    public void SignUpWithAnEmailAlreadyInUse() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.button4)).perform(click());
        onView(withId(R.id.sign_up_id)).check(matches(isDisplayed()));

        onView(withId(R.id.sign_up_name_text)).perform(typeText("New name"), closeSoftKeyboard());
        onView(withId(R.id.sign_up_email_text)).perform(typeText("user1@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.sign_up_phone_number_text)).perform(typeText("7317555555"), closeSoftKeyboard());
        onView(withId(R.id.sign_up_done)).perform(click());

        onView(withId(R.id.sign_up_id_2)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_up_username_text)).perform(typeText("Newuser"), closeSoftKeyboard());
        onView(withId(R.id.sign_up_password_text)).perform(typeText("New password"), closeSoftKeyboard());
        onView(withId(R.id.sign_up_done)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.birthday)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextDate)).perform(ViewActions.typeText("26/02/2005")).perform(closeSoftKeyboard());
        onView(withId(R.id.next_button_bday)).perform(click());

        onView(withId(R.id.activities)).check(matches(isDisplayed()));
        onView(isRoot()).perform(closeSoftKeyboard());
        onView(withId(R.id.music_button))
                .perform(scrollTo(), click());
        onView(withId(R.id.art_button))
                .perform(scrollTo(), click());
        onView(withId(R.id.next_button_activities))
                .perform(scrollTo(), click());

        onView(withId(R.id.remindermood)).check(matches(isDisplayed()));
        onView(withId(R.id.yes_remindermood)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.mood_recurrence)).check(matches(isDisplayed()));
        onView(withId(R.id.mood_frequency)).perform(click());
        onData(allOf(
                Matchers.is(instanceOf(String.class)),
                Matchers.is("Daily")
        )).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.next_button)).perform(click());

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Error checking username uniqueness!")))
                .check(matches(isDisplayed()));
        Thread.sleep(2000);
    }
}