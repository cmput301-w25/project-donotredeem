//package com.example.donotredeem;
//
//import android.content.Context;
//import android.graphics.Movie;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextClock;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import java.util.ArrayList;
//
//public class MoodEventAdapter extends ArrayAdapter<MoodEvent> {
//
//    private Context context;
//    private ArrayList<MoodEvent> Events;
//
//    public MoodEventAdapter(Context context, ArrayList<MoodEvent> Events){
//        super(context,0,Events);
//        this.context = context;
//        this.Events = Events;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
//
//        View MainView;
//        if (convertView == null) {
//            MainView = LayoutInflater.from(getContext()).inflate(R.layout.content, parent, false); }
//        else {
//            MainView = convertView;
//        }
//
//        MoodEvent Current_Mood_Event = getItem(position);
//
//        TextView ThisEmoState = MainView.findViewById(R.id.);
//        TextView ThisTime = MainView.findViewById();
//        TextView ThisDate = MainView.findViewById();
//        TextView ThisLocation = MainView.findViewById();
//        TextView ThisSituation = MainView.findViewById();
//        TextView ThisTrigger = MainView.findViewById();
//        TextView ThisTextDescription = MainView.findViewById();
//        ImageView ThisPictureDescription = MainView.findViewById();
//
//
//        if (Current_Mood_Event != null) {
//            ThisDate.setText("placeholder");
//            ThisLocation.setText("placeholder");
//            ThisTextDescription.setText("placeholder");
//            ThisTime.setText("placeholder");
//            ThisEmoState.setText("placeholder");
//            ThisSituation.setText("placeholder");
//            ThisTrigger.setText("placeholder");
//            ThisPictureDescription.setImageDrawable();
//        }
//
//        return MainView;
//
//    }
//}
