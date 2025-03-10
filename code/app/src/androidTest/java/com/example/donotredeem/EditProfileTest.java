//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import androidx.fragment.app.testing.FragmentScenario;
//
//import com.example.donotredeem.Classes.Users;
//import com.example.donotredeem.Fragments.EditProfile;
//
//import org.junit.After;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.Objects;
//
//@After
//public void tearDown() {
//    String projectId = "login-register-de540";
//    URL url = null;
//    try {
//        url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
//    } catch (MalformedURLException exception) {
//        Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
//    }
//    HttpURLConnection urlConnection = null;
//    try {
//        urlConnection = (HttpURLConnection) url.openConnection();
//        urlConnection.setRequestMethod("DELETE");
//        int response = urlConnection.getResponseCode();
//        Log.i("Response Code", "Response Code: " + response);
//    } catch (IOException exception) {
//        Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
//    } finally {
//        if (urlConnection != null) {
//            urlConnection.disconnect();
//        }
//    }
//}
//}
//
//    @Test
//    public void testUserProfileFetchedAndDisplayed() {
//        Users mockUser = new Users("testUser", "password123", "test@example.com", "This is a bio");
//
//        FragmentScenario<EditProfile> scenario = FragmentScenario.launchInContainer(EditProfile.class);
//
//        scenario.onFragment(fragment -> {
//            fragment.setDetails(mockUser);
//
//            View view = fragment.getView();
//            assertNotNull(view); // Ensure the fragment view is created
//
//            EditText usernameField = view.findViewById(R.id.username_desc);
//            EditText emailField = view.findViewById(R.id.email_desc);
//            EditText bioField = view.findViewById(R.id.bio_desc);
//
//            assertNotNull(usernameField);
//            assertNotNull(emailField);
//            assertNotNull(bioField);
//
//            assertEquals("testUser", usernameField.getText().toString());
//            assertEquals("test@example.com", emailField.getText().toString());
//            assertEquals("This is a bio", bioField.getText().toString());
//        });
//    }
//
//    @Test
//    public void testUpdateUserProfile() {
//        Users mockUser = new Users("testUser", "password123", "test@example.com", "This is a bio");
//        FragmentScenario<EditProfile> scenario = FragmentScenario.launchInContainer(EditProfile.class);
//
//        scenario.onFragment(fragment -> {
//            fragment.setUserProfileManager(mockUserProfileManager); // Ensure mock is used
//            fragment.setDetails(mockUser);
//
//            View view = fragment.getView();
//            assertNotNull(view);
//
//            EditText usernameField = view.findViewById(R.id.username_desc);
//            EditText emailField = view.findViewById(R.id.email_desc);
//            EditText bioField = view.findViewById(R.id.bio_desc);
//            Button doneButton = view.findViewById(R.id.button2);
//
//            assertNotNull(usernameField);
//            assertNotNull(emailField);
//            assertNotNull(bioField);
//            assertNotNull(doneButton);
//
//            usernameField.setText("UpdatedUser");
//            emailField.setText("updated@example.com");
//            bioField.setText("Updated bio");
//
//            doneButton.performClick();
//
//            ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
//            verify(mockUserProfileManager).updateUser(userCaptor.capture(), eq("testUser"));
//
//            Users updatedUser = userCaptor.getValue();
//            assertEquals("UpdatedUser", updatedUser.getUsername());
//            assertEquals("updated@example.com", updatedUser.getEmail());
//            assertEquals("Updated bio", updatedUser.getBio());
//        });
//    }
//}