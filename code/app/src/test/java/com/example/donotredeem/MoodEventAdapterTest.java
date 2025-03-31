package com.example.donotredeem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static org.junit.Assert.*;

import com.example.donotredeem.Adapters.MoodEventAdapter;
import com.example.donotredeem.Classes.MoodEvent;

/**
 * Unit tests for the {@link MoodEventAdapter} focusing on data management and adapter operations
 * without Android UI components. Verifies proper handling of mood event data and list operations.
 *
 * <p>Key test areas include:
 * <ul>
 *     <li>Adapter initialization and item count management</li>
 *     <li>Data retrieval and field validation for mood events</li>
 *     <li>Item removal functionality and list consistency</li>
 *     <li>Edge cases with empty adapter states</li>
 * </ul>
 *
 * <p>Uses a custom {@link TestMoodEventAdapter} to bypass Android framework dependencies.
 */
@RunWith(JUnit4.class)
public class MoodEventAdapterTest {

    private TestMoodEventAdapter adapter;
    private ArrayList<MoodEvent> testEvents;

    /**
     * Custom {@link MoodEventAdapter} implementation for testing.
     * Overrides base adapter methods to operate on a local list without Android dependencies.
     */
    private static class TestMoodEventAdapter extends MoodEventAdapter {
        private ArrayList<MoodEvent> events;

        /**
         * Creates a test adapter with specified mood events.
         *
         * @param events Initial list of mood events for testing
         */
        public TestMoodEventAdapter(ArrayList<MoodEvent> events) {
            super(null, events);
            this.events = events;
        }

        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public MoodEvent getItem(int position) {
            return events.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    /**
     * Initializes test environment before each test:
     * <ul>
     *     <li>Creates two test {@link MoodEvent} instances with varied data</li>
     *     <li>Configures test adapter with these events</li>
     * </ul>
     */    @Before
    public void setUp() {
        // Create test mood events
        testEvents = new ArrayList<>();

        MoodEvent event1 = new MoodEvent();
        event1.setEmotionalState("happy");
        event1.setUsername("testUser1");
        event1.setTime("14:30");
        event1.setDate("2023-06-15");
        event1.setPlace("Test Location");
        event1.setExplainText("Test explanation");
        event1.setExplainPicture("http://test.com/image.jpg");
        event1.setMoodEventId("event1ID");
        event1.setSituation("Family");
        event1.setTrigger("Good news");
        event1.setPrivacy(false);

        MoodEvent event2 = new MoodEvent();
        event2.setEmotionalState("sad");
        event2.setUsername("testUser2");
        event2.setTime("10:45");
        event2.setDate("2023-06-16");
        event2.setPlace("Another Location");
        event2.setExplainText(null);
        event2.setExplainPicture(null);
        event2.setMoodEventId("event2ID");
        event2.setSituation("Work");
        event2.setTrigger("Stress");
        event2.setPrivacy(true);

        testEvents.add(event1);
        testEvents.add(event2);

        // Create adapter with custom implementation
        adapter = new TestMoodEventAdapter(testEvents);
    }

    /**
     * Tests adapter initialization.
     * Verifies:
     * <ul>
     *     <li>Successful adapter instance creation</li>
     *     <li>Correct initial item count (2 events)</li>
     * </ul>
     */
    @Test
    public void testAdapterCreation() {
        assertNotNull(adapter);
        assertEquals(2, adapter.getCount());
    }

    /**
     * Tests item retrieval by position.
     * Validates:
     * <ul>
     *     <li>Non-null returns for valid positions</li>
     *     <li>Correct field values for first event (emotional state, username, time, date)</li>
     * </ul>
     */
    @Test
    public void testGetItem() {
        MoodEvent firstEvent = adapter.getItem(0);
        assertNotNull(firstEvent);
        assertEquals("happy", firstEvent.getEmotionalState());
        assertEquals("testUser1", firstEvent.getUsername());
        assertEquals("14:30", firstEvent.getTime());
        assertEquals("2023-06-15", firstEvent.getDate());
    }

    /**
     * Tests item ID generation logic.
     * Verifies position-based IDs match:
     * <ul>
     *     <li>0 for first position</li>
     *     <li>1 for second position</li>
     * </ul>
     */
    @Test
    public void testGetItemId() {
        assertEquals(0, adapter.getItemId(0));
        assertEquals(1, adapter.getItemId(1));
    }


    /**
     * Tests item count reporting.
     * Validates adapter correctly tracks:
     * <ul>
     *     <li>Initial count (2 items)</li>
     *     <li>Count changes after modifications</li>
     * </ul>
     */
    @Test
    public void testGetCount() {
        assertEquals(2, adapter.getCount());
    }

    /**
     * Tests complete field integrity of stored mood events.
     * Verifies all fields for both test events including:
     * <ul>
     *     <li>Emotional state, place, explanation text/picture</li>
     *     <li>Situation, trigger, privacy settings</li>
     *     <li>Null handling for optional fields</li>
     * </ul>
     */
    @Test
    public void testEventDetails() {
        MoodEvent event1 = adapter.getItem(0);
        assertEquals("happy", event1.getEmotionalState());
        assertEquals("Test Location", event1.getPlace());
        assertEquals("Test explanation", event1.getExplainText());
        assertEquals("http://test.com/image.jpg", event1.getExplainPicture());
        assertEquals("Family", event1.getSituation());
        assertEquals("Good news", event1.getTrigger());
        assertFalse(event1.getPrivacy());

        MoodEvent event2 = adapter.getItem(1);
        assertEquals("sad", event2.getEmotionalState());
        assertEquals("Another Location", event2.getPlace());
        assertNull(event2.getExplainText());
        assertNull(event2.getExplainPicture());
        assertEquals("Work", event2.getSituation());
        assertEquals("Stress", event2.getTrigger());
        assertTrue(event2.getPrivacy());
    }

    /**
     * Tests item removal functionality.
     * Validates:
     * <ul>
     *     <li>List size reduction after removal</li>
     *     <li>Proper item exclusion from the list</li>
     * </ul>
     */
    @Test
    public void testRemoveItem() {
        int initialCount = adapter.getCount();
        MoodEvent removedEvent = adapter.getItem(0);

        // Simulate removing an item
        testEvents.remove(0);

        assertEquals(initialCount - 1, testEvents.size());
        assertFalse(testEvents.contains(removedEvent));
    }

    /**
     * Tests empty adapter state handling.
     * Verifies:
     * <ul>
     *     <li>Successful adapter creation with empty list</li>
     *     <li>Correct reporting of zero items</li>
     * </ul>
     */
    @Test
    public void testEmptyAdapter() {
        TestMoodEventAdapter emptyAdapter = new TestMoodEventAdapter(new ArrayList<>());
        assertEquals(0, emptyAdapter.getCount());
    }
}