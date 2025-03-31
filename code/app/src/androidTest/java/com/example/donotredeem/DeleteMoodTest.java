package com.example.donotredeem;

import static android.content.ContentValues.TAG;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.allOf;

import android.util.Log;
import android.widget.ListView;

import androidx.test.espresso.action.ViewActions;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

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

/**
 * UI test for verifying the functionality of deleting a mood event.
 * This test uses Firebase's emulator for testing and disables animations for consistent results.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeleteMoodTest {

    /**
     * Launches the {@link MainActivity} before each test.
     */
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Configures Firebase to use the local emulator before all tests.
     */
    @BeforeClass
    public static void setup() {
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);

    }

    /**
     * Disables system animations for more reliable UI testing.
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
     * Seeds the Firebase Firestore database with test user data and a sample mood event.
     * This ensures there is a mood event available to delete during the test.
     *
     * @throws InterruptedException if the thread sleep is interrupted.
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
        GeoPoint location = new GeoPoint(53.5461, -113.4938);

        MoodEvent moodEvent1 = new MoodEvent(location, "User1", true, "Happy", "03/08/2025",
                "14:30", "University Campus", "Alone", "Saw a cute rabbit", "", "");

        DocumentReference moodEventRef1 = db.collection("MoodEvents").document(moodEventId1);
        moodEventRef1.set(moodEvent1);

        String loggedInUsername = "User1";
        DocumentReference userDocRef = db.collection("User").document(loggedInUsername);
        userDocRef.update("MoodRef", FieldValue.arrayUnion(moodEventRef1))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User document updated with mood events"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update user document", e));

        Thread.sleep(2000);

        ManualLoginCauseIDKMocking();
    }

    /**
     * Simulates manual login using Espresso UI interactions.
     *
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    public void ManualLoginCauseIDKMocking() throws InterruptedException {
        onView(withId(R.id.etUsername)).perform(ViewActions.typeText("User1"));
        onView(withId(R.id.etPassword)).perform(ViewActions.typeText("Password1"));

        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(5000);

        onView(withId(R.id.skip_button)).perform(click());

        onView(withId(R.id.main_activity)).check(matches(isDisplayed()));
        Thread.sleep(5000);

    }

    /**
     * Test for deleting a mood event.
     * The test navigates to the mood history page, swipes left to reveal the delete button,
     * deletes a mood event, and verifies the confirmation message.
     *
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    @Test
    public void DeletingAMood() throws InterruptedException {

        onView(withId(R.id.profilepage)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));

        onView(withId(R.id.side_panel_button)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.nav_history)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.mood_id)).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(allOf(withId(R.id.history_list), isAssignableFrom(ListView.class)))
                .atPosition(0)
                .perform(swipeLeft());

        onView(withId(R.id.delete_button_moodhistory)).perform(click());

        onView(withText("Mood event deleted"))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }

    /**
     * Cleans up the test database by deleting all documents.
     * This ensures a fresh state for subsequent tests.
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


}