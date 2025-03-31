package com.example.donotredeem;

import static org.junit.Assert.*;

import com.example.donotredeem.Classes.MoodEvent;
import com.example.donotredeem.Fragments.ProfilePageFragment;

import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Unit tests for the {@link ProfilePageFragment} fragment's date/time parsing and mood event sorting functionality.
 * Verifies correct handling of various input formats and proper chronological ordering of events.
 */
public class ProfilePageFragmentTest {

    private ProfilePageFragment profilePageFragment;
    private ArrayList<MoodEvent> testEvents;

    /**
     * Initializes test environment before each test method:
     * <ul>
     *     <li>Creates new {@link ProfilePageFragment} instance</li>
     *     <li>Initializes mood history list via reflection</li>
     * </ul>
     * @throws Exception if reflection access fails
     */
    @Before
    public void setup() throws Exception {
        profilePageFragment = new ProfilePageFragment();
        testEvents = new ArrayList<>();

        // Initialize moodHistoryList using reflection
        Field moodHistoryField = ProfilePageFragment.class.getDeclaredField("moodHistoryList");
        moodHistoryField.setAccessible(true);
        moodHistoryField.set(profilePageFragment, new ArrayList<>());
    }

    /**
     * Tests valid date parsing (DD/MM/YYYY format)
     * Verifies conversion to correct {@link LocalDate} values
     */
    @Test
    public void parseDate_validFormat_returnsCorrectDate() {
        LocalDate result = profilePageFragment.parseStringToDate("31/12/2023");
        assertEquals(LocalDate.of(2023, 12, 31), result);
    }

    /**
     * Tests invalid date handling (32nd day/13th month)
     * Verifies returns {@link LocalDate#MIN} for invalid dates
     */
    @Test
    public void parseDate_invalidDate_returnsMinDate() {
        LocalDate result = profilePageFragment.parseStringToDate("32/13/2020");
        assertEquals(LocalDate.MIN, result);
    }

    /**
     * Tests non-date string input handling
     * Verifies returns {@link LocalDate#MIN} for malformed input
     */
    @Test
    public void parseDate_malformedInput_returnsMinDate() {
        LocalDate result = profilePageFragment.parseStringToDate("not-a-date");
        assertEquals(LocalDate.MIN, result);
    }

    /**
     * Tests time parsing without seconds
     * Verifies correct conversion of "HH:mm" format
     */
    @Test
    public void parseTime_validWithoutSeconds_returnsCorrectTime() {
        LocalTime result = profilePageFragment.parseStringToTime("23:59");
        assertEquals(LocalTime.of(23, 59), result);
    }

    /**
     * Tests time parsing with seconds
     * Verifies correct conversion of "HH:mm:ss" format
     */
    @Test
    public void parseTime_validWithSeconds_returnsCorrectTime() {
        LocalTime result = profilePageFragment.parseStringToTime("12:34:56");
        assertEquals(LocalTime.of(12, 34, 56), result);
    }

    /**
     * Tests invalid time handling (24:00)
     * Verifies returns {@link LocalTime#MIN} for invalid times
     */
    @Test
    public void parseTime_invalidTime_returnsMinTime() {
        LocalTime result = profilePageFragment.parseStringToTime("24:00");
        assertEquals(LocalTime.MIN, result);
    }

    /**
     * Tests chronological sorting of mood events
     * Verifies ordering: descending dates -> descending times
     * @throws Exception if reflection access fails
     */
    @Test
    public void sortMoodEvents_sortsByDateDescThenTimeDesc() throws Exception {
        // Create test events
        addTestEvent("02/01/2024", "09:00");
        addTestEvent("01/01/2024", "10:00");
        addTestEvent("02/01/2024", "10:00");

        profilePageFragment.sortMoodEvents();

        ArrayList<MoodEvent> sortedList = getMoodHistoryList();

        // Verify order
        assertEquals("02/01/2024", sortedList.get(0).getDate());
        assertEquals("10:00", sortedList.get(0).getTime());

        assertEquals("02/01/2024", sortedList.get(1).getDate());
        assertEquals("09:00", sortedList.get(1).getTime());

        assertEquals("01/01/2024", sortedList.get(2).getDate());
    }

    /**
     * Adds test event with specified date/time to mood history
     * @param date Test date in DD/MM/YYYY format
     * @param time Test time in HH:mm or HH:mm:ss format
     * @throws Exception if reflection access fails
     */
    private void addTestEvent(String date, String time) throws Exception {
        MoodEvent event = new MoodEvent() {
            @Override public String getDate() { return date; }
            @Override public String getTime() { return time; }
            @Override public Boolean getPrivacy() { return false; }
            public String getMoodType() { return "Happy"; }
        };

        getMoodHistoryList().add(event);
    }

    /**
     * Retrieves current mood history list via reflection
     * @return ArrayList of mood events from ProfilePage
     * @throws Exception if reflection access fails
     */
    @SuppressWarnings("unchecked")
    private ArrayList<MoodEvent> getMoodHistoryList() throws Exception {
        Field field = ProfilePageFragment.class.getDeclaredField("moodHistoryList");
        field.setAccessible(true);
        return (ArrayList<MoodEvent>) field.get(profilePageFragment);
    }
}