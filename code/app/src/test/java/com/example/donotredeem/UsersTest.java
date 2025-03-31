package com.example.donotredeem;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.donotredeem.Classes.User;

import java.util.ArrayList;
import java.util.List;

public class UsersTest {

    private User user;

    @Before
    public void setUp() {
        user = new User("testUser", "password123", "test@example.com", "01-01-2000", "1234567890", "Test bio", "http://example.com/profile.jpg");
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals("testUser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("01-01-2000", user.getBirthdayDate());
        assertEquals("1234567890", user.getContact());
        assertEquals("Test bio", user.getBio());
        assertEquals("http://example.com/profile.jpg", user.getProfilePictureUrl());
    }

    @Test
    public void testSetters() {
        user.setUsername("newUsername");
        user.setPassword("newPassword");
        user.setEmail("new@example.com");
        user.setBirthdayDate("02-02-2002");
        user.setContact("9876543210");
        user.setBio("New bio");
        user.setProfilePictureUrl("http://example.com/new_profile.jpg");

        assertEquals("newUsername", user.getUsername());
        assertEquals("newPassword", user.getPassword());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("02-02-2002", user.getBirthdayDate());
        assertEquals("9876543210", user.getContact());
        assertEquals("New bio", user.getBio());
        assertEquals("http://example.com/new_profile.jpg", user.getProfilePictureUrl());
    }

    @Test
    public void testFollowersAndFollowing() {
        List<String> followerList = new ArrayList<>();
        followerList.add("follower1");
        followerList.add("follower2");
        user.setFollowerList(followerList);

        List<String> followingList = new ArrayList<>();
        followingList.add("following1");
        followingList.add("following2");
        followingList.add("following3");
        user.setFollowingList(followingList);

        assertEquals(2, user.getFollowers());
        assertEquals(3, user.getFollowing());
        assertEquals(followerList, user.getFollowerList());
        assertEquals(followingList, user.getFollowingList());
    }

    @Test
    public void testRequests() {
        List<String> requests = new ArrayList<>();
        requests.add("request1");
        requests.add("request2");
        user.setRequests(requests);

        assertEquals(requests, user.getRequests());
    }

    @Test
    public void testMoodREF() {
        List<String> moodREF = new ArrayList<>();
        moodREF.add("mood1");
        moodREF.add("mood2");
        user.setMoodREF(moodREF);

        assertEquals(moodREF, user.getMoodREF());
    }

    @Test
    public void testEmptyConstructor() {
        User emptyUser = new User();
        assertNotNull(emptyUser);
    }

    @Test
    public void testAlternativeConstructor() {
        User altUser = new User("altUser", "altPassword", "alt@example.com", "Alt bio");
        assertEquals("altUser", altUser.getUsername());
        assertEquals("altPassword", altUser.getPassword());
        assertEquals("alt@example.com", altUser.getEmail());
        assertEquals("Alt bio", altUser.getBio());
    }
}
