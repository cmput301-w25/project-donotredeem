package com.example.donotredeem.Classes;

import com.google.firebase.firestore.DocumentReference;
import java.util.List;


/**
 * Represents a user profile with information such as username, password, email, contact details, bio,
 * profile picture, and social connections (followers, following, requests, mood references, etc.).
 */
public class User {

    // User profile fields
    private String username;
    private String password;
    private String email;
    private String contact;
    private String birthdayDate; // dd-mm-yyyy
    private  List<String> MoodREF;
    private List<String> followerList;
    private List<String> followingList;
    private List<String> requests;
    private List<DocumentReference> moodRefs;
    private String bio;
    private String profilePictureUrl;
    private int moods;

    /**
     * Default no-argument constructor.
     */
    public User() {}

    /**
     * Constructor to initialize a user's basic information.
     *
     * @param username          The username of the user.
     * @param password          The password of the user.
     * @param email             The email address of the user.
     * @param dob               The user's date of birth.
     * @param contact           The contact information of the user.
     * @param bio               The biography or description of the user.
     * @param profilePictureUrl The URL of the user's profile picture.
     */
    public User(String username, String password, String email, String dob, String contact, String bio, String profilePictureUrl) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthdayDate = dob;
        this.contact = contact;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
    }

    /**
     * Constructor to initialize a user's basic info with bio.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param email    The email address of the user.
     * @param bio      The biography of the user.
     */
    public User(String username, String password, String email, String bio) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.bio = bio;
    }

    /**
     * Constructor to initialize a user with followers, following, requests, mood references, and profile picture.
     *
     * @param username          The username of the user.
     * @param bio               The biography of the user.
     * @param profilePictureUrl The URL of the user's profile picture.
     * @param followerList      The list of followers.
     * @param followingList     The list of users being followed.
     * @param requests          The list of follow requests.
     * @param moodRefs          List of document references for mood events.
     * @param moods             The count of moods associated with the user.
     */
    public User(String username, String bio, String profilePictureUrl, List<String> followerList,
                List<String> followingList, List<String> requests, List<DocumentReference> moodRefs, int moods) {
        this.username = username;
        this.bio = bio;
        this.followerList = followerList;
        this.followingList = followingList;
        this.requests = requests;
        this.moodRefs = moodRefs;
        this.profilePictureUrl = profilePictureUrl;
        this.moods = moods;
    }

    //Get Methods
    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public String getEmail() {
        return email;
    }


    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }


    public String getBirthdayDate() {
        return birthdayDate;
    }


    public int getFollowers() {
        return followerList != null ? followerList.size() : 0;
    }


    public List<String> getFollowerList() {
        return followerList;
    }


    public int getFollowing() {
        return followingList != null ? followingList.size() : 0;
    }

    public List<String> getFollowingList() {
        return followingList;
    }

    public List<String> getMoodREF() {
        return MoodREF;
    }

    public String getBio() {
        return bio;
    }

    public List<String> getRequests() {
        return requests;
    }

    public int getMoods(){return moods;}

    public String getContact() {
        return contact;
    }




    //Set Methods
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthdayDate(String birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public void setFollowerList(List<String> followerList) {
        this.followerList = followerList;
    }

    public void setFollowingList(List<String> followingList) {
        this.followingList = followingList;
    }

    public void setMoodREF(List<String> moodREF) {
        MoodREF = moodREF;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setRequests(List<String> requests) {
        this.requests = requests;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}

