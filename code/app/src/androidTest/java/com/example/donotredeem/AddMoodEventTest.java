package com.example.donotredeem;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.mockito.Mockito.when;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.donotredeem.Fragments.AddMoodEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMoodEventTest {
    @Mock
    FirebaseAuth mockAuth;
    @Mock
    FirebaseUser mockUser;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);


    @BeforeClass
    public static void setup(){
        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Before
    public void mockLogin() {
        MockitoAnnotations.openMocks(this);

        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("mockUserId");

        FirebaseAuth mockFirebaseAuthInstance = Mockito.mock(FirebaseAuth.class);
        when(mockFirebaseAuthInstance.getCurrentUser()).thenReturn(mockUser);

    }

    public void navigateToAddMoodEventFragment() {
        onView(withId(R.id.add_button)).perform(click());
    }

    @Test
    public void testAddMoodEvent() {

        mockLogin();
        navigateToAddMoodEventFragment();

        FragmentScenario.launchInContainer(AddMoodEvent.class).onFragment(fragment -> {
            onView(withId(R.id.desc)).perform(click());
        });
    }


}
