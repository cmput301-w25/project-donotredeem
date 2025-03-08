package com.example.donotredeem;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;

import androidx.annotation.Nullable;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
/**
 * Represents a mood event with details such as emotional state, time, place, and additional information.
 */
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

    /**
     * Constructs a MoodEvent with all possible fields.
     *
     * @param emotionalState The emotional state of the user.
     * @param date The date of the mood event.
     * @param time The time of the mood event.
     * @param place The place where the mood event occurred.
     * @param situation The social situation during the event (optional).
     * @param trigger The trigger that caused the mood event (optional).
     * @param explainText Additional explanation text (optional).
     * @param explainPicture A picture URI representing the event (optional).
     */
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

    /**
     * Constructs a MoodEvent with all possible fields.
     *
     * @param moodEventId The unique ID of the mood event.
     * @param emotionalState The emotional state of the user.
     * @param date The date of the mood event.
     * @param time The time of the mood event.
     * @param place The place where the mood event occurred.
     * @param situation The social situation during the event (optional).
     * @param trigger The trigger that caused the mood event (optional).
     * @param explainText Additional explanation text (optional).
     * @param explainPicture A picture URI representing the event (optional).
     */
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
    /**
     * Default constructor required for Firestore deserialization.
     */
    public MoodEvent(){};

    // Getters
    /**
     * @return The date of the mood event.
     */
    public String getDate() {
        return date;
    }
    /**
     * @return The time of the mood event.
     */
    public String getTime() {
        return time;
    }
    /**
     * @return The place where the mood event occurred.
     */
    public String getPlace() {
        return place;
    }

    /**
     * @return The emotional state of the user.
     */
    public String getEmotionalState() {
        return emotionalState;
    }
    /**
     * @return The social situation during the mood event.
     */
    public String getSituation() {
        return situation;
    }
    /**
     * @return The trigger that caused the mood event.
     */
    public String getTrigger() {
        return trigger;
    }
    /**
     * @return Additional text explaining the mood event.
     */
    public String getExplainText() {
        return explainText;
    }
    /**
     * @return A URI string representing an image related to the mood event.
     */
    public String getExplainPicture() {
        return explainPicture;
    }
    /**
     * @return The unique ID of the mood event.
     */
    public String getMoodEventId() {
        return moodEventId;
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


    public void setMoodEventId(String moodEventId) {
        this.moodEventId = moodEventId;
    }
}