package com.example.donotredeem.Fragments;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.Glide;
import com.example.donotredeem.MoodType;
import com.example.donotredeem.R;
import com.example.donotredeem.SocialSituation;



public class ViewMood extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view, container, false);

        LinearLayout roundedLayout = view.findViewById(R.id.rounded_bg);
        Drawable drawable = roundedLayout.getBackground();

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

            // Set text values
            moodText.setText(emotionalState);
            dateText.setText(date);
            timeText.setText(time);
            placeText.setText(place);
            triggerText.setText(trigger);
            explainTextView.setText(explainText);

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
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.white));
            }

            // Set social situation image
            int situationId = SocialSituation.getImageIdBySituation(situation);
            if (situationId != -1) {
                situationImage.setImageResource(situationId);
            } else {
                situationImage.setVisibility(View.GONE);
            }

            // Hide views if data is missing
            explainTextView.setVisibility(explainText != null ? View.VISIBLE : View.GONE);

            // Close button functionality
            closeButton.setOnClickListener(v -> dismiss());
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}

