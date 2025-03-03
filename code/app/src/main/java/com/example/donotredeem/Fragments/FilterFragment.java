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

    private ImageButton selectedEmoji = null;
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

        int[] emojiButtonIds = {
                R.id.filter_emoji_happy, R.id.filter_emoji_sad, R.id.filter_emoji_fear,
                R.id.filter_emoji_angry, R.id.filter_emoji_confused, R.id.filter_emoji_disgusted,
                R.id.filter_emoji_shameful, R.id.filter_emoji_surprised, R.id.filter_emoji_shy,
                R.id.filter_emoji_tired
        };

        for (int id : emojiButtonIds) {
            ImageButton emojiButton = view.findViewById(id);
            emojiButton.setOnClickListener(v -> highlightSelectedEmoji((ImageButton) v));
        }

        return view;
    }

    private boolean matchesSearch(MoodEvent event, String keyword) {
        return event.getEmotionalState().toLowerCase().contains(keyword) ||
                event.getPlace().toLowerCase().contains(keyword) ||
                event.getTrigger().toLowerCase().contains(keyword) ||
                event.getExplainText().toLowerCase().contains(keyword);
    }

    private void highlightSelectedEmoji(ImageButton selected) {
        if (selectedEmoji != null) {
            selectedEmoji.setBackground(null); // Remove highlight from previous selection
            selectedEmoji.setElevation(0);
        }

        if (selectedEmoji == selected) {
            selectedEmoji = null; // Unselect if clicking the same emoji
        } else {
            selected.setBackgroundResource(R.drawable.highlight_background);
            selected.setElevation(8);
            selectedEmoji = selected;
        }
    }
}

