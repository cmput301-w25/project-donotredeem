package com.example.donotredeem;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.donotredeem.Classes.Users;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddMoodEventTest {


    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setup() {
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);

    }

    @Before
    public void seedDatabase() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("User");
        Users[] user_data = {
                new Users("User1", "Password1", "user1@gmail.com", "This is my bio.", ""),
                new Users("User2", "Password2", "user2@gmail.com", "This is not my bio", "")
        };
        for (Users users : user_data) {
            usersRef.document(users.getUsername()).set(users);
        }
        Thread.sleep(2000);
    }

    public void ManualLoginCauseIDKMocking() throws InterruptedException {
        onView(withId(R.id.etUsername)).perform(ViewActions.typeText("User1"));
        onView(withId(R.id.etPassword)).perform(ViewActions.typeText("Password1"));

        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.skip_button)).perform(click());

        onView(withId(R.id.main_activity)).check(matches(isDisplayed()));
        onView(withId(R.id.add_button)).perform(click());

    }

    @Test
    public void CorrectSubmissionOfAddMoodEvent() throws InterruptedException {
        ManualLoginCauseIDKMocking();

        onView(withId(R.id.add_mood)).check(matches(isDisplayed()));
        onView(withContentDescription("Happy Emoji")).perform(click());

        onView(withId(R.id.desc)).perform(click());
        onView(withId(R.id.desc)).perform(ViewActions.typeText("Correct description"));

        onView(withId(R.id.reasoning)).perform(scrollTo(), click());
        onView(withId(R.id.reasoning)).perform(ViewActions.typeText("This is a trigger"));

        onView(withId(R.id.alone_social)).perform(scrollTo(), click());
        onView(withId(R.id.button)).perform(scrollTo(), click());

        onView(withText("Mood Event Saved!"))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }
    @Test
    public void MoodNotAdded() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.add_mood)).check(matches(isDisplayed()));

        onView(withContentDescription("Happy Emoji")).perform(click());
        onView(withId(R.id.desc)).perform(click());
        onView(withId(R.id.desc)).perform(ViewActions.typeText("I hate testing"));

        onView(withId(R.id.closeButton)).perform(click());

        onView(withText("Mood event not saved!"))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }

    @Test
    public void MoodRequired() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.add_mood)).check(matches(isDisplayed()));

        onView(withId(R.id.button)).perform(scrollTo(), click());
        onView(withText("Please select a mood!"))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void DescriptionRequired() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.add_mood)).check(matches(isDisplayed()));

        onView(withContentDescription("Happy Emoji")).perform(click());
        onView(withId(R.id.button)).perform(scrollTo(), click());

        onView(withText("Provide either a description or an image!"))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }

//    @Test
//    public void DateRequired() {
//        onView(withId(R.id.add_button)).perform(click());
//        onView(withId(R.id.add_mood)).check(matches(isDisplayed()));
//
//        onView(withId(R.id.emoji_happy)).perform(click());
//        onView(withId(R.id.desc)).perform(click());
//        onView(withId(R.id.desc)).perform(ViewActions.typeText("I hate testing"));
//
//        onView(withId(R.id.button)).perform(scrollTo(), click());
//
//                onView(withText("Please select a date!"))
//                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
//
//    }
//
//    @Test
//    public void TimeRequired() {
//        onView(withId(R.id.add_button)).perform(click());
//        onView(withId(R.id.add_mood)).check(matches(isDisplayed()));
//
//        onView(withId(R.id.emoji_happy)).perform(click());
//        onView(withId(R.id.desc)).perform(click());
//        onView(withId(R.id.desc)).perform(ViewActions.typeText("I hate testing"));
//
//        onView(withId(R.id.button)).perform(scrollTo(), click());
//        onView(withId(R.id.dateButton)).perform(click());
//
//        onView(withId(R.id.button)).perform(scrollTo(), click());
//
//        onView(withText("Please select a time!"))
//                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
//
//
//    }

    @Test
    public void DescriptionConstraints() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.add_mood)).check(matches(isDisplayed()));

        onView(withId(R.id.desc)).perform(click());
        onView(withId(R.id.desc)).perform(ViewActions.typeText("This description does not allow more than two hundred characters you can do anything it wont allow lol hahaha you lose ok yay bye this testing is not good okay i am just doing time pass okkkk just adding more characters lol is it working???"));

        onView(withId(R.id.desc)).check(matches(CustomMatchers.withTextLength(lessThanOrEqualTo(200))));

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
