package com.example.donotredeem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.donotredeem.Fragments.EditMoodEvent;
import com.example.donotredeem.Fragments.ViewMood;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.com.st235.swipetoactionlayout.SwipeAction;
import github.com.st235.swipetoactionlayout.SwipeMenuListener;
import github.com.st235.swipetoactionlayout.SwipeToActionLayout;
/**
 * Adapter class for displaying MoodEvent objects in a ListView.
 * Supports swipe actions for editing and deleting events.
 */
public class MoodEventAdapter extends ArrayAdapter<MoodEvent> {

    private Context context;
    private ArrayList<MoodEvent> Events;
    private FirebaseFirestore db;
    private SwipeToActionLayout swipeToActionLayout;
    /**
     * Constructor for MoodEventAdapter.
     * @param context The context of the activity or fragment.
     * @param Events The list of MoodEvent objects to display.
     */
    public MoodEventAdapter(Context context, ArrayList<MoodEvent> Events){
        super(context != null ? context : getFallbackContext(), 0, Events);
        this.context = context;
        this.Events = Events != null ? Events : new ArrayList<>();
//        this.Events = Events;
    }
    private static Context getFallbackContext() {
        // Provide a fallback context if needed
        return MyApplication.getInstance();
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
        ImageView ThisSituation = MainView.findViewById(R.id.SituationImage);
        TextView ThisTextDescription = MainView.findViewById(R.id.Additional_details);
        ImageView ThisPictureDescription = MainView.findViewById(R.id.timelineImage);
        ImageView ThisPrivacyImage = MainView.findViewById(R.id.PrivacyImage);
//        TextView ThisSituationText = MainView.findViewById(R.id.Situation_text);
//        TextView ThisTrigger = MainView.findViewById(R.id.Specific_Trigger);
//        TextView ThisLocation = MainView.findViewById(R.id.Specific_Location);


        if (Current_Mood_Event != null) {
            ThisEmoState.setText(Current_Mood_Event.getEmotionalState());
            ThisTime.setText(Current_Mood_Event.getTime().toString());
            ThisDate.setText(Current_Mood_Event.getDate().toString());
            ThisTextDescription.setText(Current_Mood_Event.getExplainText());

//            ThisTrigger.setText(Current_Mood_Event.getTrigger());
//            ThisSituationText.setText(Current_Mood_Event.getSituation());
//            ThisLocation.setText(Current_Mood_Event.getPlace());
        }

        String imageUri = Current_Mood_Event.getExplainPicture();
        String mood = Current_Mood_Event.getEmotionalState();
        String situation = Current_Mood_Event.getSituation();
        Boolean privacy = Current_Mood_Event.getPrivacy();
        db = FirebaseFirestore.getInstance();

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


        if (privacy){ThisPrivacyImage.setImageResource(R.drawable.locked);}
        else {ThisPrivacyImage.setImageResource(R.drawable.unlocked);}

        int situationId = SocialSituation.getImageIdBySituation(situation);
        if (situationId != -1) {
            ThisSituation.setImageResource(situationId);
            ThisSituation.setVisibility(View.VISIBLE);
            Log.d("MoodAdapter", "Image set.");
        } else {
            ThisSituation.setVisibility(View.GONE);
//            ThisSituationText.setVisibility(View.GONE);// Use a fallback
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

                    //Toast.makeText(context, "Mood event deleted", Toast.LENGTH_SHORT).show();
                    Snackbar.make(MainView, "Mood event deleted", Snackbar.LENGTH_SHORT).show();

                } else if ("edit".equalsIgnoreCase(action.getText().toString())) {
                    // Handle edit action if needed.
                    MoodEvent moodEvent = getItem(position);
                    launchEditMoodEventFragment(moodEvent);
                }
                // Close the swipe menu
                swipeLayout.close();
            }
        });

        Details.setOnClickListener(v -> {
            if (Current_Mood_Event != null) {
                Bundle bundle = new Bundle();

                bundle.putString("emotionalState", mood);
                bundle.putString("date", Current_Mood_Event.getDate().toString());
                bundle.putString("time", Current_Mood_Event.getTime().toString());
                bundle.putString("place", Current_Mood_Event.getPlace());
                bundle.putString("situation", situation);
                bundle.putString("trigger", Current_Mood_Event.getTrigger());
                bundle.putString("explainText", Current_Mood_Event.getExplainText());
                bundle.putString("explainPicture", imageUri);
                bundle.putBoolean("privacy",privacy);
                bundle.putBoolean("fragment",true);

                // Create the DialogFragment instance and set arguments
                ViewMood viewMoodDialog = new ViewMood();
                viewMoodDialog.setArguments(bundle);

                // Show as a DialogFragment
                viewMoodDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "ViewMoodDialog");
            }
        });
        //Store in moodJar
        ImageView jarButton = MainView.findViewById(R.id.jar_button);
