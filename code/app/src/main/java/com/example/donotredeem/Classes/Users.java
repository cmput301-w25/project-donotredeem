package com.example.donotredeem.Classes;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

/**
 * Users
 *      User class locally stores information about users
 */

public class Users {
    private String username;
    private String password;
    private String email;
//    private String phoneNumber;
//    private String name;
//    private String birthdayDate; // yyyy-mm-dd
//    private List<String> helpYouDown;
//    private String frequency;
//    private Boolean remember;
//    private String picture;
    private  List<String> MoodREF;
//    private Boolean isAdmin;
    private List<String> followerList;
    private List<String> followingList;
    private List<String> requests;
    private List<DocumentReference> moodRefs;
    private String bio;
    private String profilePictureUrl;
    private int moods;

    /**
     * Empty Users Constructors
     */
    public Users() {}

//    /**
//     * Users Constructor
//     * @param username unique ID of the user
//     * @param password passcode to their account
//     * @param email email of the user
//     * @param password passcode to their account
//     * @param phoneNumber phone number to the User
//     * @param name name to their account
//     * @param birthdayDate the date the user was born
//     * @param helpYouDown Array of things users does to make them feel happy when they are down
//     * @param frequency how daily they want to add their moodevent
//     * @param picture hold the pictureID of the picture in the Picture class
//     * @param Event hold the ID of each moodevent
//     * @param isAdmin checks if the user is an admin or not
//     * @param followers hold the interger of the followers
//     * @param followerList hold the array of the followers, each element is the username of the user
//     * @param following hold the interger of the following
//     * @param followingList hold the array of the following, each element is the username of the user
//     */
//    public Users(String username, String password, String email, int phoneNumber, String name, String birthdayDate, List<String> helpYouDown, String frequency, String picture, List<String> Event, Boolean isAdmin, int followers, int following, List<String> followerList, List<String> followingList){
//        this.username = username;
//        this.password = password;
//        this.email = email;
//        this.phoneNumber = phoneNumber;
//        this.name = name;
//        this.birthdayDate = birthdayDate;
//        this.helpYouDown = helpYouDown;
//        this.frequency = frequency;
//        this.picture = picture;
//        this.Events = Event;
//        this.isAdmin = isAdmin;
//        this.followers = followers;
//        this.following = following;
//        this.followerList = followerList;
//        this.followingList = followingList;
//    }
//    public Users (String username, String password, String email, int followers, int following, List<String> followerList, List<String> followingList, List<String> requests){
//        this.username = username;
//        this.password = password;
//        this.email = email;
//        this.followerList = followerList;
//        this.followingList = followingList;
//        this.requests = requests;
//    }

    public Users(String username, String password, String email, String bio, String profilePictureUrl) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
    }

    public Users(String username, String bio, String profilePictureUrl, List<String> followerList,
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getBirthdayDate() {
//        return birthdayDate;
//    }
//
//    public void setBirthdayDate(String birthdayDate) {
//        this.birthdayDate = birthdayDate;
//    }
//
//    public List<String> getHelpYouDown() {
//        return helpYouDown;
//    }
//
//    public void setHelpYouDown(List<String> helpYouDown) {
//        this.helpYouDown = helpYouDown;
//    }
//
//    public String getFrequency() {
//        return frequency;
//    }
//
//    public void setFrequency(String frequency) {
//        this.frequency = frequency;
//    }
//
//    public String getPicture() {
//        return picture;
//    }
//
//    public void setPicture(String picture) {
//        this.picture = picture;
//    }
//
//
//    public Boolean getAdmin() {
//        return isAdmin;
//    }
//
//    public void setAdmin(Boolean admin) {
//        isAdmin = admin;
//    }
//
    public int getFollowers() {
        return followerList != null ? followerList.size() : 0;
    }

    public List<String> getFollowerList() {
        return followerList;
    }

    public void setFollowerList(List<String> followerList) {
        this.followerList = followerList;
    }

    public int getFollowing() {
        return followingList != null ? followingList.size() : 0;
    }

    public List<String> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(List<String> followingList) {
        this.followingList = followingList;
    }

    public List<String> getMoodREF() {
        return MoodREF;
    }

    public void setMoodREF(List<String> moodREF) {
        MoodREF = moodREF;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getRequests() {
        return requests;
    }

    public void setRequests(List<String> requests) {
        this.requests = requests;
    }

    public int getMoods(){return moods;}
}

