package com.example.donotredeem;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;

import androidx.annotation.Nullable;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;

public class MoodEvent {

    private String date;
    private String time;
    private String place;
    private String emotionalState;
    private String situation;
    private String trigger;
    private String explainText;
    private String explainPicture;
    private String moodEventId;


//    // Constructor
//    public MoodEvent(String emotionalState, LocalDate date, LocalTime time,
//                     String place, Location location, String situation,
//                     String trigger, String explainText, String explainPicture) {
//
//        // Required Field
//        if (emotionalState == null || emotionalState.trim().isEmpty()) {
//            throw new IllegalArgumentException("Emotional state is required.");
//        }
//        this.emotionalState = emotionalState;
//
//        // user date and time, if not given then system date and time
//        this.date = (date != null) ? date : LocalDate.now();
//        this.time = (time != null) ? time : LocalTime.now();
//
//        // Defaults to user-entered place or empty string
//        this.place = (place != null) ? place : "";
//
//        // Get location if available
//        if (location != null) {
//            this.latitude = location.getLatitude();
//            this.longitude = location.getLongitude();
//        } else {
//            this.latitude = 0.0;
//            this.longitude = 0.0;
//        }
//
//        // Optional fields, need to provide null values if not there
//        this.situation = situation;
//        this.trigger = trigger;
//        this.explainText = explainText;
//        this.explainPicture = explainPicture;
//    }
//
//    public MoodEvent(String emotionalState, LocalDate date, LocalTime time,
//                     String place,
//                     String trigger, String explainText) {
//
//        // Required Field
//        if (emotionalState == null || emotionalState.trim().isEmpty()) {
//            throw new IllegalArgumentException("Emotional state is required.");
//        }
//        this.emotionalState = emotionalState;
//
//        // user date and time, if not given then system date and time
//        this.date = LocalDate.of(2024, 3, 2);
//        this.time = LocalTime.of(12, 30);
//
//        // Defaults to user-entered place or empty string
//        this.place = (place != null) ? place : "";
//
//
//        // Optional fields, need to provide null values if not there
//        this.trigger = trigger;
//        this.explainText = explainText;
//    }

    //    heer testing
    public MoodEvent(String emotionalState, String date, String time,
                     String place, @Nullable String situation,
                     @Nullable String trigger, @Nullable String explainText, @Nullable String explainPicture) {

        // Required Field
        if (emotionalState == null || emotionalState.trim().isEmpty()) {
            throw new IllegalArgumentException("Emotional state is required.");
        }
        this.emotionalState = emotionalState;
        this.time = time;
        this.date = date;

        // Defaults to user-entered place or empty string
        this.place = (place != null) ? place : "";

        // Optional fields, need to provide null values if not there
        this.situation = situation;
        this.trigger = trigger;
        this.explainText = explainText;
        this.explainPicture = explainPicture;
    }

    public MoodEvent(String moodEventId, String emotionalState, String date, String time,
                     String place, @Nullable String situation,
                     @Nullable String trigger, @Nullable String explainText, @Nullable String explainPicture) {
        if (emotionalState == null || emotionalState.trim().isEmpty()) {
            throw new IllegalArgumentException("Emotional state is required.");
        }
        this.moodEventId = moodEventId;
        this.emotionalState = emotionalState;
        this.time = time;
        this.date = date;
        this.place = (place != null) ? place : "";
        this.situation = situation;
        this.trigger = trigger;
        this.explainText = explainText;
        this.explainPicture = explainPicture;
    }

    public MoodEvent(){};

//    public MoodEvent(String emotionalState, LocalDate date, LocalTime time,
//                     String place, String situation,
//                     String trigger, String explainText) {
//
//        // Required Field
//        if (emotionalState == null || emotionalState.trim().isEmpty()) {
//            throw new IllegalArgumentException("Emotional state is required.");
//        }
//        this.emotionalState = emotionalState;
//        this.time = (time != null) ? time : LocalTime.now();
//        this.date = (date != null) ? date : LocalDate.now();
//
//        // Defaults to user-entered place or empty string
//        this.place = (place != null) ? place : "";
//
//        this.situation = situation;
//        // Optional fields, need to provide null values if not there
//        this.trigger = trigger;
//        this.explainText = explainText;
//    }
//
//    public MoodEvent(String emotionalState, LocalDate date, LocalTime time,
//                     String place,
//                     String trigger) {
//
//        // Required Field
//        if (emotionalState == null || emotionalState.trim().isEmpty()) {
//            throw new IllegalArgumentException("Emotional state is required.");
//        }
//        this.emotionalState = emotionalState;
//
//        // user date and time, if not given then system date and time
//        this.date = LocalDate.of(2024, 3, 2);
//        this.time = LocalTime.of(12, 30);
//
//        // Defaults to user-entered place or empty string
//        this.place = (place != null) ? place : "";
//
//
//        // Optional fields, need to provide null values if not there
//        this.trigger = trigger;
//    }





    // Getters
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }


    public String getEmotionalState() {
        return emotionalState;
    }

    public String getSituation() {
        return situation;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getExplainText() {
        return explainText;
    }

    public String getExplainPicture() {
        return explainPicture;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    public void setEmotionalState(String emotionalState) {
        this.emotionalState = emotionalState;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public void setExplainText(String explainText) {
        this.explainText = explainText;
    }

    public void setExplainPicture(String explainPicture) {
        this.explainPicture = explainPicture;
    }
    public String getMoodEventId() {
        return moodEventId;
    }

    public void setMoodEventId(String moodEventId) {
        this.moodEventId = moodEventId;
    }
}