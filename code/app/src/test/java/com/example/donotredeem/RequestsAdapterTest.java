package com.example.donotredeem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.donotredeem.Classes.UserProfileManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

@RunWith(MockitoJUnitRunner.class)
public class RequestsAdapterTest {

    @Mock
    private Context mockContext;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private UserProfileManager mockUserProfileManager;

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private FirebaseApp mockFirebaseApp;

    @Mock
    private FirebaseStorage mockFirebaseStorage;

    @Mock
    private FirebaseOptions mockFirebaseOptions;

    @Mock
    private LayoutInflater mockLayoutInflater;

    @Mock
    private View mockConvertView;

    @Mock
    private ViewGroup mockParent;

    @Mock
    private TextView mockUsernameTextView;

    @Mock
    private Button mockAcceptButton;

    @Mock
    private Button mockDeclineButton;

    @Mock
    private CircleImageView mockProfilePic;

    @Captor
    private ArgumentCaptor<UserProfileManager.OnUpdateListener> listenerCaptor;

    private RequestAdapter requestAdapter;
    private List<String> testRequests;

    @Before
    public void setUp() throws Exception {
        try (MockedStatic<FirebaseApp> firebaseAppMockedStatic = Mockito.mockStatic(FirebaseApp.class);
             MockedStatic<FirebaseFirestore> firebaseFirestoreMockedStatic = Mockito.mockStatic(FirebaseFirestore.class);
             MockedStatic<FirebaseStorage> firebaseStorageMockedStatic = Mockito.mockStatic(FirebaseStorage.class)) {

            firebaseAppMockedStatic.when(FirebaseApp::getInstance).thenReturn(mockFirebaseApp);
//            when(mockFirebaseApp.getOptions()).thenReturn(mockFirebaseOptions);
//            when(mockFirebaseOptions.getStorageBucket()).thenReturn("test-bucket");

            firebaseFirestoreMockedStatic.when(FirebaseFirestore::getInstance).thenReturn(mockFirestore);
            firebaseStorageMockedStatic.when(FirebaseStorage::getInstance).thenReturn(mockFirebaseStorage);

            testRequests = new ArrayList<>();
            testRequests.add("testUser1");
            testRequests.add("testUser2");

            lenient().when(mockContext.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE))
                    .thenReturn(mockSharedPreferences);
            lenient().when(mockSharedPreferences.getString("username", ""))
                    .thenReturn("currentUser");

            requestAdapter = new RequestAdapter(mockContext, testRequests) {
                @Override
                public void notifyDataSetChanged() {}

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    when(convertView.findViewById(R.id.username)).thenReturn(mockUsernameTextView);
                    when(convertView.findViewById(R.id.acceptButton)).thenReturn(mockAcceptButton);
                    when(convertView.findViewById(R.id.declineButton)).thenReturn(mockDeclineButton);
                    when(convertView.findViewById(R.id.pfp)).thenReturn(mockProfilePic);
                    return super.getView(position, convertView, parent);
                }
            };

            injectMockDependencies();
        }
    }

    private void injectMockDependencies() throws Exception {
        Field userProfileManagerField = RequestAdapter.class.getDeclaredField("userProfileManager");
        userProfileManagerField.setAccessible(true);
        userProfileManagerField.set(requestAdapter, mockUserProfileManager);

        Field dbField = RequestAdapter.class.getDeclaredField("db");
        dbField.setAccessible(true);
        dbField.set(requestAdapter, mockFirestore);
    }

    @Test
    public void testHandleAccept() throws Exception {
        doAnswer((Answer<Void>) invocation -> {
            UserProfileManager.OnUpdateListener listener =
                    (UserProfileManager.OnUpdateListener) invocation.getArguments()[2];
            listener.onSuccess();
            return null;
        }).when(mockUserProfileManager).acceptFollowRequest(
                anyString(),
                anyString(),
                any(UserProfileManager.OnUpdateListener.class)
        );

        Method handleAcceptMethod = RequestAdapter.class.getDeclaredMethod(
                "handleAccept", String.class, int.class);
        handleAcceptMethod.setAccessible(true);

        handleAcceptMethod.invoke(requestAdapter, "testUser1", 0);

        verify(mockUserProfileManager).acceptFollowRequest(
                anyString(),
                anyString(),
                any(UserProfileManager.OnUpdateListener.class)
        );
    }

    @Test
    public void testHandleDecline() throws Exception {
        doAnswer((Answer<Void>) invocation -> {
            UserProfileManager.OnUpdateListener listener =
                    (UserProfileManager.OnUpdateListener) invocation.getArguments()[2];
            listener.onSuccess();
            return null;
        }).when(mockUserProfileManager).declineFollowRequest(
                anyString(),
                anyString(),
                any(UserProfileManager.OnUpdateListener.class)
        );

        Method handleDeclineMethod = RequestAdapter.class.getDeclaredMethod(
                "handleDecline", String.class, int.class);
        handleDeclineMethod.setAccessible(true);

        handleDeclineMethod.invoke(requestAdapter, "testUser1", 0);

        verify(mockUserProfileManager).declineFollowRequest(
                anyString(),
                anyString(),
                any(UserProfileManager.OnUpdateListener.class)
        );
    }
}
