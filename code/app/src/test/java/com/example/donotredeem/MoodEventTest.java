package com.example.donotredeem;

import static org.junit.Assert.*;
        import org.junit.Before;
import org.junit.Test;

import com.example.donotredeem.MoodEvent;
import com.google.firebase.firestore.GeoPoint;

/**
 * Unit tests for the {@link MoodEvent} class, verifying constructor behaviors, field validation,
 * and property accessors/mutators. Covers both normal usage and edge cases.
 *
 * <p>Key test areas include:
 * <ul>
 *     <li>Constructor variations (required fields vs. all fields)</li>
 *     <li>Field validation constraints (non-empty emotional state)</li>
 *     <li>Null handling and default values</li>
 *     <li>Setter method functionality</li>
 *     <li>Special constructor with mood event ID</li>
 * </ul>
 */
public class MoodEventTest {

    private MoodEvent moodEvent;
    private GeoPoint testLocation;
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_EMOTIONAL_STATE = "Happy";
    private static final String TEST_DATE = "2025-03-31";
    private static final String TEST_TIME = "12:00";
    private static final String TEST_PLACE = "Home";
    private static final String TEST_SITUATION = "With friends";
    private static final String TEST_TRIGGER = "Good news";
    private static final String TEST_TEXT = "Feeling great today!";
    private static final String TEST_PICTURE = "image_uri_string";
    private static final String TEST_MOOD_ID = "mood123";

    /**
     * Initializes test environment before each test:
     * <ul>
     *     <li>Creates test GeoPoint location</li>
     *     <li>Instantiates MoodEvent with all fields populated</li>
     * </ul>
     */
    @Before
    public void setUp() {
        testLocation = new GeoPoint(53.5461, -113.4938);
        moodEvent = new MoodEvent(
                testLocation, TEST_USERNAME, true, TEST_EMOTIONAL_STATE,
                TEST_DATE, TEST_TIME, TEST_PLACE, TEST_SITUATION,
                TEST_TRIGGER, TEST_TEXT, TEST_PICTURE
        );
    }

    /**
     * Tests constructor with only required fields.
     * Verifies:
     * <ul>
     *     <li>Core fields are properly set (emotional state, date/time, place)</li>
     *     <li>Optional fields remain null when not provided</li>
     *     <li>Location and privacy settings are correctly stored</li>
     * </ul>
     */
    @Test
    public void testConstructorWithRequiredFields() {
        MoodEvent basicMoodEvent = new MoodEvent(
                testLocation, TEST_USERNAME, true, TEST_EMOTIONAL_STATE,
                TEST_DATE, TEST_TIME, TEST_PLACE, null, null, null, null
        );

        assertEquals(TEST_EMOTIONAL_STATE, basicMoodEvent.getEmotionalState());
        assertEquals(TEST_DATE, basicMoodEvent.getDate());
        assertEquals(TEST_TIME, basicMoodEvent.getTime());
        assertEquals(TEST_PLACE, basicMoodEvent.getPlace());
        assertEquals(TEST_USERNAME, basicMoodEvent.getUsername());
        assertTrue(basicMoodEvent.getPrivacy());
        assertEquals(testLocation, basicMoodEvent.getLocation());

        assertNull(basicMoodEvent.getSituation());
        assertNull(basicMoodEvent.getTrigger());
        assertNull(basicMoodEvent.getExplainText());
        assertNull(basicMoodEvent.getExplainPicture());
    }

    /**
     * Tests full-argument constructor with all fields populated.
     * Verifies correct storage of:
     * <ul>
     *     <li>Emotional state, situation, and trigger</li>
     *     <li>Explanation text and picture URI</li>
     *     <li>Privacy setting and username</li>
     * </ul>
     */
    @Test
    public void testConstructorWithAllFields() {
        assertEquals(TEST_EMOTIONAL_STATE, moodEvent.getEmotionalState());
        assertEquals(TEST_DATE, moodEvent.getDate());
        assertEquals(TEST_TIME, moodEvent.getTime());
        assertEquals(TEST_PLACE, moodEvent.getPlace());
        assertEquals(TEST_SITUATION, moodEvent.getSituation());
        assertEquals(TEST_TRIGGER, moodEvent.getTrigger());
        assertEquals(TEST_TEXT, moodEvent.getExplainText());
        assertEquals(TEST_PICTURE, moodEvent.getExplainPicture());
        assertEquals(TEST_USERNAME, moodEvent.getUsername());
        assertTrue(moodEvent.getPrivacy());
        assertEquals(testLocation, moodEvent.getLocation());
    }

