package com.example.donotredeem;

import android.content.Context;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

        return MainView;

    }
}