//        jarButton.setOnClickListener(v -> {
//            MoodEvent currentEvent = getItem(position);
//            if (currentEvent != null) {
//                addToMoodJar(currentEvent, MainView);
//            }
//        });
        jarButton.setOnClickListener(v -> {
            MoodEvent currentEvent = getItem(position);
            if (currentEvent != null) {
                addToMoodJar(currentEvent, MainView.getRootView());
            }
        });

        return MainView;

    }
    /**
     * Deletes a mood event from Firestore.
     * @param moodEvent The mood event to be deleted.
     */
    private void deleteMoodEventFromFirestore(MoodEvent moodEvent) {
        if (moodEvent != null && moodEvent.getMoodEventId() != null) {
            String moodEventId = moodEvent.getMoodEventId();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("MoodEvents").document(moodEventId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("MoodEventAdapter", "Deleted MoodEvent with ID: " + moodEventId);
//                            db.collection("User")
//                                .whereEqualTo("username", username)
//                                .get()
//                                    .addOnSuccessListener(queryDocumentSnapshots -> {
//                                                DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
//
//                                                DocumentReference userDocRef = userDoc.getReference();
//                                                userDocRef.update("moods", FieldValue.increment(-1));
//                                            })
                        // Optionally, remove the reference from the user's document
                        removeMoodRefFromUser(moodEventId);
                        removeFromAllMoodJars(moodEventId);

                    })
                    .addOnFailureListener(e -> Log.e("MoodEventAdapter", "Error deleting MoodEvent", e));
        } else {
            Log.e("MoodEventAdapter", "MoodEvent or moodEventId is null");
        }
    }

    /**
     * Removes the mood event reference from the user's document.
     * @param moodEventId The ID of the mood event to be removed.
     */
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
                        userDocRef.update("moods", FieldValue.increment(-1));
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
    /**
     * Launches the EditMoodEvent fragment with the selected mood event's details.
     * @param moodEvent The mood event to be edited.
     */
    private void launchEditMoodEventFragment(MoodEvent moodEvent) {
        AppCompatActivity activity = (AppCompatActivity) context;

        EditMoodEvent editFragment = new EditMoodEvent();
        Bundle args = new Bundle();

        // Pass MoodEvent data individually as Strings
        args.putString("moodEventId", moodEvent.getMoodEventId());
        args.putString("emotionalState", moodEvent.getEmotionalState());
        args.putString("date", moodEvent.getDate());
        args.putString("time", moodEvent.getTime());
        args.putString("place", moodEvent.getPlace());
        args.putString("situation", moodEvent.getSituation());
        args.putString("trigger", moodEvent.getTrigger());
        args.putString("explainText", moodEvent.getExplainText());
        args.putString("explainPicture", moodEvent.getExplainPicture());
        args.putBoolean("privacy",moodEvent.getPrivacy());

        editFragment.setArguments(args);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, editFragment, "EditMoodEvent")
                .addToBackStack(null)
                .commit();
    }

//    private void addToMoodJar(MoodEvent moodEvent, View view) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
//        String username = sharedPreferences.getString("username", null);
//
//        if (username == null) {
////            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
//            Snackbar.make(view, "User not logged in", Snackbar.LENGTH_SHORT).show();
//
//            return;
//        }
//
//        if (moodEvent.getMoodEventId() == null) {
////            Toast.makeText(context, "Mood event has no ID", Toast.LENGTH_SHORT).show();
//            Snackbar.make(view, "Mood event has no ID", Snackbar.LENGTH_SHORT).show();
//
//            return;
//        }
//
//        DocumentReference moodEventRef = db.collection("MoodEvents").document(moodEvent.getMoodEventId());
//
////        db.collection("User").document(username)
////                .get()
////                .addOnSuccessListener(documentSnapshot -> {
////                    if (documentSnapshot.exists()) {
////                        List<DocumentReference> moodJar = (List<DocumentReference>) documentSnapshot.get("moodJar");
////
////                        if (moodJar != null && moodJar.contains(moodEventRef)) {
////                            Snackbar.make(view,"Mood event already exists in Mood Jar!", Snackbar.LENGTH_SHORT).show();
////                        } else {
////                            // Add the mood event if it doesn't already exist
////                            db.collection("User").document(username)
////                                    .update("moodJar", FieldValue.arrayUnion(moodEventRef))
////                                    .addOnSuccessListener(aVoid ->
////                                            Snackbar.make(view, "Added to Mood Jar!", Snackbar.LENGTH_SHORT).show()
////                                    )
////                                    .addOnFailureListener(e ->
////                                            Snackbar.make(view, "Failed to add: " + e.getMessage(), Snackbar.LENGTH_SHORT).show()
////
////                                    );
////                        }
////                    } else {
////                        Snackbar.make(view, "User document does not exist!", Snackbar.LENGTH_SHORT).show();
////                    }
////                })
////                .addOnFailureListener(e ->
////                        Snackbar.make(view,"Failed to retrieve user data: " + e.getMessage(), Snackbar.LENGTH_SHORT).show()
////                );
//        db.collection("User").document(username)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        List<DocumentReference> moodJar = (List<DocumentReference>) documentSnapshot.get("moodJar");
//
//                        // Get valid view for Snackbar
//                        View rootView;
//                        if (context instanceof AppCompatActivity) {
//                            rootView = ((AppCompatActivity) context).getWindow().getDecorView().findViewById(android.R.id.content);
//                        } else {
//                            rootView = null;
//                        }
//
//                        if (moodJar != null && moodJar.contains(moodEventRef)) {
//                            showSnackbar(rootView, "Already in Mood Jar!");
//                        } else {
//                            db.collection("User").document(username)
//                                    .update("moodJar", FieldValue.arrayUnion(moodEventRef))
//                                    .addOnSuccessListener(aVoid ->
//                                            showSnackbar(rootView, "Added to Mood Jar!")
//                                    )
//                                    .addOnFailureListener(e ->
//                                            showSnackbar(rootView, "Failed: " + e.getMessage())
//                                    );
//                        }
//                    }
//                })
//                .addOnFailureListener(e ->
//                        showSnackbar(view, "Failed: " + e.getMessage())
//                );
//
//    }

    private void removeFromAllMoodJars(String moodEventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference moodEventRef = db.collection("MoodEvents").document(moodEventId);

        db.collection("User")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot userDoc : queryDocumentSnapshots.getDocuments()) {
                        DocumentReference userDocRef = userDoc.getReference();

                        // Remove the MoodEvent reference from the moodJar array
                        userDocRef.update("moodJar", FieldValue.arrayRemove(moodEventRef))
                                .addOnSuccessListener(aVoid ->
                                        Log.d("MoodEventAdapter", "Removed MoodEvent from moodJar of user: " + userDoc.getId()))
                                .addOnFailureListener(e ->
                                        Log.e("MoodEventAdapter", "Failed to remove from moodJar", e));
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("MoodEventAdapter", "Failed to query users for moodJar cleanup", e)
                );
    }