    /**
     * Tests constructor with explicit mood event ID parameter.
     * Verifies:
     * <ul>
     *     <li>Mood event ID is properly stored</li>
     *     <li>Other fields maintain correct values</li>
     * </ul>
     */
    @Test
    public void testConstructorWithMoodEventId() {
        MoodEvent moodEventWithId = new MoodEvent(
                testLocation, TEST_USERNAME, true, TEST_MOOD_ID, TEST_EMOTIONAL_STATE,
                TEST_DATE, TEST_TIME, TEST_PLACE, TEST_SITUATION,
                TEST_TRIGGER, TEST_TEXT, TEST_PICTURE
        );

        assertEquals(TEST_MOOD_ID, moodEventWithId.getMoodEventId());
        assertEquals(TEST_EMOTIONAL_STATE, moodEventWithId.getEmotionalState());
    }

    /**
     * Tests null emotional state constraint.
     * Verifies constructor throws {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullEmotionalState() {
        new MoodEvent(testLocation, TEST_USERNAME, true, null, TEST_DATE, TEST_TIME, TEST_PLACE,
                TEST_SITUATION, TEST_TRIGGER, TEST_TEXT, TEST_PICTURE);
    }

    /**
     * Tests empty emotional state constraint.
     * Verifies constructor throws {@link IllegalArgumentException} for whitespace-only input.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyEmotionalState() {
        new MoodEvent(testLocation, TEST_USERNAME, true, "  ", TEST_DATE, TEST_TIME, TEST_PLACE,
                TEST_SITUATION, TEST_TRIGGER, TEST_TEXT, TEST_PICTURE);
    }

    /**
     * Tests null place parameter handling.
     * Verifies empty string is stored instead of null.
     */
    @Test
    public void testConstructorWithNullPlace() {
        MoodEvent moodEventNullPlace = new MoodEvent(
                testLocation, TEST_USERNAME, true, TEST_EMOTIONAL_STATE,
                TEST_DATE, TEST_TIME, null, TEST_SITUATION,
                TEST_TRIGGER, TEST_TEXT, TEST_PICTURE
        );

        assertEquals("", moodEventNullPlace.getPlace());
    }

    /**
     * Tests default constructor availability.
     * Verifies no-argument constructor creates non-null instance.
     */
    @Test
    public void testDefaultConstructor() {
        MoodEvent defaultMoodEvent = new MoodEvent();
        assertNotNull(defaultMoodEvent);
    }

    /**
     * Comprehensive test of all setter methods.
     * Verifies:
     * <ul>
     *     <li>All property types can be modified (strings, booleans, GeoPoints)</li>
     *     <li>Mood event ID can be set post-construction</li>
     *     <li>Field values persist after modification</li>
     * </ul>
     */
    @Test
    public void testSetters() {
        MoodEvent testMoodEvent = new MoodEvent();

        GeoPoint newLocation = new GeoPoint(51.0447, -114.0719);
        testMoodEvent.setLocation(newLocation);
        assertEquals(newLocation, testMoodEvent.getLocation());

        testMoodEvent.setPrivacy(false);
        assertFalse(testMoodEvent.getPrivacy());

        testMoodEvent.setUsername("newUser");
        assertEquals("newUser", testMoodEvent.getUsername());

        testMoodEvent.setDate("2025-04-01");
        assertEquals("2025-04-01", testMoodEvent.getDate());

        testMoodEvent.setTime("13:30");
        assertEquals("13:30", testMoodEvent.getTime());

        testMoodEvent.setPlace("Office");
        assertEquals("Office", testMoodEvent.getPlace());

        testMoodEvent.setEmotionalState("Sad");
        assertEquals("Sad", testMoodEvent.getEmotionalState());

        testMoodEvent.setSituation("Alone");
        assertEquals("Alone", testMoodEvent.getSituation());

        testMoodEvent.setTrigger("Bad news");
        assertEquals("Bad news", testMoodEvent.getTrigger());

        testMoodEvent.setExplainText("Not feeling great");
        assertEquals("Not feeling great", testMoodEvent.getExplainText());

        testMoodEvent.setExplainPicture("new_image_uri");
        assertEquals("new_image_uri", testMoodEvent.getExplainPicture());

        testMoodEvent.setMoodEventId("mood456");
        assertEquals("mood456", testMoodEvent.getMoodEventId());
    }
}
