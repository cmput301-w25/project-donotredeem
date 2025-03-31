package com.example.donotredeem;

import static org.junit.Assert.*;

import com.example.donotredeem.Fragments.SearchedUser;

import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class SearchedUserTest {

    private SearchedUser searchedUser;
    private ArrayList<MoodEvent> testEvents;

    // Mock MoodEvent implementation for testing
    private abstract static class TestMoodEvent extends MoodEvent {
        @Override public Boolean getPrivacy() { return false; }
        public String getMoodType() { return "Happy"; }
    }

    @Before
    public void setup() throws Exception {
        searchedUser = new SearchedUser();
        testEvents = new ArrayList<>();

        // Initialize moodHistoryList using reflection
        Field moodHistoryField = SearchedUser.class.getDeclaredField("moodHistoryList");
        moodHistoryField.setAccessible(true);
        moodHistoryField.set(searchedUser, new ArrayList<>());
    }

    // Date Parsing Tests ================================================
    @Test
    public void parseDate_validFormat_returnsCorrectDate() {
        LocalDate result = searchedUser.parseStringToDate("31/12/2023");
        assertEquals(LocalDate.of(2023, 12, 31), result);
    }

    @Test
    public void parseDate_invalidDate_returnsMinDate() {
        LocalDate result = searchedUser.parseStringToDate("32/13/2020");
        assertEquals(LocalDate.MIN, result);
    }

    @Test
    public void parseDate_malformedInput_returnsMinDate() {
        LocalDate result = searchedUser.parseStringToDate("not-a-date");
        assertEquals(LocalDate.MIN, result);
    }

    // Time Parsing Tests ================================================
    @Test
    public void parseTime_validWithoutSeconds_returnsCorrectTime() {
        LocalTime result = searchedUser.parseStringToTime("23:59");
        assertEquals(LocalTime.of(23, 59), result);
    }

    @Test
    public void parseTime_validWithSeconds_returnsCorrectTime() {
        LocalTime result = searchedUser.parseStringToTime("12:34:56");
        assertEquals(LocalTime.of(12, 34, 56), result);
    }

    @Test
    public void parseTime_invalidTime_returnsMinTime() {
        LocalTime result = searchedUser.parseStringToTime("24:00");
        assertEquals(LocalTime.MIN, result);
    }

    // Sorting Tests =====================================================
    @Test
    public void sortMoodEvents_sortsByDateDescThenTimeDesc() throws Exception {
        // Create test events
        addTestEvent("02/01/2024", "10:00");
        addTestEvent("01/01/2024", "12:00");
        addTestEvent("02/01/2024", "09:00");

        searchedUser.sortMoodEvents();

        ArrayList<MoodEvent> sortedList = getMoodHistoryList();

        // Verify order
        assertEquals("02/01/2024", sortedList.get(0).getDate());
        assertEquals("10:00", sortedList.get(0).getTime());

        assertEquals("02/01/2024", sortedList.get(1).getDate());
        assertEquals("09:00", sortedList.get(1).getTime());

        assertEquals("01/01/2024", sortedList.get(2).getDate());
    }

    // Helper methods ====================================================
    private void addTestEvent(String date, String time) throws Exception {
        MoodEvent event = new TestMoodEvent() {
            @Override public String getDate() { return date; }
            @Override public String getTime() { return time; }
        };
        getMoodHistoryList().add(event);
    }

    @SuppressWarnings("unchecked")
    private ArrayList<MoodEvent> getMoodHistoryList() throws Exception {
        Field field = SearchedUser.class.getDeclaredField("moodHistoryList");
        field.setAccessible(true);
        return (ArrayList<MoodEvent>) field.get(searchedUser);
    }
}