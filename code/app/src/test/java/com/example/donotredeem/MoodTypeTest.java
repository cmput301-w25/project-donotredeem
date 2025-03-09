package com.example.donotredeem;

import static org.junit.Assert.*;

import com.example.donotredeem.MoodType;

import org.junit.Test;

public class MoodTypeTest {

    /**
     * Test if each MoodType returns the correct mood name.
     */
    @Test
    public void testGetMood() {
        assertEquals("Happy", MoodType.Happy.getMood());
        assertEquals("Sad", MoodType.Sad.getMood());
        assertEquals("Fear", MoodType.Fear.getMood());
        assertEquals("Angry", MoodType.Angry.getMood());
        assertEquals("Confused", MoodType.Confused.getMood());
        assertEquals("Disgusted", MoodType.Disgusted.getMood());
        assertEquals("Shameful", MoodType.Shameful.getMood());
        assertEquals("Surprised", MoodType.Surprised.getMood());
        assertEquals("Shy", MoodType.Shy.getMood());
        assertEquals("Tired", MoodType.Tired.getMood());
    }

    /**
     * Test if each MoodType returns the correct drawable image resource ID.
     */
    @Test
    public void testGetImgId() {
        assertEquals(R.drawable.hapi, MoodType.Happy.getImg_id());
        assertEquals(R.drawable.sad, MoodType.Sad.getImg_id());
        assertEquals(R.drawable.fear, MoodType.Fear.getImg_id());
        assertEquals(R.drawable.angry, MoodType.Angry.getImg_id());
        assertEquals(R.drawable.confused, MoodType.Confused.getImg_id());
        assertEquals(R.drawable.disgust, MoodType.Disgusted.getImg_id());
        assertEquals(R.drawable.shame, MoodType.Shameful.getImg_id());
        assertEquals(R.drawable.surprised, MoodType.Surprised.getImg_id());
        assertEquals(R.drawable.shy, MoodType.Shy.getImg_id());
        assertEquals(R.drawable.tired, MoodType.Tired.getImg_id());
    }

    /**
     * Test if getImageIdByMood() returns the correct drawable ID for a given mood name.
     */
    @Test
    public void testGetImageIdByMood() {
        assertEquals(R.drawable.hapi, MoodType.getImageIdByMood("Happy"));
        assertEquals(R.drawable.sad, MoodType.getImageIdByMood("Sad"));
        assertEquals(R.drawable.fear, MoodType.getImageIdByMood("Fear"));
        assertEquals(R.drawable.angry, MoodType.getImageIdByMood("Angry"));
        assertEquals(R.drawable.confused, MoodType.getImageIdByMood("Confused"));
        assertEquals(R.drawable.disgust, MoodType.getImageIdByMood("Disgusted"));
        assertEquals(R.drawable.shame, MoodType.getImageIdByMood("Shameful"));
        assertEquals(R.drawable.surprised, MoodType.getImageIdByMood("Surprised"));
        assertEquals(R.drawable.shy, MoodType.getImageIdByMood("Shy"));
        assertEquals(R.drawable.tired, MoodType.getImageIdByMood("Tired"));
    }

    /**
     * Test if getImageIdByMood() handles case-insensitive input correctly.
     */
    @Test
    public void testGetImageIdByMood_CaseInsensitive() {
        assertEquals(R.drawable.hapi, MoodType.getImageIdByMood("happy"));
        assertEquals(R.drawable.sad, MoodType.getImageIdByMood("SAD"));
        assertEquals(R.drawable.fear, MoodType.getImageIdByMood("fEaR"));
    }

    /**
     * Test if getImageIdByMood() returns -1 for invalid moods.
     */
    @Test
    public void testGetImageIdByMood_Invalid() {
        assertEquals(-1, MoodType.getImageIdByMood("Excited")); // Not in enum
        assertEquals(-1, MoodType.getImageIdByMood(""));        // Empty string
        assertEquals(-1, MoodType.getImageIdByMood(null));      // Null input
    }
}
