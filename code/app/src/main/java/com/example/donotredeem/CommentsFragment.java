package com.example.donotredeem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

/**
 * Bottom sheet dialog fragment for displaying and managing comments on mood events.
 * Features:
 * - Real-time comment loading from Firestore
 * - Comment input with validation
 * - Auto-expanding bottom sheet behavior
 * - RecyclerView with comment threading
 */
public class CommentsFragment extends BottomSheetDialogFragment {

    private RecyclerView commentsRecyclerView;
    private EditText commentInput;
    private ImageButton postComment;
    private CommentsAdapter commentsAdapter;
    private ArrayList<Comment> commentList;
    private FirebaseFirestore db;
    private String postId;

    /**
     * Initializes fragment styling for bottom sheet appearance
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    /**
     * Creates and configures the fragment's view hierarchy
     * @return Root view for the fragment's UI
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        commentInput = view.findViewById(R.id.commentInput);
        postComment = view.findViewById(R.id.postComment);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            postId = getArguments().getString("postId");
        }

        commentList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(commentList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(commentsAdapter);

        loadComments();

        postComment.setOnClickListener(v -> {
            String text = commentInput.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                addComment(text);
            } else {
                Toast.makeText(getContext(), "Enter a comment", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * Configures bottom sheet behavior to expand fully and disable collapsing
     */
    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        }
    }


    /**
     * Loads comments from Firestore with real-time updates
     * Uses snapshot listener to automatically refresh UI when changes occur
     */
    private void loadComments() {
        CollectionReference commentsRef = db.collection("MoodEvents").document(postId).collection("Comments");
        commentsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) return;

                        ArrayList<Comment> newComments = new ArrayList<>();
                        if (snapshots != null) {
                            for (QueryDocumentSnapshot document : snapshots) {
                                Comment comment = document.toObject(Comment.class);
                                newComments.add(comment);
                            }
                        }

                        // Update the list and preserve reference
                        commentList.clear();
                        commentList.addAll(newComments);
                        commentsAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * Adds a new comment to Firestore database
     * @param text The comment text content from input field
     */
    private void addComment(String text) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String loggedInUsername = sharedPreferences.getString("username", null);
        Comment comment = new Comment(loggedInUsername, text, Timestamp.now());

        CollectionReference commentsRef = db.collection("MoodEvents").document(postId).collection("Comments");
        commentsRef.add(comment).addOnSuccessListener(documentReference -> {
            commentInput.setText("");
            Toast.makeText(getContext(), "Comment added", Toast.LENGTH_SHORT).show();
        });
    }
}