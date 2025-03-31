package com.example.donotredeem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.donotredeem.Classes.Comment;
import com.google.firebase.Timestamp;

/**
 * Unit tests for list operations relevant to a {@code CommentAdapter} implementation.
 * This class verifies core functionality of comment list management without Android framework dependencies,
 * focusing on CRUD operations and list modification scenarios.
 *
 * <p>Key test areas include:
 * <ul>
 *     <li>List initialization and basic integrity checks</li>
 *     <li>Item addition/removal operations and size validation</li>
 *     <li>Data consistency after modifications</li>
 *     <li>Boundary cases for list manipulation</li>
 * </ul>
 */
@RunWith(JUnit4.class)
public class CommentAdapterTest {

    // Test data
    private ArrayList<Comment> commentList;

    /**
     * Initializes test data before each test method execution.
     * Creates three comments with:
     * <ul>
     *     <li>Different authors (user1-user3)</li>
     *     <li>Sequential comment texts</li>
     *     <li>Timestamps spanning different time intervals (1 hour, 2 hours, and 24 hours before test execution)</li>
     * </ul>
     */
    @Before
    public void setUp() {
        // Initialize test data without Android dependencies
        commentList = new ArrayList<>();

        // Create example comments
        // Note: We can use Timestamp since it's a Firebase class, not an Android framework class
        commentList.add(new Comment("user1", "This is comment 1", new Timestamp(new Date(System.currentTimeMillis() - 3600000))));
        commentList.add(new Comment("user2", "This is comment 2", new Timestamp(new Date(System.currentTimeMillis() - 7200000))));
        commentList.add(new Comment("user3", "This is comment 3", new Timestamp(new Date(System.currentTimeMillis() - 86400000))));
    }

    /**
     * Tests fundamental list operations and data validation.
     * Verifies:
     * <ul>
     *     <li>Initial list non-null state and correct size (3 items)</li>
     *     <li>Proper item retrieval and field validation for first comment</li>
     *     <li>List size changes after adding new comments</li>
     *     <li>Item removal and subsequent order adjustment</li>
     *     <li>Complete list clearance functionality</li>
     * </ul>
     */
    @Test
    public void testListOperations() {
        // Test the list operations that would affect the adapter's behavior
        assertNotNull("Comment list should not be null", commentList);
        assertEquals("Comment list should have 3 items", 3, commentList.size());

        // Test item access
        Comment firstComment = commentList.get(0);
        assertEquals("First comment should be from user1", "user1", firstComment.getAuthor());
        assertEquals("First comment text should match", "This is comment 1", firstComment.getCommentText());

        // Test adding a comment
        Comment newComment = new Comment("user4", "This is a new comment", new Timestamp(new Date()));
        commentList.add(newComment);
        assertEquals("After adding item, list should have 4 items", 4, commentList.size());
        assertEquals("Fourth comment should be from user4", "user4", commentList.get(3).getAuthor());

        // Test removing a comment
        commentList.remove(1); // Remove second comment
        assertEquals("After removing item, list should have 3 items", 3, commentList.size());
        assertEquals("Second comment should now be from user3", "user3", commentList.get(1).getAuthor());

        // Test clearing the list
        commentList.clear();
        assertEquals("After clearing, list should be empty", 0, commentList.size());
    }

    /**
     * Tests dynamic list size changes through modifications.
     * Validates:
     * <ul>
     *     <li>Initial size confirmation (3 items)</li>
     *     <li>Size increment after adding multiple items</li>
     *     <li>Size decrement after removing items</li>
     *     <li>Consistent size reporting through modifications</li>
     * </ul>
     */
    @Test
    public void testListModification() {
        // Test how modifications to the list would affect getItemCount()
        int initialSize = commentList.size();
        assertEquals("Initial size should be 3", 3, initialSize);

        // Adding comments should increase the count
        Comment comment4 = new Comment("user4", "Comment 4", new Timestamp(new Date()));
        Comment comment5 = new Comment("user5", "Comment 5", new Timestamp(new Date()));
        commentList.add(comment4);
        commentList.add(comment5);

        int newSize = commentList.size();
        assertEquals("Size after adding 2 items should be 5", 5, newSize);

        // Removing comments should decrease the count
        commentList.remove(0); // Remove first comment
        assertEquals("Size after removing 1 item should be 4", 4, commentList.size());
    }

}