package com.example.donotredeem;

import static android.content.ContentValues.TAG;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.Press.FINGER;
import static androidx.test.espresso.action.Tap.SINGLE;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.allOf;
import static org.junit.runners.MethodSorters.DEFAULT;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.donotredeem.Classes.Users;
import com.google.android.material.snackbar.Snackbar;
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
public class EditMoodTest {

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
                new Users("User1", "Password1", "user1@gmail.com", "This is my bio."),
                new Users("User2", "Password2", "user2@gmail.com", "This is not my bio")
        };

        for (Users user : user_data) {
            usersRef.document(user.getUsername()).set(user);
        }

        Thread.sleep(2000);

        String moodEventId1 = UUID.randomUUID().toString();
        GeoPoint location = new GeoPoint(53.5461, -113.4938);

        MoodEvent moodEvent1 = new MoodEvent(location, "User1", true, moodEventId1, "Happy", "03/08/2025",
                "14:30", "University Campus", "Alone", "Saw a cute rabbit", "Hello description", "");

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

    public void ManualLoginCauseIDKMocking() throws InterruptedException {
        onView(withId(R.id.etUsername)).perform(ViewActions.typeText("User1"));
        Thread.sleep(5000);
        onView(withId(R.id.etPassword)).perform(ViewActions.typeText("Password1"));

        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.skip_button)).perform(click());

        onView(withId(R.id.main_activity)).check(matches(isDisplayed()));
        Thread.sleep(5000);

    }

    @Test
    public void EditingAMood() throws InterruptedException {

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

        onView(withId(R.id.edit_button_moodhistory)).perform(click());
        onView(withId(R.id.edit_id)).check(matches(isDisplayed()));
        Thread.sleep(1000);

        onView(withId(R.id.desc)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.desc)).perform(ViewActions.clearText());
        onView(withId(R.id.desc)).perform(ViewActions.typeText("This is editing"));
        onView(withId(R.id.desc)).perform(ViewActions.closeSoftKeyboard());
        Thread.sleep(2000);
        onView(withId(R.id.dateButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.timeButton)).perform(click());

        Thread.sleep(2000);
        onView(withId(R.id.button)).perform(scrollTo(),click());
        Thread.sleep(5000);
        onView(withId(R.id.mood_id)).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.Additional_details), withText("This is editing"))).check(matches(isDisplayed()));


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


}