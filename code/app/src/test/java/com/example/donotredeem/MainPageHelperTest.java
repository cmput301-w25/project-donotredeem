package com.example.donotredeem;

import static org.junit.Assert.*;

import android.util.Log;

import com.example.donotredeem.Fragments.MainPage;
import com.example.donotredeem.MoodEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class MainPageHelperTest {

    private MainPage mainPage;
    private ArrayList<MoodEvent> testEvents;

    @Before
    public void setup() throws Exception {
        mainPage = new MainPage();
        testEvents = new ArrayList<>();
    }

    // Test date parsing ================================================
    @Test
    public void parseDate_validFormat_returnsCorrectDate() {
        LocalDate result = mainPage.parseStringToDate("31/12/2023");
        assertEquals(LocalDate.of(2023, 12, 31), result);
    }

    @Test
    public void parseDate_invalidDate_returnsMinDate() {
        LocalDate result = mainPage.parseStringToDate("32/13/2020");
        assertEquals(LocalDate.MIN, result);
    }

    // Test time parsing ================================================
    @Test
    public void parseTime_validWithoutSeconds_returnsCorrectTime() {
        LocalTime result = mainPage.parseStringToTime("23:59");
        assertEquals(LocalTime.of(23, 59), result);
    }

    @Test
    public void parseTime_validWithSeconds_returnsCorrectTime() {
        LocalTime result = mainPage.parseStringToTime("12:34:56");
        assertEquals(LocalTime.of(12, 34, 56), result);
    }

    @Test
    public void parseTime_invalidTime_returnsMinTime() {
        LocalTime result = mainPage.parseStringToTime("24:00");
        assertEquals(LocalTime.MIN, result);
    }

    // Test sorting logic ================================================
    @Test
    public void sortMoodEvents_sortsByDateDescThenTimeDesc() throws Exception {
        // Create test events
        testEvents.add(createMockEvent("02/01/2024", "09:00"));
        testEvents.add(createMockEvent("01/01/2024", "10:00"));
        testEvents.add(createMockEvent("02/01/2024", "10:00"));

        // Get private sort method via reflection
        Method sortMethod = MainPage.class.getDeclaredMethod("sort", ArrayList.class);
        sortMethod.setAccessible(true);

        @SuppressWarnings("unchecked")
        ArrayList<MoodEvent> sorted = (ArrayList<MoodEvent>) sortMethod.invoke(mainPage, testEvents);

        // Verify order
        assertEquals("02/01/2024", sorted.get(0).getDate());
        assertEquals("10:00", sorted.get(0).getTime());

        assertEquals("02/01/2024", sorted.get(1).getDate());
        assertEquals("09:00", sorted.get(1).getTime());

        assertEquals("01/01/2024", sorted.get(2).getDate());
    }

    private MoodEvent createMockEvent(String date, String time) {
        return new MoodEvent() {
            @Override
            public String getDate() { return date; }

            @Override
            public String getTime() { return time; }

        };
    }
}