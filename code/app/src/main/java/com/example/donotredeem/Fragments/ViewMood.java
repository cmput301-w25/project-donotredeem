package com.example.donotredeem.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;


import com.bumptech.glide.Glide;
import com.example.donotredeem.MoodType;
import com.example.donotredeem.R;
import com.example.donotredeem.SocialSituation;


/**
 * Dialog fragment displaying detailed view of a mood event.
 *
 * Shows comprehensive information including:
 * - Emotional state with color-coded background
 * - Date/time and location details
 * - Social situation context
 * - Mood triggers and explanations
 * - Associated images and privacy status
 *
 * Handles dynamic UI element visibility based on data availability
 */
public class ViewMood extends DialogFragment {

    /**
     * Creates and configures the dialog view with mood event details
     *
     * @param inflater LayoutInflater to inflate XML layout
     * @param container Parent view group for fragment's UI
     * @param savedInstanceState Saved state bundle
     * @return Configured view displaying mood event details
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_mood, container, false);

        // Retrieve arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            String emotionalState = arguments.getString("emotionalState");
            String date = arguments.getString("date");
            String time = arguments.getString("time");
            String place = arguments.getString("place");
            String situation = arguments.getString("situation");
            String trigger = arguments.getString("trigger");
            String explainText = arguments.getString("explainText");
            String explainPictureURI = arguments.getString("explainPicture");
            Boolean privacy = arguments.getBoolean("privacy");
            Boolean prev_frag = arguments.getBoolean("fragment");



            // Initialize UI elements
            TextView moodText = view.findViewById(R.id.View_Emotional_State);
            ImageView Emoji = view.findViewById(R.id.emojiIcon);
            TextView dateText = view.findViewById(R.id.View_Specific_Date);
            TextView timeText = view.findViewById(R.id.View_Specific_Time);
            TextView placeText = view.findViewById(R.id.View_Specific_Location);
            ImageView situationImage = view.findViewById(R.id.View_SituationImage);
            TextView triggerText = view.findViewById(R.id.View_Specific_Trigger);
            TextView explainTextView = view.findViewById(R.id.View_Additional_details);
            ImageView explainPictureView = view.findViewById(R.id.View_timelineImage);
            Button closeButton = view.findViewById(R.id.closeButton);
            TextView situationText = view.findViewById(R.id.View_Situation_Text);
            TextView privacyText = view.findViewById(R.id.View_Privacy_Text);
            ImageView privacyImage = view.findViewById(R.id.View_Privacy_Image);

            ImageView descIcon = view.findViewById(R.id.DescIcon);
            ImageView locationIcon = view.findViewById(R.id.LocationIcon);
            ImageView reasonIcon = view.findViewById(R.id.ReasonIcon);




            if (prev_frag){privacyImage.setVisibility(View.VISIBLE);
            privacyText.setVisibility(View.VISIBLE);}
            else
            {privacyImage.setVisibility(View.INVISIBLE);
            privacyText.setVisibility(View.INVISIBLE);}

            // Set text values
            moodText.setText(emotionalState);
            dateText.setText(date);
            timeText.setText(time);
            placeText.setText(place);
            situationText.setText(situation);
            triggerText.setText(trigger);
            explainTextView.setText(explainText);

            if (privacy) {
                privacyText.setText("This Mood Event is Private ");
                privacyImage.setImageResource(R.drawable.locked);}
            else
            {
                privacyText.setText("This Mood Event is Public");
                privacyImage.setImageResource(R.drawable.unlocked);
            }


            // Load image (if available)
            if (explainPictureURI != null && !explainPictureURI.isEmpty()) {
                Glide.with(requireContext())
                        .load(explainPictureURI)
                        .placeholder(R.drawable.rounded_background)
                        .error(R.drawable.rounded_background)
                        .into(explainPictureView);
                explainPictureView.setVisibility(View.VISIBLE);
            } else {
                explainPictureView.setVisibility(View.GONE);
            }

            int imageId = MoodType.getImageIdByMood(emotionalState);
            Emoji.setImageResource(imageId);

            int colorId = requireContext().getResources().getIdentifier(emotionalState, "color", requireContext().getPackageName());
            // Set background color based on emotional state
            if (colorId != 0) { // Ensure the color resource exists
                int color = ContextCompat.getColor(requireContext(), colorId);
                view.setBackgroundColor(color);
                moodText.setTextColor(color);
                closeButton.setBackgroundColor(color);
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.white));
                moodText.setTextColor(getResources().getColor(R.color.white));
            }

            // Set social situation image
            int situationId = SocialSituation.getImageIdBySituation(situation);
            if (situationId != -1) {
                situationImage.setImageResource(situationId);
                situationText.setVisibility(View.VISIBLE);
                situationImage.setVisibility(View.VISIBLE);
            } else {
                situationText.setVisibility(View.GONE);
                situationImage.setVisibility(View.GONE);
            }

            if (explainText != "") {
                explainTextView.setVisibility(View.VISIBLE);
                descIcon.setVisibility(View.VISIBLE);
            } else if (explainText == ""){
                explainTextView.setVisibility(View.GONE);
                descIcon.setVisibility(View.GONE);
            }

            if (trigger != "") {
                triggerText.setVisibility(View.VISIBLE);
                reasonIcon.setVisibility(View.VISIBLE);
            } else if (trigger == ""){
                triggerText.setVisibility(View.GONE);
                reasonIcon.setVisibility(View.GONE);
            }
            if (place != "") {
                placeText.setVisibility(View.VISIBLE);
                locationIcon.setVisibility(View.VISIBLE);
            } else if (place == "") {
                placeText.setVisibility(View.GONE);
                locationIcon.setVisibility(View.GONE);
            }

            // Close button functionality
            closeButton.setOnClickListener(v -> dismiss());
        }

        return view;
    }

    /**
     * Configures dialog window dimensions on startup
     *
     * Ensures proper sizing to accommodate dynamic content
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}