//    private void showSnackbar(View anchorView, String message) {
//        if (anchorView == null || !(context instanceof AppCompatActivity)) return;
//
//        try {
//            Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content),
//                            message, Snackbar.LENGTH_SHORT)
//                    .setAnchorView(anchorView)
//                    .show();
//        } catch (Exception e) {
//            Log.e("SnackbarError", "Failed to show snackbar", e);
//        }
//    }
private void addToMoodJar(MoodEvent moodEvent, View view) {
    if (context == null || moodEvent == null) return;

    SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
    String username = sharedPreferences != null ?
            sharedPreferences.getString("username", null) : null;

    if (username == null) {
        showSnackbar(null, "User not logged in");
        return;
    }

    String moodEventId = moodEvent.getMoodEventId();
    if (moodEventId == null) {
        showSnackbar(null, "Mood event has no ID");
        return;
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference moodEventRef = db.collection("MoodEvents").document(moodEventId);

    db.collection("User").document(username)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    List<DocumentReference> moodJar = null;
                    try {
                        moodJar = (List<DocumentReference>) documentSnapshot.get("moodJar");
                    } catch (RuntimeException e) {
                        Log.e("MoodEventAdapter", "Error getting moodJar", e);
                    }

                    View rootView;
                    if (context instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) context;
                        if (activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
                            rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                        } else {
                            rootView = null;
                        }
                    } else {
                        rootView = null;
                    }

                    if (moodJar != null && moodJar.contains(moodEventRef)) {
                        showSnackbar(null, "Already in Mood Jar!");
                    } else {
                        db.collection("User").document(username)
                                .update("moodJar", FieldValue.arrayUnion(moodEventRef))
                                .addOnSuccessListener(aVoid ->
                                        showSnackbar(null, "Added to Mood Jar!"))
                                .addOnFailureListener(e ->
                                        showSnackbar(null, "Failed: " + e.getMessage()));
                    }
                }
            })
            .addOnFailureListener(e ->
                    showSnackbar(null, "Failed: " + e.getMessage()));
}

//    private void showSnackbar(View anchorView, String message) {
//        if (context == null || !(context instanceof AppCompatActivity)) return;
//
//        AppCompatActivity activity = (AppCompatActivity) context;
//        View rootView = activity.findViewById(android.R.id.content);
//        if (rootView == null) return;
//
//        try {
//            Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
//            if (anchorView != null) {
//                snackbar.setAnchorView(anchorView);
//            }
//            snackbar.show();
//        } catch (Exception e) {
//            Log.e("SnackbarError", "Failed to show snackbar", e);
//        }
//    }
private void showSnackbar(View anchorView, String message) {
    if (context == null || !(context instanceof AppCompatActivity)) {
        return;
    }

    AppCompatActivity activity = (AppCompatActivity) context;

    // Check if activity is finishing or already destroyed
    if (activity.isFinishing() || activity.isDestroyed()) {
        return;
    }

    // Get the activity's root view
    View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
    if (rootView == null) {
        return;
    }

    try {
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);

        // Only set anchor view if it's valid and visible
        if (anchorView != null &&
                anchorView.isShown() &&
                anchorView.getWindowToken() != null) {
            snackbar.setAnchorView(anchorView);
        }

        snackbar.show();
    } catch (Exception e) {
        Log.e("SnackbarError", "Failed to show snackbar", e);
        // Fallback to Toast
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
}