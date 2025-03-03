package com.example.donotredeem.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.R;

import java.util.ArrayList;

public class FilterFragment extends DialogFragment {
    private Button donebtn;
    private ImageButton close;
    private EditText Keyword;
    private FilterMoodListener listener;
    private ArrayList<MoodEvent> moodEvents;

    public interface FilterMoodListener {
        void filterMood(ArrayList<MoodEvent> filteredList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            moodEvents = (ArrayList<MoodEvent>) getArguments().getSerializable("moodEvents");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getTargetFragment() instanceof FilterMoodListener) {
            listener = (FilterMoodListener) getTargetFragment();
        } else {
            throw new RuntimeException("Target fragment must implement FilterMoodListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_mood, container, false);

        Keyword = view.findViewById(R.id.desc);
        donebtn = view.findViewById(R.id.done_filter_button);
        close = view.findViewById(R.id.filer_closeButton);

        close.setOnClickListener( v -> {
            dismiss();
        });


        donebtn.setOnClickListener(v -> {
            String searchKeyword = Keyword.getText().toString().trim().toLowerCase();
            ArrayList<MoodEvent> filteredList = new ArrayList<>();

            if (moodEvents != null) {
                for (MoodEvent event : moodEvents) {
                    if (matchesSearch(event, searchKeyword)) {
                        filteredList.add(event);
                    }
                }
            }
            if (listener != null) {
                listener.filterMood(filteredList);
            }
            dismiss();
        });

        return view;
    }

    private boolean matchesSearch(MoodEvent event, String keyword) {
        return event.getEmotionalState().toLowerCase().contains(keyword) ||
                event.getPlace().toLowerCase().contains(keyword) ||
                event.getTrigger().toLowerCase().contains(keyword) ||
                event.getExplainText().toLowerCase().contains(keyword);
    }
}

//package com.example.donotredeem.Fragments;
//
//import static com.google.android.material.internal.ViewUtils.hideKeyboard;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.DialogFragment;
//import androidx.fragment.app.Fragment;
//
//import com.example.donotredeem.MoodEvent;
//import com.example.donotredeem.R;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.ArrayList;
//
//public class FilterFragment extends DialogFragment {
//    private Button donebtn;
//    private EditText Keyword;
//    private FilterMoodListener listener;
//    private ArrayList<MoodEvent> moodEvents;
//
//    MoodEvent[] moodEvents = {
//            new MoodEvent("Happy", LocalDate.of(2024, 3, 2), LocalTime.of(10, 0), "Park", "Good weather", "Had a great morning walk"),
//            new MoodEvent("Sad", LocalDate.of(2024, 3, 1), LocalTime.of(15, 30), "Home", "Bad news", "Received some disappointing news"),
//            new MoodEvent("Excited", LocalDate.of(2024, 3, 3), LocalTime.of(18, 45), "Mall", "Shopping", "Bought something nice"),
//            new MoodEvent("Angry", LocalDate.of(2024, 3, 4), LocalTime.of(14, 15), "Office", "Work stress", "A difficult meeting happened"),
//            new MoodEvent("Calm", LocalDate.of(2024, 3, 5), LocalTime.of(20, 0), "Beach", "Meditation", "Relaxed watching the sunset")
//    };
//
//    public interface FilterMoodListener {
//        void filterMood(ArrayList<MoodEvent> filteredList);
//    }
//
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        Fragment parentFragment = getParentFragment();
//        if (parentFragment instanceof FilterMoodListener) {
//            listener = (FilterMoodListener) parentFragment;
//        } else {
//            throw new RuntimeException("Parent fragment must implement FilterMoodListener");
//        }
//    }
//    private void hideKeyboard() {
//        View view = getView();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.filter_mood, container, false);
//        Log.d("DEBUG", "Filtered List Size: ");
//
//        // Initialize views
//        Keyword = view.findViewById(R.id.desc);
//        donebtn = view.findViewById(R.id.done_filter_button);
//
//
//
//        // Handle button click
//        donebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String searchKeyword = Keyword.getText().toString().trim().toLowerCase();
//                ArrayList<MoodEvent> filteredList = new ArrayList<>();
//
//                for (MoodEvent event : moodEvents) {
//                    if (event.getEmotionalState().toLowerCase().contains(searchKeyword) ||
//                            event.getPlace().toLowerCase().contains(searchKeyword) ||
//                            event.getTrigger().toLowerCase().contains(searchKeyword) ||
//                            event.getExplainText().toLowerCase().contains(searchKeyword)) {
//                        filteredList.add(event);
//                    }
//                }
//                Log.d("DEBUG", "Filtered List Size: " + filteredList.size());
//                listener.filterMood(filteredList);
//                hideKeyboard();
//                new Handler(Looper.getMainLooper()).postDelayed(() -> dismiss(), 200); // Close dialog after filtering
//            }
//        });
//
//        return view;
//    }
//}