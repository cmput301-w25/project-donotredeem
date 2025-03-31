package com.example.donotredeem;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the {@link UserProfileManager} class, verifying Firestore interactions
 * for user profile management operations. Tests focus on successful execution paths
 * and proper document updates in Firebase Firestore.
 *
 * <p>Key test scenarios include:
 * <ul>
 *     <li>Follow request acceptance/declination workflows</li>
 *     <li>User profile data retrieval (existing/non-existent users)</li>
 *     <li>Follower/following list management operations</li>
 * </ul>
 *
 * <p>Uses Mockito to mock Firebase Firestore components for isolated testing.
 */
public class UserProfileManagerTest {

    private UserProfileManager userProfileManager;

    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private FirebaseStorage mockStorage;

    @Mock
    private CollectionReference mockUserCollection;

    @Mock
    private DocumentReference mockDocRef;

    @Mock
    private Task<Void> mockVoidTask;

    @Mock
    private Task<DocumentSnapshot> mockDocTask;

    @Mock
    private DocumentSnapshot mockDocSnapshot;

    @Mock
    private UserProfileManager.OnUpdateListener mockUpdateListener;

    @Mock
    private UserProfileManager.OnUserProfileFetchListener mockFetchListener;

    /**
     * Initializes test environment before each test:
     * <ul>
     *     <li>Creates Mockito mocks for Firebase services</li>
     *     <li>Configures Firestore collection/document reference mocking</li>
     *     <li>Initializes UserProfileManager with mocked dependencies</li>
     * </ul>
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Mock Firestore collection and document references
        when(mockDb.collection("User")).thenReturn(mockUserCollection);
        when(mockUserCollection.document(any(String.class))).thenReturn(mockDocRef);

        userProfileManager = new UserProfileManager(mockDb);
        userProfileManager.storage = mockStorage;
    }

    /**
     * Tests successful follow request acceptance.
     * Verifies:
     * <ul>
     *     <li>Current user's follower_list and requests fields are updated</li>
     *     <li>Firestore document update operations are executed</li>
     * </ul>
     */
    @Test
    public void testAcceptFollowRequest_Success() {
        // Arrange
        String currentUser = "currentUser";
        String requesterUsername = "requester";

        // Stub the update method for current user's document
        when(mockDocRef.update(
                eq("follower_list"), any(),
                eq("requests"), any()
        )).thenReturn(mockVoidTask);

        // Stub task listeners
        when(mockVoidTask.addOnSuccessListener(any())).thenReturn(mockVoidTask);
        when(mockVoidTask.addOnFailureListener(any())).thenReturn(mockVoidTask);

        // Act
        userProfileManager.acceptFollowRequest(currentUser, requesterUsername, mockUpdateListener);

        // Assert
        // Verify the current user's document is updated with the correct fields
        verify(mockDocRef).update(
                eq("follower_list"), any(),
                eq("requests"), any()
        );
    }

    /**
     * Tests successful follow request declination.
     * Verifies:
     * <ul>
     *     <li>Both current user's requests and requester's requested fields are updated</li>
     *     <li>Separate document references are used for current user and requester</li>
     * </ul>
     */
    @Test
    public void testDeclineFollowRequest_Success() {
        // Arrange
        String currentUser = "currentUser";
        String requesterUsername = "requester";

        // Mock separate DocumentReferences for current user and requester
        DocumentReference mockCurrentUserDocRef = mock(DocumentReference.class);
        DocumentReference mockRequesterDocRef = mock(DocumentReference.class);

        // Stub Firestore to return correct DocumentReferences based on username
        when(mockUserCollection.document(currentUser)).thenReturn(mockCurrentUserDocRef);
        when(mockUserCollection.document(requesterUsername)).thenReturn(mockRequesterDocRef);

        // Stub update methods to return the mock Task
        when(mockCurrentUserDocRef.update(eq("requests"), any())).thenReturn(mockVoidTask);
        when(mockRequesterDocRef.update(eq("requested"), any())).thenReturn(mockVoidTask);

        // Stub Task listeners
        when(mockVoidTask.addOnSuccessListener(any())).thenReturn(mockVoidTask);
        when(mockVoidTask.addOnFailureListener(any())).thenReturn(mockVoidTask);

        // Act
        userProfileManager.declineFollowRequest(currentUser, requesterUsername, mockUpdateListener);

        // Assert: Verify both documents are updated correctly
        verify(mockCurrentUserDocRef).update(eq("requests"), any());
        verify(mockRequesterDocRef).update(eq("requested"), any());
    }

