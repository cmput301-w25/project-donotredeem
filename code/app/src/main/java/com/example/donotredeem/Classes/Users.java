package com.example.donotredeem.Classes;

import java.util.List;

/**
 * Users
 *      User class locally stores information about users
 */

public class Users {
    private String username;
    private String password;
    private String email;
    private int phoneNumber;
    private String name;
    private String birthdayDate; // yyyy-mm-dd
    private List<String> helpYouDown;
    private String frequency;
//    private Boolean remember;
    private String picture;
    private  List<String> Events;
    private Boolean isAdmin;
    private int followers;
    private List<String> followerList;
    private int following;
    private List<String> followingList;

    /**
     * Empty Users Constructors
     */
    public Users() {}

    /**
     * Users Constructor
     * @param username unique ID of the user
     * @param password passcode to their account
     * @param email email of the user
     * @param password passcode to their account
     * @param phoneNumber phone number to the User
     * @param name name to their account
     * @param birthdayDate the date the user was born
     * @param helpYouDown Array of things users does to make them feel happy when they are down
     * @param frequency how daily they want to add their moodevent
     * @param remember boolean to check if the device has to remember their account login details
     * @param picture hold the pictureID of the picture in the Picture class
     * @param Event hold the ID of each moodevent
     * @param isAdmin checks if the user is an admin or not
     * @param followers hold the interger of the followers
     * @param followerList hold the array of the followers, each element is the username of the user
     * @param following hold the interger of the following
     * @param followingList hold the array of the following, each element is the username of the user
     */
    public Users(String username, String password, String email, int phoneNumber, String name, String birthdayDate, List<String> helpYouDown, String frequency, Boolean remember, String picture, List<String> Event, Boolean isAdmin, int followers, int following, List<String> followerList, List<String> followingList){
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.birthdayDate = birthdayDate;
        this.helpYouDown = helpYouDown;
        this.frequency = frequency;
        this.remember = remember;
        this.picture = picture;
        this.Events = Event;
        this.isAdmin = isAdmin;
        this.followers = followers;
        this.following = following;
        this.followerList = followerList;
        this.followingList = followingList;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdayDate() {
        return birthdayDate;
    }

    public void setBirthdayDate(String birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public List<String> getHelpYouDown() {
        return helpYouDown;
    }

    public void setHelpYouDown(List<String> helpYouDown) {
        this.helpYouDown = helpYouDown;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Boolean getRemember() {
        return remember;
    }

    public void setRemember(Boolean remember) {
        this.remember = remember;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<String> getEvents() {
        return Events;
    }

    public void setEvents(List<String> events) {
        Events = events;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public List<String> getFollowerList() {
        return followerList;
    }

    public void setFollowerList(List<String> followerList) {
        this.followerList = followerList;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public List<String> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(List<String> followingList) {
        this.followingList = followingList;
    }
}
