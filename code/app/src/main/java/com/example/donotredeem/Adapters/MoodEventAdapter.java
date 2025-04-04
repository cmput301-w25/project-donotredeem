package com.example.donotredeem.Adapters;

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
import com.example.donotredeem.Classes.MoodEvent;
import com.example.donotredeem.Fragments.CommentsFragment;
import com.example.donotredeem.Fragments.EditMoodEventFragment;
import com.example.donotredeem.Fragments.ViewMoodDialogFragment;
import com.example.donotredeem.Enumertions.MoodTypeEnum;
import com.example.donotredeem.MyApplication;
import com.example.donotredeem.R;
import com.example.donotredeem.Enumertions.SocialSituationEnum;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import github.com.st235.swipetoactionlayout.SwipeAction;
import github.com.st235.swipetoactionlayout.SwipeMenuListener;
import github.com.st235.swipetoactionlayout.SwipeToActionLayout;

/**
 * Adapter class for displaying MoodEvent objects in a ListView with swipe actions.
 * Features include:
 * - Displaying mood events with emotional state, time, date, and additional details
 * - Swipe-to-edit and swipe-to-delete functionality
 * - Integration with Firestore for data persistence
 * - Mood Jar integration for saving important events
 * - Visual feedback with Snackbars and error handling
 */
public class MoodEventAdapter extends ArrayAdapter<MoodEvent> {

    private Context context;
    private ArrayList<MoodEvent> Events;
    private FirebaseFirestore db;

    /**
     * Constructs a MoodEventAdapter with null-safe initialization
     * @param context The context of the calling activity/fragment
     * @param Events List of MoodEvent objects to display (uses empty list if null)
     */
    public MoodEventAdapter(Context context, ArrayList<MoodEvent> Events){
        super(context != null ? context : getFallbackContext(), 0, Events);
        this.context = context;
        this.Events = Events != null ? Events : new ArrayList<>();
    }

    /**
     * Provides fallback context for adapter initialization
     * @return Application context if activity context is unavailable
     */
    private static Context getFallbackContext() {
        // Provide a fallback context if needed
        return MyApplication.getInstance();
    }

    /**
     * Constructs and configures list item views with swipe actions
     * @param position Item position in the dataset
     * @param convertView Recycled view if available
     * @param parent Parent view group
     * @return Fully configured list item view
     */
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


        if (Current_Mood_Event != null) {
            ThisEmoState.setText(Current_Mood_Event.getEmotionalState());
            ThisTime.setText(Current_Mood_Event.getTime().toString());
            ThisDate.setText(Current_Mood_Event.getDate().toString());
            ThisTextDescription.setText(Current_Mood_Event.getExplainText());

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

        int situationId = SocialSituationEnum.getImageIdBySituation(situation);
        if (situationId != -1) {
            ThisSituation.setImageResource(situationId);
            ThisSituation.setVisibility(View.VISIBLE);
            Log.d("MoodAdapter", "Image set.");
        } else {
            ThisSituation.setVisibility(View.GONE);
        }

        int imageId = MoodTypeEnum.getImageIdByMood(mood);
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
                ViewMoodDialogFragment viewMoodDialogFragmentDialog = new ViewMoodDialogFragment();
                viewMoodDialogFragmentDialog.setArguments(bundle);

                // Show as a DialogFragment
                viewMoodDialogFragmentDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "ViewMoodDialog");
            }
        });
        //Store in moodJar
        ImageView jarButton = MainView.findViewById(R.id.jar_button);
        jarButton.setOnClickListener(v -> {
            MoodEvent currentEvent = getItem(position);
            if (currentEvent != null) {
                addToMoodJar(currentEvent, MainView.getRootView());
            }
        });

        //Comments
        ImageView commentIcon = MainView.findViewById(R.id.imageView10);
        commentIcon.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("postId", Current_Mood_Event.getMoodEventId());
            CommentsFragment commentsFragment = new CommentsFragment();
            commentsFragment.setArguments(bundle);
            commentsFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "CommentsFragment");
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

        EditMoodEventFragment editFragment = new EditMoodEventFragment();
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
        if (moodEvent.getLocation() != null) {
            args.putDoubleArray("locationpts", new double[]{
                    moodEvent.getLocation().getLatitude(),
                    moodEvent.getLocation().getLongitude()
            });}

        editFragment.setArguments(args);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, editFragment, "EditMoodEvent")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Removes mood event reference from all users' moodJar arrays.
     * @param moodEventId ID of the mood event to remove
     */
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

    /**
     * Adds mood event to user's Mood Jar collection.
     * @param moodEvent Event to add
     * @param view Anchor view for snackbar positioning
     */
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

    /**
     * Displays snackbar message with fallback to Toast.
     * @param anchorView View for snackbar positioning
     * @param message Message to display
     */
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