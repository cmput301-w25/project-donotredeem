package com.example.donotredeem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class MoodEventAdapterTest {

    private TestMoodEventAdapter adapter;
    private ArrayList<MoodEvent> testEvents;

    // Custom subclass to override ArrayAdapter methods
    private static class TestMoodEventAdapter extends MoodEventAdapter {
        private ArrayList<MoodEvent> events;

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

    @Before
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

    @Test
    public void testAdapterCreation() {
        assertNotNull(adapter);
        assertEquals(2, adapter.getCount());
    }

    @Test
    public void testGetItem() {
        MoodEvent firstEvent = adapter.getItem(0);
        assertNotNull(firstEvent);
        assertEquals("happy", firstEvent.getEmotionalState());
        assertEquals("testUser1", firstEvent.getUsername());
        assertEquals("14:30", firstEvent.getTime());
        assertEquals("2023-06-15", firstEvent.getDate());
    }

    @Test
    public void testGetItemId() {
        assertEquals(0, adapter.getItemId(0));
        assertEquals(1, adapter.getItemId(1));
    }

    @Test
    public void testGetCount() {
        assertEquals(2, adapter.getCount());
    }

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

    @Test
    public void testRemoveItem() {
        int initialCount = adapter.getCount();
        MoodEvent removedEvent = adapter.getItem(0);

        // Simulate removing an item
        testEvents.remove(0);

        assertEquals(initialCount - 1, testEvents.size());
        assertFalse(testEvents.contains(removedEvent));
    }

    @Test
    public void testEmptyAdapter() {
        TestMoodEventAdapter emptyAdapter = new TestMoodEventAdapter(new ArrayList<>());
        assertEquals(0, emptyAdapter.getCount());
    }
}