package com.example.donotredeem;

import android.content.Context;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MoodEventAdapter extends ArrayAdapter<MoodEvent> {

    private Context context;
    private ArrayList<MoodEvent> Events;

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
//        TextView ThisSituation = MainView.findViewById();
        TextView ThisTrigger = MainView.findViewById(R.id.Specific_Trigger);
        TextView ThisTextDescription = MainView.findViewById(R.id.Additional_details);
        ImageView ThisPictureDescription = MainView.findViewById(R.id.timelineImage);


        if (Current_Mood_Event != null) {
            ThisEmoState.setText(Current_Mood_Event.getEmotionalState());
            ThisTime.setText(Current_Mood_Event.getTime().toString());
            ThisDate.setText(Current_Mood_Event.getDate().toString());
            ThisTrigger.setText(Current_Mood_Event.getTrigger());
            ThisTextDescription.setText(Current_Mood_Event.getExplainText());
        }

        String mood = Current_Mood_Event.getEmotionalState();
        int imageId = MoodType.getImageIdByMood(mood);
        Emoji.setImageResource(imageId);
        int colorId = context.getResources().getIdentifier(mood, "color", context.getPackageName());

        if (colorId != 0) { // Ensure the color resource exists
            int color = ContextCompat.getColor(context, colorId);
            Details.setCardBackgroundColor(color);
        } else {
            Details.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white)); // Set fallback color
        }



        return MainView;

    }
}
