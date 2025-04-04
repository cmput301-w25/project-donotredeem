package com.example.donotredeem;

import static android.content.ContentValues.TAG;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;

import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.donotredeem.Activities.MainActivity;
import com.example.donotredeem.Classes.MoodEvent;
import com.example.donotredeem.Classes.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

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
import java.util.UUID;

import static androidx.test.espresso.Espresso.onData;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
/**
 * This class contains UI tests for the user registration process using Espresso.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterActivityUserTest {

    /**
     * Launches the {@link MainActivity} before each test.
     */
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * This class contains UI tests for the user registration process using Espresso.
     */
    @BeforeClass
    public static void setup() {
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);

    }
    /**
     * Disables system animations before executing UI tests to improve stability.
     */
    @BeforeClass
    public static void disableAnimations() {
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global window_animation_scale 0");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global transition_animation_scale 0");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global animator_duration_scale 0");
    }
    /**
     * Seeds the Firestore database with test users and mood events before each test.
     * @throws InterruptedException if thread sleep is interrupted
     */
    @Before
    public void seedDatabase() throws InterruptedException {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("User");

        User[] user_data = {
                new User("User1", "Password1", "user1@gmail.com", "This is my bio."),
                new User("User2", "Password2", "user2@gmail.com", "This is not my bio")
        };

        for (User user : user_data) {
            usersRef.document(user.getUsername()).set(user);
        }

        Thread.sleep(2000);

        String moodEventId1 = UUID.randomUUID().toString();
        GeoPoint location1 = new GeoPoint(53.5461, -113.4938);
        MoodEvent moodEvent1 = new MoodEvent(location1, "User1", true, moodEventId1, "Happy", "03/02/2025",
                "14:30", "University Campus", "Alone", "Saw a cute rabbit", "Hello description", "");

        DocumentReference moodEventRef1 = db.collection("MoodEvents").document(moodEventId1);
        moodEventRef1.set(moodEvent1);


        String moodEventId2 = UUID.randomUUID().toString();

        GeoPoint location2 = new GeoPoint(53.5264, -113.5241);
        MoodEvent moodEvent2 = new MoodEvent(location2, "User1", true, moodEventId2, "Angry", "29/03/2025",
                "15:00", "Library", "Alone", "Loud noise", "Annoyed by the noise", "");

        DocumentReference moodEventRef2 = db.collection("MoodEvents").document(moodEventId2);
        moodEventRef2.set(moodEvent2);

        String loggedInUsername = "User1";
        DocumentReference userDocRef = db.collection("User").document(loggedInUsername);
        userDocRef.update("MoodRef", FieldValue.arrayUnion(moodEventRef1, moodEventRef2))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User document updated with mood events"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update user document", e));

        Thread.sleep(2000);


    }

    /**
     * Cleans up Firestore database after each test by deleting all test data.
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
        }
    }

    /**
     * Tests the entire registration flow, verifying UI elements and interactions.
     * @throws InterruptedException if thread sleep is interrupted
     */
    @Test
    public void RegisterSuccessful() throws InterruptedException {
        onView(withId(R.id.button4)).perform(click());

        onView(withId(R.id.sign_up_id)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_up_name_text)).perform(ViewActions.typeText("bruh")).perform(closeSoftKeyboard());
        onView(withId(R.id.sign_up_email_text)).perform(ViewActions.typeText("tissue@gmail.com")).perform(closeSoftKeyboard());
        onView(withId(R.id.sign_up_phone_number_text)).perform(ViewActions.typeText("1234567890")).perform(closeSoftKeyboard());
        onView(withId(R.id.sign_up_done)).perform(click());

        onView(withId(R.id.sign_up_id_2)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_up_username_text)).perform(ViewActions.typeText("bruh123456789")).perform(closeSoftKeyboard());
        onView(withId(R.id.sign_up_password_text)).perform(ViewActions.typeText("password")).perform(closeSoftKeyboard());
        onView(withId(R.id.sign_up_done)).perform(click());

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
                is(instanceOf(String.class)),
                is("Daily")
        )).perform(click());
        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.test4)).check(matches(isDisplayed()));
    }


}