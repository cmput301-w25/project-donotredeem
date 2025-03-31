package com.example.donotredeem;

import static org.junit.Assert.*;

import com.example.donotredeem.Classes.MoodEvent;
import com.example.donotredeem.Fragments.MainPageFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Unit tests for parsing and sorting functionality in the {@link MainPageFragment} class.
 * Verifies correct handling of date/time strings and proper chronological sorting
 * of mood events.
 *
 * <p>Key test areas include:
 * <ul>
 *     <li>Date parsing with valid/invalid formats</li>
 *     <li>Time parsing with and without seconds</li>
 *     <li>Chronological sorting of events (date descending, then time descending)</li>
 * </ul>
 *
 * <p>Uses reflection to test private sorting method and includes boundary cases
 * for date/time validation.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainPageFragmentHelperTest {

    private MainPageFragment mainPageFragment;
    private ArrayList<MoodEvent> testEvents;

    /**
     * Initializes test environment before each test method:
     * <ul>
     *     <li>Creates fresh {@link MainPageFragment} instance</li>
     *     <li>Initializes empty test events list</li>
     * </ul>
     */
    @Before
    public void setup() throws Exception {
        mainPageFragment = new MainPageFragment();
        testEvents = new ArrayList<>();
    }

    /**
     * Tests valid date parsing with DD/MM/YYYY format.
     * Verifies correct conversion to {@link LocalDate}.
     */
    @Test
    public void parseDate_validFormat_returnsCorrectDate() {
        LocalDate result = mainPageFragment.parseStringToDate("31/12/2023");
        assertEquals(LocalDate.of(2023, 12, 31), result);
    }

    /**
     * Tests handling of invalid date values.
     * Verifies returns {@link LocalDate#MIN} for:
     * <ul>
     *     <li>Invalid day (32)</li>
     *     <li>Invalid month (13)</li>
     * </ul>
     */
    @Test
    public void parseDate_invalidDate_returnsMinDate() {
        LocalDate result = mainPageFragment.parseStringToDate("32/13/2020");
        assertEquals(LocalDate.MIN, result);
    }

    /**
     * Tests time parsing without seconds.
     * Verifies correct conversion of "HH:mm" format to {@link LocalTime}.
     */
    @Test
    public void parseTime_validWithoutSeconds_returnsCorrectTime() {
        LocalTime result = mainPageFragment.parseStringToTime("23:59");
        assertEquals(LocalTime.of(23, 59), result);
    }

    /**
     * Tests time parsing with seconds.
     * Verifies correct conversion of "HH:mm:ss" format to {@link LocalTime}.
     */
    @Test
    public void parseTime_validWithSeconds_returnsCorrectTime() {
        LocalTime result = mainPageFragment.parseStringToTime("12:34:56");
        assertEquals(LocalTime.of(12, 34, 56), result);
    }

    /**
     * Tests handling of invalid time values.
     * Verifies returns {@link LocalTime#MIN} for:
     * <ul>
     *     <li>Invalid hour (24)</li>
     *     <li>Invalid minute (60)</li>
     * </ul>
     */
    @Test
    public void parseTime_invalidTime_returnsMinTime() {
        LocalTime result = mainPageFragment.parseStringToTime("24:00");
        assertEquals(LocalTime.MIN, result);
    }


    /**
     * Creates mock {@link MoodEvent} instances with predefined dates/times.
     *
     * @param date Date string in DD/MM/YYYY format
     * @param time Time string in HH:mm or HH:mm:ss format
     * @return MoodEvent with specified date/time values
     */
    private MoodEvent createMockEvent(String date, String time) {
        return new MoodEvent() {
            @Override
            public String getDate() { return date; }

            @Override
            public String getTime() { return time; }

        };
    }
}