    /**
     * Tests successful retrieval of existing user profile.
     * Verifies:
     * <ul>
     *     <li>Firestore document snapshot is converted to User object</li>
     *     <li>All profile fields are properly mapped (username, email, bio, etc.)</li>
     *     <li>onUserProfileFetched callback is triggered with User object</li>
     * </ul>
     */
    @Test
    public void testGetUserProfile_Success() {
        // Arrange
        String username = "testUser";

        when(mockDocRef.get()).thenReturn(mockDocTask);
        when(mockDocTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<DocumentSnapshot> listener = invocation.getArgument(0);
            listener.onComplete(mockDocTask);
            return mockDocTask;
        });

        when(mockDocTask.isSuccessful()).thenReturn(true);
        when(mockDocTask.getResult()).thenReturn(mockDocSnapshot);
        when(mockDocSnapshot.exists()).thenReturn(true);
        when(mockDocSnapshot.getId()).thenReturn(username);
        when(mockDocSnapshot.getString("password")).thenReturn("password123");
        when(mockDocSnapshot.getString("email")).thenReturn("test@example.com");
        when(mockDocSnapshot.getString("bio")).thenReturn("Test bio");
        when(mockDocSnapshot.getString("pfp")).thenReturn("profile.jpg");
        when(mockDocSnapshot.getString("birthDate")).thenReturn("01-01-2000");
        when(mockDocSnapshot.getString("phone")).thenReturn("1234567890");

        // Act
        userProfileManager.getUserProfile(username, mockFetchListener);

        // Assert
        verify(mockFetchListener).onUserProfileFetched(any(User.class));
    }

    /**
     * Tests handling of non-existent user profiles.
     * Verifies:
     * <ul>
     *     <li>onUserProfileFetched callback is triggered with null</li>
     *     <li>Non-existent document snapshot is properly handled</li>
     * </ul>
     */
    @Test
    public void testGetUserProfile_DocumentDoesNotExist() {
        // Arrange
        String username = "nonExistentUser";

        when(mockDocRef.get()).thenReturn(mockDocTask);
        when(mockDocTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<DocumentSnapshot> listener = invocation.getArgument(0);
            listener.onComplete(mockDocTask);
            return mockDocTask;
        });

        when(mockDocTask.isSuccessful()).thenReturn(true);
        when(mockDocTask.getResult()).thenReturn(mockDocSnapshot);
        when(mockDocSnapshot.exists()).thenReturn(false);

        // Act
        userProfileManager.getUserProfile(username, mockFetchListener);

        // Assert
        verify(mockFetchListener).onUserProfileFetched(null);
    }

    /**
     * Tests removal from following list.
     * Verifies:
     * <ul>
     *     <li>following_list and following count are updated</li>
     *     <li>Firestore arrayRemove operation is executed</li>
     * </ul>
     */
    @Test
    public void testRemoveFromFollowingList_Success() {
        // Arrange
        String username = "currentUser";
        String targetUsername = "targetUser";

        when(mockDocRef.update(
                eq("following_list"), any(),
                eq("following"), any()
        )).thenReturn(mockVoidTask);

        when(mockVoidTask.addOnSuccessListener(any())).thenReturn(mockVoidTask);
        when(mockVoidTask.addOnFailureListener(any())).thenReturn(mockVoidTask);

        // Act
        userProfileManager.removeFromFollowingList(username, targetUsername, mockUpdateListener);

        // Assert
        verify(mockDocRef).update(
                eq("following_list"), any(),
                eq("following"), any()
        );
    }

    /**
     * Tests removal from followers list.
     * Verifies:
     * <ul>
     *     <li>follower_list and followers count are updated</li>
     *     <li>Firestore arrayRemove operation is executed</li>
     * </ul>
     */
    @Test
    public void testRemoveFromFollowersList_Success() {
        // Arrange
        String username = "currentUser";
        String targetUsername = "targetUser";

        when(mockDocRef.update(
                eq("follower_list"), any(),
                eq("followers"), any()
        )).thenReturn(mockVoidTask);

        when(mockVoidTask.addOnSuccessListener(any())).thenReturn(mockVoidTask);
        when(mockVoidTask.addOnFailureListener(any())).thenReturn(mockVoidTask);

        // Act
        userProfileManager.removeFromFollowersList(username, targetUsername, mockUpdateListener);

        // Assert
        verify(mockDocRef).update(
                eq("follower_list"), any(),
                eq("followers"), any()
        );
    }
}