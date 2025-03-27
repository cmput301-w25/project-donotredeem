//package com.example.donotredeem;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static androidx.test.espresso.action.ViewActions.typeText;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//
//import android.util.Log;
//
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.filters.LargeTest;
//import androidx.test.platform.app.InstrumentationRegistry;
//
//import com.example.donotredeem.Classes.Users;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.Objects;
//
//
//@RunWith(AndroidJUnit4.class)
//@LargeTest
//public class AuthenticationTest {
//    @Rule
//    public ActivityScenarioRule<LogIn> scenario = new ActivityScenarioRule<>(LogIn.class);
//
//
//    @BeforeClass
//    public static void setup() {
//        String androidLocalhost = "10.0.2.2";
//        int portNumber = 8080;
//        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
//
//    }
//    @BeforeClass
//    public static void disableAnimations() throws IOException {
//        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
//                "settings put global window_animation_scale 0"
//        );
//        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
//                "settings put global transition_animation_scale 0"
//        );
//        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
//                "settings put global animator_duration_scale 0"
//        );
//    }
//
//    @Before
//    public void seedDatabase() throws InterruptedException {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference usersRef = db.collection("User");
//        Users[] user_data = {
//                new Users("User1", "Password1", "user1@gmail.com", "This is my bio."),
//                new Users("User2", "Password2", "user2@gmail.com", "This is not my bio")
//        };
//        for (Users users : user_data) {
//            usersRef.document(users.getUsername()).set(users);
//        }
//        Thread.sleep(2000);
//    }
//
//    @After
//    public void tearDown() {
////        Espresso.unregisterIdlingResources(LogIn.firebaseIdlingResource);
//        String projectId = "login-register-de540";
//        URL url = null;
//        try {
//            url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
//        } catch (MalformedURLException exception) {
//            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
//        }
//        HttpURLConnection urlConnection = null;
//        try {
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("DELETE");
//            int response = urlConnection.getResponseCode();
//            Log.i("Response Code", "Response Code: " + response);
//        } catch (IOException exception) {
//            Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//        }}
//    @Test
//    public void UserExistButPasswordIsWrong() throws InterruptedException{
//        Thread.sleep(5000);
//        onView(withId(R.id.etUsername)).perform(typeText("User1"), closeSoftKeyboard());
//        onView(withId(R.id.etPassword)).perform(typeText("WrongPassword"), closeSoftKeyboard());
//
//        onView(withId(R.id.btnLogin)).perform(click());
//        Thread.sleep(500);
////        onView(withText("Incorrect password."))
////                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
//        onView(withId(com.google.android.material.R.id.snackbar_text))
//                .check(matches(withText("Incorrect password.")))
//                .check(matches(isDisplayed()));
//        Thread.sleep(2000);
//    }
//}
