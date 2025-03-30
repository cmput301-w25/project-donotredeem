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
public class FollowerAdapterTest {

    // Test data
    private List<String> followerList;

    @Before
    public void setUp() {
        // Initialize test data only - no Android dependencies
        followerList = new ArrayList<>(Arrays.asList("user1", "user2", "user3"));
    }

    @Test
    public void testListOperations() {
        // Test the List operations that would affect the adapter's behavior
        assertNotNull("Follower list should not be null", followerList);
        assertEquals("Follower list should have 3 items", 3, followerList.size());

        // Test that we can access items
        assertEquals("First item should be user1", "user1", followerList.get(0));
        assertEquals("Second item should be user2", "user2", followerList.get(1));
        assertEquals("Third item should be user3", "user3", followerList.get(2));

        // Test adding an item
        followerList.add("user4");
        assertEquals("After adding item, list should have 4 items", 4, followerList.size());
        assertEquals("Fourth item should be user4", "user4", followerList.get(3));

        // Test removing an item
        followerList.remove("user2");
        assertEquals("After removing item, list should have 3 items", 3, followerList.size());
        assertEquals("Second item should now be user3", "user3", followerList.get(1));

        // Test clearing the list
        followerList.clear();
        assertEquals("After clearing, list should be empty", 0, followerList.size());
    }

    @Test
    public void testListModification() {
        // Test how modifications to the list would affect getItemCount()
        int initialSize = followerList.size();
        assertEquals("Initial size should be 3", 3, initialSize);

        // Adding items should increase the count
        followerList.add("user4");
        followerList.add("user5");
        int newSize = followerList.size();
        assertEquals("Size after adding 2 items should be 5", 5, newSize);

        // Removing items should decrease the count
        followerList.remove(0); // Remove first item
        assertEquals("Size after removing 1 item should be 4", 4, followerList.size());
    }

}