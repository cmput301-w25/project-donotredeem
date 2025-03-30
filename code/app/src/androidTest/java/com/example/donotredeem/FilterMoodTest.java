package com.example.donotredeem;

import static android.content.ContentValues.TAG;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;

import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.donotredeem.Classes.Users;
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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FilterMoodTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setup() {
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);

    }
    @BeforeClass
    public static void disableAnimations() {
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global window_animation_scale 0");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global transition_animation_scale 0");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global animator_duration_scale 0");
    }

    @Before
    public void seedDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("User");

        Users[] user_data = {
                new Users("User1", "Password1", "user1@gmail.com", "This is my bio.", ""),
                new Users("User2", "Password2", "user2@gmail.com", "This is not my bio","")
        };

        for (Users user : user_data) {
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

        ManualLoginCauseIDKMocking();
    }


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


    public void ManualLoginCauseIDKMocking() throws InterruptedException {
        onView(withId(R.id.etUsername)).perform(ViewActions.typeText("User1"));
        onView(withId(R.id.etPassword)).perform(ViewActions.typeText("Password1"));

        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(5000);

        onView(withId(R.id.skip_button)).perform(click());

        onView(withId(R.id.main_activity)).check(matches(isDisplayed()));
        Thread.sleep(5000);

    }


    @Test
    public void FilterTestEmojiOnly() throws InterruptedException {
        onView(withId(R.id.profilepage)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        Thread.sleep(1000);

        onView(withId(R.id.profilepage)).check(matches(isDisplayed()));
        onView(withId(R.id.side_panel_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.nav_history)).perform(click());
        onView(withId(R.id.mood_id)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.filter_icon)).check(matches(isDisplayed()));

        onView(withId(R.id.filter_icon)).perform(ViewActions.click());
        Thread.sleep(1000);


        onView(withId(R.id.filter_emoji_happy)).check(matches(isDisplayed()));
        onView(withId(R.id.filter_emoji_happy)).perform(ViewActions.click());
        Thread.sleep(1000);


        onView(withId(R.id.done_filter_button)).check(matches(isDisplayed()));
        onView(withId(R.id.done_filter_button)).perform(ViewActions.click());
        Thread.sleep(1000);

        onView(allOf(withId(R.id.Emotional_State), withText("Happy")))
                .check(matches(isDisplayed()));
        Thread.sleep(2000);

        onView(withId(R.id.profilepage)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.profilepage)).check(matches(isDisplayed()));
        onView(withId(R.id.side_panel_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.Sign_out)).perform(click());



    }

    @Test
    public void FilterTestLastWeekOnly() throws InterruptedException {
        onView(withId(R.id.profilepage)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        Thread.sleep(1000);

        onView(withId(R.id.profilepage)).check(matches(isDisplayed()));
        onView(withId(R.id.side_panel_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.nav_history)).perform(click());
        onView(withId(R.id.mood_id)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.filter_icon)).check(matches(isDisplayed()));

        onView(withId(R.id.filter_icon)).perform(ViewActions.click());
        Thread.sleep(1000);


        onView(withId(R.id.week_filter_button)).check(matches(isDisplayed()));
        onView(withId(R.id.week_filter_button)).perform(ViewActions.click());
        Thread.sleep(1000);


        onView(withId(R.id.done_filter_button)).check(matches(isDisplayed()));
        onView(withId(R.id.done_filter_button)).perform(ViewActions.click());
        Thread.sleep(1000);

        onView(allOf(withId(R.id.Emotional_State), withText("Angry")))
                .check(matches(isDisplayed()));
        Thread.sleep(2000);

        onView(withId(R.id.profilepage)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.profilepage)).check(matches(isDisplayed()));
        onView(withId(R.id.side_panel_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.Sign_out)).perform(click());

    }

    @Test
    public void FilterTestWordSearchOnly() throws InterruptedException {
        onView(withId(R.id.profilepage)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        Thread.sleep(1000);

        onView(withId(R.id.profilepage)).check(matches(isDisplayed()));
        onView(withId(R.id.side_panel_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.nav_history)).perform(click());
        onView(withId(R.id.mood_id)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.filter_icon)).check(matches(isDisplayed()));

        onView(withId(R.id.filter_icon)).perform(ViewActions.click());
        Thread.sleep(1000);

        onView(withId(R.id.desc)).check(matches(isDisplayed()));
        onView(withId(R.id.desc)).perform(ViewActions.typeText("Hello"));
        Thread.sleep(1000);

        onView(withId(R.id.done_filter_button)).check(matches(isDisplayed()));
        onView(withId(R.id.done_filter_button)).perform(ViewActions.click());
        Thread.sleep(1000);

        onView(allOf(withId(R.id.Emotional_State), withText("Happy")))
                .check(matches(isDisplayed()));
        Thread.sleep(2000);

        onView(withId(R.id.profilepage)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.profilepage)).check(matches(isDisplayed()));
        onView(withId(R.id.side_panel_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.Sign_out)).perform(click());

    }

    @Test
    public void FilterTestMultipleNoDisplay() throws InterruptedException {
        onView(withId(R.id.profilepage)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        Thread.sleep(1000);

        onView(withId(R.id.profilepage)).check(matches(isDisplayed()));
        onView(withId(R.id.side_panel_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.nav_history)).perform(click());
        onView(withId(R.id.mood_id)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.filter_icon)).check(matches(isDisplayed()));

        onView(withId(R.id.filter_icon)).perform(ViewActions.click());
        Thread.sleep(1000);

        onView(withId(R.id.week_filter_button)).check(matches(isDisplayed()));
        onView(withId(R.id.week_filter_button)).perform(ViewActions.click());
        Thread.sleep(1000);

        onView(withId(R.id.desc)).check(matches(isDisplayed()));
        onView(withId(R.id.desc)).perform(ViewActions.typeText("Hello"));
        Thread.sleep(1000);

        onView(withId(R.id.done_filter_button)).check(matches(isDisplayed()));
        onView(withId(R.id.done_filter_button)).perform(ViewActions.click());
        Thread.sleep(3000);


        onView(allOf(withId(R.id.Emotional_State), withText("Happy")))
                .check(doesNotExist());
        onView(allOf(withId(R.id.Emotional_State), withText("Angry")))
                .check(doesNotExist());
        Thread.sleep(2000);

        onView(withId(R.id.profilepage)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.profilepage)).check(matches(isDisplayed()));
        onView(withId(R.id.side_panel_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.Sign_out)).perform(click());

    }


}