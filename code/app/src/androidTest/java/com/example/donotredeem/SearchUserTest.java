package com.example.donotredeem;

import static android.content.ContentValues.TAG;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

/**
 * UI test class for searching a user in the application.
 */
public class SearchUserTest {
    /**
     * Launches the {@link MainActivity} before each test.
     */
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Sets up Firebase emulator for Firestore before all tests.
     */
    @BeforeClass
    public static void setup() {
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);

    }

    /**
     * Disables UI animations before all tests to improve test stability.
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
     * Seeds Firestore with test user data and mood events before each test.
     *
     * @throws InterruptedException if thread sleep is interrupted.
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
        MoodEvent moodEvent1 = new MoodEvent(location1, "User1", true, moodEventId1, "Happy", "03/08/2025",
                "14:30", "University Campus", "Alone", "Saw a cute rabbit", "Hello description", "");

        DocumentReference moodEventRef1 = db.collection("MoodEvents").document(moodEventId1);
        moodEventRef1.set(moodEvent1);


        String moodEventId2 = UUID.randomUUID().toString();

        GeoPoint location2 = new GeoPoint(53.5264, -113.5241);
        MoodEvent moodEvent2 = new MoodEvent(location2, "User1", true, moodEventId2, "Angry", "03/08/2025",
                "15:00", "Library", "Alone", "Loud noise", "Annoyed by the noise", "");

        DocumentReference moodEventRef2 = db.collection("MoodEvents").document(moodEventId2);
        moodEventRef2.set(moodEvent2);

        String loggedInUsername = "User1";
        DocumentReference userDocRef = db.collection("User").document(loggedInUsername);
        userDocRef.update("MoodRef", FieldValue.arrayUnion(moodEventRef1, moodEventRef2))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User document updated with mood events"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update user document", e));

        Thread.sleep(2000);

        ManualLoginCauseIDKMocking();
    }

    /**
     * Cleans up Firestore data by deleting all test documents after each test.
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
     * Performs manual login as a workaround due to lack of mocking.
     *
     * @throws InterruptedException if thread sleep is interrupted.
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
     * Tests searching for a user and verifying that the correct profile is displayed.
     *
     * @throws InterruptedException if thread sleep is interrupted.
     */
    @Test
    public void SearchingUser() throws InterruptedException {
        onView(withId(R.id.imageView4)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.search)).check(matches(isDisplayed()));
        onView(withId(R.id.search_bar)).perform(click());
        onView(withId(R.id.search_bar)).perform(ViewActions.typeText("User2"));
        Thread.sleep(1000);
        onView(withId(R.id.card_user)).perform(click());

        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.textView2)).check(matches(withText("User2")));

    }
}
