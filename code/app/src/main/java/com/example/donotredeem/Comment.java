package com.example.donotredeem;


import com.google.firebase.Timestamp;

public class Comment {
    private String author, pfp;
    private String commentText;
    private Timestamp timestamp;

    // Empty constructor required for Firestore
    public Comment() {}

    public Comment(String author, String commentText, Timestamp timestamp, String pfp) {
        this.author = author;
        this.commentText = commentText;
        this.timestamp = timestamp;
        this.pfp = pfp;
    }

    public String getAuthor() {
        return author;
    }

    public String getCommentText() {
        return commentText;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getPfp() {
        return pfp;
    }

}
