package com.example.donotredeem;

import static org.junit.Assert.*;

import com.example.donotredeem.Fragments.moodhistory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Unit tests for date/time parsing functionality in the {@link moodhistory} fragment.
 * Verifies correct conversion of string representations to {@link LocalDate} and {@link LocalTime} objects.
 *
 * <p>Test scenarios include:
 * <ul>
 *     <li>Standard date formats and leap year handling</li>
 *     <li>Time parsing with/without seconds</li>
 *     <li>Boundary cases like midnight</li>
 * </ul>
 */
@RunWith(JUnit4.class)
public class MoodHistoryTest {

    private moodhistory fragment;

    /**
     * Initializes a fresh {@link moodhistory} instance before each test method execution.
     */
    @Before
    public void setUp() {
        fragment = new moodhistory();
    }

    /**
     * Tests parsing of valid date string in DD/MM/YYYY format.
     * Verifies correct conversion to {@link LocalDate} with:
     * <ul>
     *     <li>Year: 2023</li>
     *     <li>Month: December (12)</li>
     *     <li>Day: 25</li>
     * </ul>
     */    @Test
    public void testParseStringToDate_ValidFormat() {
        LocalDate date = fragment.parseStringToDate("25/12/2023");
        assertEquals(2023, date.getYear());
        assertEquals(12, date.getMonthValue());
        assertEquals(25, date.getDayOfMonth());
    }

    /**
     * Tests leap year date handling.
     * Verifies February 29, 2020 is parsed correctly to {@link LocalDate}.
     */
    @Test
    public void testParseStringToDate_LeapYear() {
        LocalDate date = fragment.parseStringToDate("29/02/2020");
        assertEquals(2020, date.getYear());
        assertEquals(2, date.getMonthValue());
        assertEquals(29, date.getDayOfMonth());
    }


    /**
     * Tests time parsing without seconds.
     * Verifies conversion of "23:59" to {@link LocalTime} with:
     * <ul>
     *     <li>23 hours</li>
     *     <li>59 minutes</li>
     *     <li>0 seconds</li>
     * </ul>
     */
    @Test
    public void testParseStringToTime_ValidWithoutSeconds() {
        LocalTime time = fragment.parseStringToTime("23:59");
        assertEquals(23, time.getHour());
        assertEquals(59, time.getMinute());
        assertEquals(0, time.getSecond());
    }

    /**
     * Tests time parsing with seconds.
     * Verifies conversion of "12:34:56" to {@link LocalTime} with:
     * <ul>
     *     <li>12 hours</li>
     *     <li>34 minutes</li>
     *     <li>56 seconds</li>
     * </ul>
     */
    @Test
    public void testParseStringToTime_ValidWithSeconds() {
        LocalTime time = fragment.parseStringToTime("12:34:56");
        assertEquals(12, time.getHour());
        assertEquals(34, time.getMinute());
        assertEquals(56, time.getSecond());
    }

    /**
     * Tests midnight time parsing.
     * Verifies "00:00" converts to {@link LocalTime} with:
     * <ul>
     *     <li>0 hours</li>
     *     <li>0 minutes</li>
     * </ul>
     */
    @Test
    public void testParseStringToTime_Midnight() {
        LocalTime time = fragment.parseStringToTime("00:00");
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
    }
}

