package com.example.donotredeem.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.donotredeem.Fragments.CommentsFragment;
import com.example.donotredeem.Fragments.ViewMoodDialogFragment;
import com.example.donotredeem.Classes.MoodEvent;
import com.example.donotredeem.Enumertions.MoodTypeEnum;
import com.example.donotredeem.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for displaying MoodEvents in a main feed/list view.
 * Features include:
 * - Displaying public mood events with user details
 * - Profile picture loading from Firestore
 * - Clickable elements for detailed views and comments
 * - Visual representation of mood states with colors and icons
 */
public class MainPageAdapter extends ArrayAdapter<MoodEvent> {
    private Context context;
    private ArrayList<MoodEvent> Events;
    private FirebaseFirestore db;

    /**
     * Constructs a MainPageAdapter for displaying mood events
     * @param context The host activity context
     * @param Events List of MoodEvent objects to display
     */
    public MainPageAdapter(Context context, ArrayList<MoodEvent> Events){
        super(context,0,Events);
        this.context = context;
        this.Events = Events;
    }

    /**
     * Creates and configures views for each mood event in the list
     * @param position Item position in dataset
     * @param convertView Recycled view if available
     * @param parent Parent view group
     * @return Configured view for mood event display
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View MainView;
        if (convertView == null) {
            MainView = LayoutInflater.from(getContext()).inflate(R.layout.main_page_content, parent, false); }
        else {
            MainView = convertView;
        }

        db = FirebaseFirestore.getInstance();

        MoodEvent Current_Mood_Event = getItem(position);
        ImageView Emoji = MainView.findViewById(R.id.main_page_emoji);

        TextView UserName = MainView.findViewById(R.id.username);

        CardView Details = MainView.findViewById(R.id.main_page_details);

        TextView MainEmoState = MainView.findViewById(R.id.main_mood_text);
        TextView MainTime = MainView.findViewById(R.id.main_time);
        TextView MainDate = MainView.findViewById(R.id.main_date);
        TextView MainLocation = MainView.findViewById(R.id.main_location);
        TextView MainTextDescription = MainView.findViewById(R.id.main_text_desc);
        ImageView MainPictureDescription = MainView.findViewById(R.id.main_picture_desc);

        CircleImageView profilePic = MainView.findViewById(R.id.pfp_icon);


        if (Current_Mood_Event != null) {
            MainEmoState.setText(Current_Mood_Event.getEmotionalState());
            MainTime.setText(Current_Mood_Event.getTime().toString());
            MainLocation.setText(Current_Mood_Event.getPlace());
            UserName.setText(Current_Mood_Event.getUsername());
            MainDate.setText(Current_Mood_Event.getDate().toString());
            MainTextDescription.setText(Current_Mood_Event.getExplainText());

            loadProfilePicture(Current_Mood_Event.getUsername(), profilePic);

        }

        String imageUri = Current_Mood_Event.getExplainPicture();

        if (imageUri != null && !imageUri.isEmpty()) {
            // Use Glide to load the image into the ImageView
            Glide.with(getContext()) // 'context' could be your activity or fragment
                    .load(imageUri) // Load the image from the URL
                    .placeholder(R.drawable.rounded_background) // Optional: set a placeholder while loading
                    .error(R.drawable.rounded_background) // Optional: set an error image if the URL fails
                    .into(MainPictureDescription); // Set the image into the ImageView
        } else {
            // Fallback to default image if no URL is provided
            MainPictureDescription.setImageResource(R.drawable.rounded_background);
        }


        int imageId = MoodTypeEnum.getImageIdByMood(Current_Mood_Event.getEmotionalState());
        Emoji.setImageResource(imageId);
        int colorId = context.getResources().getIdentifier(Current_Mood_Event.getEmotionalState(), "color", context.getPackageName());

        if (colorId != 0) { // Ensure the color resource exists
            int color = ContextCompat.getColor(context, colorId);
            Details.setCardBackgroundColor(color);
        } else {
            Details.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white)); // Set fallback color
        }

        if (Current_Mood_Event.getExplainPicture() != null) {
            // If picture description is available, show the image
            MainPictureDescription.setVisibility(View.VISIBLE);
            // Set the image resource if available
        } else {
            // Hide the image if there's no picture description
            MainPictureDescription.setVisibility(View.GONE);
        }

        if(Current_Mood_Event.getExplainText() != null){
            MainTextDescription.setVisibility(View.VISIBLE);
        }
        else {
            // Hide the image if there's no picture description
            MainTextDescription.setVisibility(View.GONE);
        }


        Details.setOnClickListener(v -> {
            if (Current_Mood_Event != null) {
                Bundle bundle = new Bundle();

                bundle.putString("emotionalState", Current_Mood_Event.getEmotionalState());
                bundle.putString("date", Current_Mood_Event.getDate().toString());
                bundle.putString("time", Current_Mood_Event.getTime().toString());
                bundle.putString("place", Current_Mood_Event.getPlace());
                bundle.putString("situation", Current_Mood_Event.getSituation());
                bundle.putString("trigger", Current_Mood_Event.getTrigger());
                bundle.putString("explainText", Current_Mood_Event.getExplainText());
                bundle.putString("explainPicture", imageUri);
                bundle.putBoolean("fragment",Current_Mood_Event.getPrivacy());

                // Create the DialogFragment instance and set arguments
                ViewMoodDialogFragment viewMoodDialogFragmentDialog = new ViewMoodDialogFragment();
                viewMoodDialogFragmentDialog.setArguments(bundle);

                // Show as a DialogFragment
                viewMoodDialogFragmentDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "ViewMoodDialog");
            }
        });

        ImageView commentIcon = MainView.findViewById(R.id.comment_icon);
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
     * Loads user profile picture from Firestore
     * @param username User to load picture for
     * @param imageView Target view to display the picture
     */
    private void loadProfilePicture(String username, CircleImageView imageView) {
        db.collection("User")
                .document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String pfpUrl = documentSnapshot.getString("pfp");
                        if (pfpUrl != null && !pfpUrl.isEmpty()) {
                            Glide.with(imageView.getContext())
                                    .load(pfpUrl)
                                    .placeholder(R.drawable.ic_account_circle)
                                    .error(R.drawable.ic_account_circle)
                                    .into(imageView);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    imageView.setImageResource(R.drawable.ic_account_circle);
                });
    }


}
