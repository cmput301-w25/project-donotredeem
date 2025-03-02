package com.example.donotredeem;

import android.graphics.Bitmap;
import android.location.Location;

import java.time.LocalDate;
import java.time.LocalTime;

public class MoodEvent {

    private LocalDate date;
    private LocalTime time;
    private String place;
    private double latitude;
    private double longitude;
    private String emotionalState;
    private String situation;
    private String trigger;
    private String explainText;
    private Bitmap explainPicture;


    // Constructor
    public MoodEvent(String emotionalState, LocalDate date, LocalTime time,
                     String place, Location location, String situation,
                     String trigger, String explainText, Bitmap explainPicture) {

        // Required Field
        if (emotionalState == null || emotionalState.trim().isEmpty()) {
            throw new IllegalArgumentException("Emotional state is required.");
        }
        this.emotionalState = emotionalState;

        // user date and time, if not given then system date and time
        this.date = (date != null) ? date : LocalDate.now();
        this.time = (time != null) ? time : LocalTime.now();

        // Defaults to user-entered place or empty string
        this.place = (place != null) ? place : "";

        // Get location if available
        if (location != null) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        } else {
            this.latitude = 0.0;
            this.longitude = 0.0;
        }

        // Optional fields, need to provide null values if not there
        this.situation = situation;
        this.trigger = trigger;
        this.explainText = explainText;
        this.explainPicture = explainPicture;
    }

    public MoodEvent(String emotionalState, LocalDate date, LocalTime time,
                     String place,
                     String trigger, String explainText) {

        // Required Field
        if (emotionalState == null || emotionalState.trim().isEmpty()) {
            throw new IllegalArgumentException("Emotional state is required.");
        }
        this.emotionalState = emotionalState;

        // user date and time, if not given then system date and time
        this.date = LocalDate.of(2024, 3, 2);
        this.time = LocalTime.of(12, 30);

        // Defaults to user-entered place or empty string
        this.place = (place != null) ? place : "";


        // Optional fields, need to provide null values if not there
        this.trigger = trigger;
        this.explainText = explainText;
    }


    // Getters
    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
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

    public Bitmap getExplainPicture() {
        return explainPicture;
    }

    // Setters
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public void setExplainPicture(Bitmap explainPicture) {
        this.explainPicture = explainPicture;
    }
}


