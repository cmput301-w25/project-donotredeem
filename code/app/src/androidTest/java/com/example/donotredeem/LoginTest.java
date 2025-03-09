package com.example.donotredeem;

import static android.app.PendingIntent.getActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.IdlingPolicies.setIdlingResourceTimeout;
import static androidx.test.espresso.IdlingPolicies.setMasterPolicyTimeout;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import android.util.Log;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.donotredeem.Classes.Users;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matcher;
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
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static java.util.function.Predicate.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    @Rule
    public ActivityScenarioRule<LogIn> scenario = new ActivityScenarioRule<>(LogIn.class);


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
                new Users("User1", "Password1", "user1@gmail.com", "This is my bio."),
                new Users("User2", "Password2", "user2@gmail.com", "This is not my bio")
        };
        for (Users users : user_data) {
            usersRef.document(users.getUsername()).set(users);
        }
        Thread.sleep(2000);
    }

    @Test
    public void SuccessfulLoginBecauseUserExists() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.etUsername)).perform(ViewActions.typeText("User1"));
        onView(withId(R.id.etPassword)).perform(ViewActions.typeText("Password1"));

        onView(withId(R.id.btnLogin)).perform(click());

        onView(withId(R.id.main_activity)).check(matches(isDisplayed()));
//        //onView(withId(R.id.main_activity)).check(matches(isDisplayed()));
//        Thread.sleep(5000);
//
////      onView(withText("Login successfully."))
////                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
//        onView(withText("Login successfully."))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
////        Thread.sleep(2000);
////        onView(withId(com.google.android.material.R.id.snackbar_text))
////                .check(matches(withText("Login successfully.")))
////                .check(matches(isDisplayed()));
    }

    @Test
    public void UserDoesNotExist() {

        onView(withId(R.id.etUsername)).perform(ViewActions.typeText("UserDoesNotExist"));
        onView(withId(R.id.etPassword)).perform(ViewActions.typeText("Password3"));

        onView(withId(R.id.btnLogin)).perform(click());

        onView(withText("User not found."))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }

    @Test
    public void UserExistButPasswordIsWrong() {

        onView(withId(R.id.etUsername)).perform(ViewActions.typeText("User1"));
        onView(withId(R.id.etPassword)).perform(ViewActions.typeText("WrongPassword"));

        onView(withId(R.id.btnLogin)).perform(click());

        onView(withText("Incorrect password."))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));


    }

    @Test
    public void SignUpWithAUsernameAlreadyInUse() {

        onView(withId(R.id.button4)).perform(click());
        onView(withId(R.id.sign_up_id)).check(matches(isDisplayed()));

        onView(withId(R.id.sign_up_name_text)).perform(ViewActions.typeText("New name"));
        onView(withId(R.id.sign_up_email_text)).perform(ViewActions.typeText("email@gmail.com"));
        onView(withId(R.id.sign_up_phone_number_text)).perform(ViewActions.typeText("9876543210"));
        onView(withId(R.id.sign_up_done)).perform(click());

        onView(withId(R.id.sign_up_id_2)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_up_username_text)).perform(ViewActions.typeText("User1"));
        onView(withId(R.id.sign_up_password_text)).perform(ViewActions.typeText("New password"));

        onView(withText("Username name already taken!"))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));


    }

    @Test
    public void SignUpWithAnEmailAlreadyInUse() {

        onView(withId(R.id.button4)).perform(click());
        onView(withId(R.id.sign_up_id)).check(matches(isDisplayed()));

        onView(withId(R.id.sign_up_name_text)).perform(ViewActions.typeText("New name"));
        onView(withId(R.id.sign_up_email_text)).perform(ViewActions.typeText("user1@gmail.com"));
        onView(withId(R.id.sign_up_phone_number_text)).perform(ViewActions.typeText("7317555555"));
        onView(withId(R.id.sign_up_done)).perform(click());

        onView(withId(R.id.sign_up_id_2)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_up_username_text)).perform(ViewActions.typeText("New user"));
        onView(withId(R.id.sign_up_password_text)).perform(ViewActions.typeText("New password"));

        onView(withText("Registration failed."))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

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