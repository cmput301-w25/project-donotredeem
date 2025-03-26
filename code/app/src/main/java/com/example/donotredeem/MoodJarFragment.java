package com.example.donotredeem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MoodJarFragment extends Fragment {
    private ImageView jarImage;
    private Button addNoteButton;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private String currentUsername;
    private int noteCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mood_jar, container, false);

        jarImage = view.findViewById(R.id.jar_image);
        addNoteButton = view.findViewById(R.id.add_note_button);

        // Initialize Firestore and SharedPreferences
        db = FirebaseFirestore.getInstance();
        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);

        loadNotes();
        setupButton();

        return view;
    }

    private void setupButton() {
        addNoteButton.setOnClickListener(v -> showAddNoteDialog());
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Positive Note");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String note = input.getText().toString().trim();
            if (!note.isEmpty()) {
                saveNoteToFirestore(note);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveNoteToFirestore(String note) {
        if (currentUsername == null) return;

        Map<String, Object> noteData = new HashMap<>();
        noteData.put("text", note);
        noteData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("User")
                .document(currentUsername)
                .collection("moodJar")
                .add(noteData)
                .addOnSuccessListener(documentReference -> {
                    noteCount++;
                    updateJarImage();
                    Toast.makeText(requireContext(), "Note added!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error saving note", Toast.LENGTH_SHORT).show());
    }

    private void loadNotes() {
        if (currentUsername == null) return;

        db.collection("User")
                .document(currentUsername)
                .collection("moodJar")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    noteCount = queryDocumentSnapshots.size();
                    updateJarImage();
                });
    }

    private void updateJarImage() {
        // Update jar image based on note count
        if (noteCount < 3) {
            jarImage.setImageResource(R.drawable.jar_empty);
        } else if (noteCount < 6) {
            jarImage.setImageResource(R.drawable.jar_quarter);
        } else if (noteCount < 9) {
            jarImage.setImageResource(R.drawable.jar_half);
        } else {
            jarImage.setImageResource(R.drawable.jar_full);
        }
    }
}