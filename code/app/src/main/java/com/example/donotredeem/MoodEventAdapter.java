package com.example.donotredeem;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URI;
import java.time.LocalTime;
import java.util.ArrayList;

import github.com.st235.swipetoactionlayout.SwipeAction;
import github.com.st235.swipetoactionlayout.SwipeMenuListener;
import github.com.st235.swipetoactionlayout.SwipeToActionLayout;

public class MoodEventAdapter extends ArrayAdapter<MoodEvent> {

    private Context context;
    private ArrayList<MoodEvent> Events;
    private FirebaseFirestore db;
    private SwipeToActionLayout swipeToActionLayout;

    public MoodEventAdapter(Context context, ArrayList<MoodEvent> Events){
        super(context,0,Events);
        this.context = context;
        this.Events = Events;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View MainView;
        if (convertView == null) {
            MainView = LayoutInflater.from(getContext()).inflate(R.layout.history_content, parent, false); }
        else {
            MainView = convertView;
        }


        MoodEvent Current_Mood_Event = getItem(position);

        ImageView Emoji = MainView.findViewById(R.id.emojiIcon);

        CardView Details = MainView.findViewById(R.id.details);

        TextView ThisEmoState = MainView.findViewById(R.id.Emotional_State);
        TextView ThisTime = MainView.findViewById(R.id.Specific_Time);
        TextView ThisDate = MainView.findViewById(R.id.Specific_Date);
        TextView ThisLocation = MainView.findViewById(R.id.Specific_Location);
        ImageView ThisSituation = MainView.findViewById(R.id.SituationImage);
        ThisSituation.setVisibility(View.VISIBLE);
        TextView ThisTrigger = MainView.findViewById(R.id.Specific_Trigger);
        TextView ThisTextDescription = MainView.findViewById(R.id.Additional_details);
        ImageView ThisPictureDescription = MainView.findViewById(R.id.timelineImage);


        if (Current_Mood_Event != null) {
            ThisEmoState.setText(Current_Mood_Event.getEmotionalState());
            ThisTime.setText(Current_Mood_Event.getTime().toString());
            ThisLocation.setText(Current_Mood_Event.getPlace());
            ThisDate.setText(Current_Mood_Event.getDate().toString());
            ThisTrigger.setText(Current_Mood_Event.getTrigger());
            ThisTextDescription.setText(Current_Mood_Event.getExplainText());
        }

        String imageUri = Current_Mood_Event.getExplainPicture();

        if (imageUri != null && !imageUri.isEmpty()) {
            // Use Glide to load the image into the ImageView
            Glide.with(getContext()) // 'context' could be your activity or fragment
                    .load(imageUri) // Load the image from the URL
                    .placeholder(R.drawable.rounded_background) // Optional: set a placeholder while loading
                    .error(R.drawable.rounded_background) // Optional: set an error image if the URL fails
                    .into(ThisPictureDescription); // Set the image into the ImageView
        } else {
            // Fallback to default image if no URL is provided
            ThisPictureDescription.setImageResource(R.drawable.rounded_background);
        }


//        if (imageUri != null) {
//            Log.d("image", "image uri is not null");
//            ThisPictureDescription.setImageURI(imageUri);
//        } else {
//            ThisPictureDescription.setImageResource(R.drawable.cat); // Default image
//        }

        String mood = Current_Mood_Event.getEmotionalState();
        String situation = Current_Mood_Event.getSituation();
        db = FirebaseFirestore.getInstance();


        int situationId = SocialSituation.getImageIdBySituation(situation);
        if (situationId != -1) {
            ThisSituation.setImageResource(situationId);
            Log.d("MoodAdapter", "Image set.");
        } else {
            ThisSituation.setVisibility(View.GONE);  // Use a fallback
        }

        int imageId = MoodType.getImageIdByMood(mood);
        Emoji.setImageResource(imageId);
        int colorId = context.getResources().getIdentifier(mood, "color", context.getPackageName());

        if (colorId != 0) { // Ensure the color resource exists
            int color = ContextCompat.getColor(context, colorId);
            Details.setCardBackgroundColor(color);
        } else {
            Details.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white)); // Set fallback color
        }

        if (Current_Mood_Event.getExplainPicture() != null) {
            // If picture description is available, show the image
            ThisPictureDescription.setVisibility(View.VISIBLE);
            // Set the image resource if available
            ThisPictureDescription.setImageResource(R.drawable.sad); // Use actual image from the data if needed
        } else {
            // Hide the image if there's no picture description
            ThisPictureDescription.setVisibility(View.GONE);
        }

        if(Current_Mood_Event.getExplainText() != null){
            ThisTextDescription.setVisibility(View.VISIBLE);
        }
        else {
            // Hide the image if there's no picture description
            ThisTextDescription.setVisibility(View.GONE);
        }

