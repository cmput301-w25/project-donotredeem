package com.example.donotredeem;


import com.google.firebase.Timestamp;

/**
 * Represents a comment entity with author, content, and timestamp information.
 * Used for Firestore data serialization/deserialization and comment display.
 */
public class Comment {
    private String author, pfp;
    private String commentText;
    private Timestamp timestamp;

    /**
     * Empty constructor required for Firestore data deserialization
     */
    public Comment() {}

    /**
     * Constructs a Comment with essential data
     * @param author Username of the comment author
     * @param commentText Text content of the comment
     * @param timestamp Creation timestamp from Firestore
     */
    public Comment(String author, String commentText, Timestamp timestamp) {
        this.author = author;
        this.commentText = commentText;
        this.timestamp = timestamp;
    }

    /**
     * @return Username of the comment author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return Text content of the comment
     */
    public String getCommentText() {
        return commentText;
    }

    /**
     * @return Firestore timestamp of when the comment was created
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

}
