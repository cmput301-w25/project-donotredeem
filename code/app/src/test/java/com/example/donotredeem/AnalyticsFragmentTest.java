package com.example.donotredeem;

import static org.junit.Assert.*;

import com.example.donotredeem.Fragments.AnalyticsFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;
import java.util.ArrayList;
/**
 * Unit tests for the {@link AnalyticsFragment} class.
 * This class verifies the correctness of key functionalities related to mood analytics display,
 * calendar date processing, and resource management.
 *
 * <p>Key functionalities tested include:
 * <ul>
 *     <li>Mapping mood states to their corresponding emoji drawable resources</li>
 *     <li>Generating day arrays for calendar month views</li>
 *     <li>Formatting dates into human-readable month/year strings</li>
 *     <li>Retrieving the complete set of emoji resources</li>
 * </ul>
 *
 * <p>Tests use JUnit 4 framework with specific focus on boundary cases and format validation.
 */
@RunWith(JUnit4.class)
public class AnalyticsFragmentTest {

    private AnalyticsFragment fragment;

    /**
     * Initializes the {@link AnalyticsFragment} instance before each test method execution.
     */
    @Before
    public void setUp() {
        fragment = new AnalyticsFragment();
    }

    /**
     * Tests the mapping of mood states to emoji drawable resources.
     * Verifies:
     * <ul>
     *     <li>Correct drawable IDs for all supported mood states ("Happy" to "Tired")</li>
     *     <li>Return of 0 (invalid resource) for unknown mood states</li>
     *     <li>Graceful handling of null input</li>
     * </ul>
     */
    @Test
    public void testGetEmojiForState() {
        assertEquals(R.drawable.hapi, fragment.getEmojiForState("Happy"));
        assertEquals(R.drawable.sad, fragment.getEmojiForState("Sad"));
        assertEquals(R.drawable.fear, fragment.getEmojiForState("Fear"));
        assertEquals(R.drawable.angry, fragment.getEmojiForState("Angry"));
        assertEquals(R.drawable.confused, fragment.getEmojiForState("Confused"));
        assertEquals(R.drawable.disgust, fragment.getEmojiForState("Disgusted"));
        assertEquals(R.drawable.shame, fragment.getEmojiForState("Shameful"));
        assertEquals(R.drawable.surprised, fragment.getEmojiForState("Surprised"));
        assertEquals(R.drawable.shy, fragment.getEmojiForState("Shy"));
        assertEquals(R.drawable.tired, fragment.getEmojiForState("Tired"));
        assertEquals(0, fragment.getEmojiForState("Unknown"));
        assertEquals(0, fragment.getEmojiForState(null));
    }

    /**
     * Tests generation of day arrays for calendar month views.
     * Validates:
     * <ul>
     *     <li>Fixed 42-element array size (6-week grid)</li>
     *     <li>Correct day numbering for February 2020 (leap year with 29 days)</li>
     *     <li>Correct positioning of July 2023 start/end dates</li>
     * </ul>
     */
    @Test
    public void testDaysInMonthArray() {
        // Test February 2020 (leap year)
        ArrayList<String> feb2020 = fragment.daysInMonthArray(LocalDate.of(2020, 2, 1));
        assertEquals(42, feb2020.size()); // Always 6 weeks
        assertTrue(feb2020.contains("29")); // Last day
        assertEquals("", feb2020.get(0)); // Feb 1 2020 was Saturday (position 6 in 0-based)

        // Test July 2023 (31 days, starts on Saturday)
        ArrayList<String> july2023 = fragment.daysInMonthArray(LocalDate.of(2023, 7, 1));
        assertEquals("1", july2023.get(6)); // First day position
        assertEquals("31", july2023.get(36)); // Last day position
    }

    /**
     * Tests conversion of dates to "Month Year" format strings.
     * Checks edge cases including:
     * <ul>
     *     <li>Month with 28/29 days (February)</li>
     *     <li>End-of-year month (December)</li>
     * </ul>
     */
    @Test
    public void testMonthYearFromDate() {
        assertEquals("February 2020",
                fragment.monthYearFromDate(LocalDate.of(2020, 2, 1)));
        assertEquals("December 2025",
                fragment.monthYearFromDate(LocalDate.of(2025, 12, 15)));
    }

    /**
     * Tests retrieval of emoji resource array.
     * Verifies:
     * <ul>
     *     <li>Array contains exactly 10 elements</li>
     *     <li>Correct ordering from "Happy" to "Tired" emojis</li>
     * </ul>
     */
    @Test
    public void testGetEmojiResourcesArray() {
        int[] emojis = fragment.getEmojiResourcesArray();
        assertEquals(10, emojis.length);
        assertEquals(R.drawable.hapi, emojis[0]);
        assertEquals(R.drawable.tired, emojis[9]);
    }
}