//BITMAP IMAGES NOT WORKING WE NEED TO CHANGE TO URI EVERYWHERE
//        if (Current_Mood_Event.getImageUri() != null && !Current_Mood_Event.getImageUri().isEmpty()) {
//            ThisPictureDescription.setVisibility(View.VISIBLE);
//            ThisPictureDescription.setImageURI(Uri.parse(Current_Mood_Event.getImageUri()));
//        } else {
//            ThisPictureDescription.setVisibility(View.GONE);
//        }

        SwipeToActionLayout swipeLayout = MainView.findViewById(R.id.swipe_layout);

        // Set the menu listener on the swipe layout
        swipeLayout.setMenuListener(new SwipeMenuListener() {
            @Override
            public void onClosed(View view) {
                // No action needed
            }

            @Override
            public void onOpened(View view) {
                // No action needed
            }

            @Override
            public void onFullyOpened(View view, SwipeAction quickAction) {
                // No action needed
            }

            @Override
            public void onActionClicked(View view, SwipeAction action) {
                // Check by comparing the text instead of using getId()
                if ("delete".equalsIgnoreCase(action.getText().toString())) {
                    // Get the mood event corresponding to this item
                    MoodEvent moodEvent = getItem(position);

                    // Remove the mood event from the list and notify adapter
                    Events.remove(position);
                    notifyDataSetChanged();

                    // Delete the mood event from Firestore and update User's MoodRef array
                    deleteMoodEventFromFirestore(moodEvent);

                    Toast.makeText(context, "Mood event deleted", Toast.LENGTH_SHORT).show();
                } else if ("edit".equalsIgnoreCase(action.getText().toString())) {
                    // Handle edit action if needed.
                    Toast.makeText(context, "Edit action clicked", Toast.LENGTH_SHORT).show();
                }
                // Close the swipe menu
                swipeLayout.close();
            }
        });



        return MainView;

    }
    private void deleteMoodEventFromFirestore(MoodEvent moodEvent) {
        if (moodEvent != null && moodEvent.getMoodEventId() != null) {
            String moodEventId = moodEvent.getMoodEventId();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("MoodEvents").document(moodEventId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("MoodEventAdapter", "Deleted MoodEvent with ID: " + moodEventId);
                        // Optionally, remove the reference from the user's document
                        removeMoodRefFromUser(moodEventId);
                    })
                    .addOnFailureListener(e -> Log.e("MoodEventAdapter", "Error deleting MoodEvent", e));
        } else {
            Log.e("MoodEventAdapter", "MoodEvent or moodEventId is null");
        }
    }


    private void removeMoodRefFromUser(String moodEventId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        if (username == null) {
            Log.e("MoodEventAdapter", "Username not found in SharedPreferences");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference moodEventRef = db.collection("MoodEvents").document(moodEventId); // Get the actual DocumentReference

        db.collection("User")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                        DocumentReference userDocRef = userDoc.getReference();

                        // Remove the correct DocumentReference, not the String ID
                        userDocRef.update("MoodRef", FieldValue.arrayRemove(moodEventRef))
                                .addOnSuccessListener(aVoid -> Log.d("MoodEventAdapter", "Removed MoodEvent reference from user document"))
                                .addOnFailureListener(e -> Log.e("MoodEventAdapter", "Error updating user document", e));
                    } else {
                        Log.e("MoodEventAdapter", "No user document found for username: " + username);
                    }
                })
                .addOnFailureListener(e -> Log.e("MoodEventAdapter", "Error retrieving user document", e));
    }


}