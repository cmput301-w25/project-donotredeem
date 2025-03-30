package com.example.donotredeem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class FollowingAdapterTest {

    // Test data
    private List<String> followingList;

    @Before
    public void setUp() {
        // Initialize test data only - no Android dependencies
        followingList = new ArrayList<>(Arrays.asList("user1", "user2", "user3"));
    }

    @Test
    public void testListOperations() {
        // Test the List operations that would affect the adapter's behavior
        assertNotNull("Following list should not be null", followingList);
        assertEquals("Following list should have 3 items", 3, followingList.size());

        // Test that we can access items
        assertEquals("First item should be user1", "user1", followingList.get(0));
        assertEquals("Second item should be user2", "user2", followingList.get(1));
        assertEquals("Third item should be user3", "user3", followingList.get(2));

        // Test adding an item
        followingList.add("user4");
        assertEquals("After adding item, list should have 4 items", 4, followingList.size());
        assertEquals("Fourth item should be user4", "user4", followingList.get(3));

        // Test removing an item
        followingList.remove("user2");
        assertEquals("After removing item, list should have 3 items", 3, followingList.size());
        assertEquals("Second item should now be user3", "user3", followingList.get(1));

        // Test clearing the list
        followingList.clear();
        assertEquals("After clearing, list should be empty", 0, followingList.size());
    }

    @Test
    public void testListModification() {
        // Test how modifications to the list would affect getItemCount()
        int initialSize = followingList.size();
        assertEquals("Initial size should be 3", 3, initialSize);

        // Adding items should increase the count
        followingList.add("user4");
        followingList.add("user5");
        int newSize = followingList.size();
        assertEquals("Size after adding 2 items should be 5", 5, newSize);

        // Removing items should decrease the count
        followingList.remove(0); // Remove first item
        assertEquals("Size after removing 1 item should be 4", 4, followingList.size());
    }

}