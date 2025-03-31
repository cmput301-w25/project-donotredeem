package com.example.donotredeem;

import static org.junit.Assert.*;

import com.example.donotredeem.Enumertions.MoodTypeEnum;
import com.example.donotredeem.Fragments.FilterFragment;
import com.example.donotredeem.Classes.MoodEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;

/**
 * Unit tests for the {@link FilterFragment} class's filtering and mood mapping functionality.
 * Verifies core features including search term matching, whole word detection, and mood button ID resolution.
 *
 * <p>Key test areas include:
 * <ul>
 *     <li>Multi-field search matching (emotional state, location, triggers, descriptions)</li>
 *     <li>Regex-based whole word boundary detection</li>
 *     <li>Mood type mapping from button IDs</li>
 * </ul>
 *
 * <p>Uses JUnit 4 framework with custom {@link TestMoodEvent} class for controlled date testing.
 */
@RunWith(JUnit4.class)
public class FilterFragmentTest {

    private FilterFragment fragment;
    private final TestMoodEvent currentMonthEvent = new TestMoodEvent("01/" + LocalDate.now().getMonthValue() + "/" + LocalDate.now().getYear());
    private final TestMoodEvent lastMonthEvent = new TestMoodEvent("01/" + LocalDate.now().minusMonths(1).getMonthValue() + "/" + LocalDate.now().getYear());

    /**
     * Test-specific {@link MoodEvent} extension with fixed date values.
     * Allows controlled testing of date-based filtering without real-time dependencies.
     */
    static class TestMoodEvent extends MoodEvent {
        private final String date;

        TestMoodEvent(String date) {
            this.date = date;
        }

        @Override
        public String getDate() {
            return date;
        }
    }

    /**
     * Initializes a new {@link FilterFragment} instance before each test method execution.
     */
    @Before
    public void setUp() throws Exception {
        fragment = new FilterFragment();
    }


    /**
     * Tests multi-field search capability with case-insensitive partial matching.
     * Verifies matches across:
     * <ul>
     *     <li>Emotional state ("happy")</li>
     *     <li>Location ("home")</li>
     *     <li>Trigger partial match ("gathering")</li>
     *     <li>Description phrase match ("great time")</li>
     *     <li>Negative case for non-matching term ("sad")</li>
     * </ul>
     */
    @Test
    public void testMatchesSearch() {
        MoodEvent event = new MoodEvent();
        event.setEmotionalState("Happy");
        event.setPlace("Home");
        event.setTrigger("Family gathering");
        event.setExplainText("Had a great time with family");

        assertTrue(fragment.matchesSearch(event, "happy"));
        assertTrue(fragment.matchesSearch(event, "home"));
        assertTrue(fragment.matchesSearch(event, "gathering"));
        assertTrue(fragment.matchesSearch(event, "great time"));
        assertFalse(fragment.matchesSearch(event, "sad"));
    }

    /**
     * Tests regex-based whole word boundary detection.
     * Verifies:
     * <ul>
     *     <li>Exact match with word boundaries ("\\bhappy\\b")</li>
     *     <li>Rejection of prefix matches ("Unhappy")</li>
     *     <li>Rejection of suffix matches ("Happily")</li>
     * </ul>
     */
    @Test
    public void testMatchesWholeWord() {
        assertTrue(fragment.matchesWholeWord("Happy birthday", "\\bhappy\\b"));
        assertFalse(fragment.matchesWholeWord("Unhappy day", "\\bhappy\\b"));
        assertFalse(fragment.matchesWholeWord("Happily ever after", "\\bhappy\\b"));
    }

    /**
     * Tests mood type resolution from button IDs using reflection-injected test values.
     * Verifies:
     * <ul>
     *     <li>Correct mood mapping for first/last buttons in sequence (1001=Happy, 1010=Tired)</li>
     *     <li>Null return for unrecognized button ID (9999)</li>
     * </ul>
     *
     * @throws Exception if reflection field modification fails
     */
    @Test
    public void testGetMoodForButtonId() throws Exception {
        int[] testIds = {1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010};
        java.lang.reflect.Field field = FilterFragment.class.getDeclaredField("emojiButtonIds");
        field.setAccessible(true);
        field.set(fragment, testIds);

        assertEquals(MoodTypeEnum.Happy, fragment.getMoodForButtonId(1001));
        assertEquals(MoodTypeEnum.Sad, fragment.getMoodForButtonId(1002));
        assertNull(fragment.getMoodForButtonId(9999));
    }
}