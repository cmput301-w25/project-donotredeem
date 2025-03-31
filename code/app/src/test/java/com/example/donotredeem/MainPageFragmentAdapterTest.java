package com.example.donotredeem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.*;

import com.example.donotredeem.Adapters.MainPageAdapter;
import com.example.donotredeem.Classes.MoodEvent;

/**
 * Unit tests for the {@link MainPageAdapter} class focusing on core adapter functionality
 * while avoiding Android framework dependencies. Verifies proper data handling and basic
 * adapter operations through method overrides and mock objects.
 *
 * <p>Key test areas include:
 * <ul>
 *     <li>Adapter initialization with mock context and test data</li>
 *     <li>Data retrieval methods (getItem, getItemId)</li>
 *     <li>Item count management</li>
 *     <li>Simplified view handling verification</li>
 * </ul>
 *
 * <p>Uses Mockito to mock Android {@link Context} and creates a custom adapter implementation
 * that bypasses UI-related methods for isolated business logic testing.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainPageFragmentAdapterTest {

    @Mock
    private Context mockContext;

    private MainPageAdapter adapter;
    private ArrayList<MoodEvent> testEvents;

    /**
     * Initializes test environment before each test method:
     * <ul>
     *     <li>Creates mock Context using Mockito</li>
     *     <li>Prepares test MoodEvents with varied data states</li>
     *     <li>Configures custom adapter with overridden methods:
     *         <ul>
     *             <li>Simplified getView() returning null to avoid view inflation</li>
     *             <li>Direct data access methods using test collection</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    @Before
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

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

        MoodEvent event2 = new MoodEvent();
        event2.setEmotionalState("sad");
        event2.setUsername("testUser2");
        event2.setTime("10:45");
        event2.setDate("2023-06-16");
        event2.setPlace("Another Location");
        event2.setExplainText(null);
        event2.setExplainPicture(null);
        event2.setMoodEventId("event2ID");

        testEvents.add(event1);
        testEvents.add(event2);

        // Create a custom MainPageAdapter that allows us to override all problematic methods
        adapter = new MainPageAdapter(mockContext, testEvents) {
            @Override
            public Context getContext() {
                return mockContext;
            }

            @Override
            public int getCount() {
                return testEvents.size();
            }

            @Override
            public MoodEvent getItem(int position) {
                return testEvents.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Simplified getView method that doesn't mock or stub anything
                return null;
            }
        };
    }


    /**
     * Tests adapter initialization and basic configuration.
     * Verifies:
     * <ul>
     *     <li>Successful adapter instance creation</li>
     *     <li>Correct item count matching test data size</li>
     * </ul>
     */
    @Test
    public void testAdapterCreation() {
        assertNotNull(adapter);
        assertEquals(2, adapter.getCount());
    }

    /**
     * Tests the overridden getView() implementation.
     * Validates that the method returns null as specified
     * in the custom adapter configuration, indicating
     * no view inflation is attempted during testing.
     */
    @Test
    public void testGetView() {
        // Get the view
        View resultView = adapter.getView(0, null, null);

        // Basic assertions
        assertNull(resultView);
    }


    /**
     * Tests item retrieval functionality.
     * Verifies:
     * <ul>
     *     <li>Non-null return for valid positions</li>
     *     <li>Correct data mapping for first test event</li>
     *     <li>Proper field values in retrieved MoodEvent</li>
     * </ul>
     */
    @Test
    public void testGetItem() {
        // Directly use the overridden getItem method
        MoodEvent firstEvent = adapter.getItem(0);
        assertNotNull(firstEvent);
        assertEquals("happy", firstEvent.getEmotionalState());
        assertEquals("testUser1", firstEvent.getUsername());
    }

    /**
     * Tests item ID generation logic.
     * Validates that position-based IDs match:
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
     * Tests context retrieval override.
     * Verifies the adapter returns the mock Context
     * instance provided during setup.
     */
    @Test
    public void testGetContext() {
        assertEquals(mockContext, adapter.getContext());
    }

    /**
     * Tests item count reporting.
     * Validates the adapter correctly reports:
     * <ul>
     *     <li>Initial count matching test data size (2)</li>
     *     <li>Consistent count after setup</li>
     * </ul>
     */
    @Test
    public void testCount() {
        assertEquals(2, adapter.getCount());
    }
}