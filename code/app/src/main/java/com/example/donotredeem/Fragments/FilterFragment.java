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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class FilterFragment extends DialogFragment {
    private Button donebtn;
    private ImageButton close;
    private EditText Keyword;
    private Button pastmonth;
    private Button pastweek;
    private Button all;
    String btn;
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
        pastmonth = view.findViewById(R.id.month_filter_button);
        pastweek = view.findViewById(R.id.week_filter_button);
        all = view.findViewById(R.id.All_filter_button);

        pastmonth.setOnClickListener(v ->{
            btn = "month";
        });
        pastweek.setOnClickListener(v ->{
            btn = "week";
        });
        all.setOnClickListener(v ->{
            btn = "all";
        });
        close.setOnClickListener( v -> {
            dismiss();
        });


        donebtn.setOnClickListener(v -> {
            String searchKeyword = Keyword.getText().toString().trim().toLowerCase();
            ArrayList<MoodEvent> filteredList = new ArrayList<>();

            if (moodEvents != null) {
                for (MoodEvent event : moodEvents) {
                    boolean matchesKeyword = matchesSearch(event, searchKeyword);
                    boolean isInMonth = month(event);
                    boolean isInWeek = week(event);

                    if (Objects.equals(btn, "month")) {
                        // If "month" is selected, add event only if it matches keyword OR no keyword is entered
                        if (isInMonth && (matchesKeyword || searchKeyword.isEmpty())) {
                            filteredList.add(event);
                        }
                    } else if (Objects.equals(btn, "week")) {
                        // If "week" is selected, add event only if it matches keyword OR no keyword is entered
                        if (isInWeek && (matchesKeyword || searchKeyword.isEmpty())) {
                            filteredList.add(event);
                        }
                    } else if (Objects.equals(btn, "all")) {
                        // If "all" is selected, apply only keyword filtering
                        if (matchesKeyword || searchKeyword.isEmpty()) {
                            filteredList.add(event);
                        }
                    } else if (btn == null) {
                        // If no filter is selected, only apply keyword filtering
                        if (matchesKeyword) {
                            filteredList.add(event);
                        }
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

    private boolean month(MoodEvent event){
        LocalDate currentDate = LocalDate.now();

        // Get event date from MoodEvent (assuming you have a getDate() method)
        LocalDate eventDate = parseStringToDate(event.getDate());

        // Compare year and month components
        return eventDate.getYear() == currentDate.getYear() &&
                eventDate.getMonth() == currentDate.getMonth();
    }

    private boolean week(MoodEvent event){
        LocalDate currentDate = LocalDate.now();
        LocalDate eventDate = parseStringToDate(event.getDate());

        // Get week of year using system's week definition
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        return currentDate.getYear() == eventDate.getYear() &&
                currentDate.get(weekFields.weekOfYear()) == eventDate.get(weekFields.weekOfYear());
    }
    private boolean matchesSearch(MoodEvent event, String keyword) {
        if (keyword.isEmpty()) {
            return false; // Avoid matching empty keyword
        }

        // Define regex pattern for whole word match
        String pattern = "\\b" + Pattern.quote(keyword) + "\\b";

        return matchesWholeWord(event.getEmotionalState(), pattern) ||
                matchesWholeWord(event.getPlace(), pattern) ||
                matchesWholeWord(event.getTrigger(), pattern) ||
                matchesWholeWord(event.getExplainText(), pattern);
    }

    /**
     * Checks if the given text contains the keyword as a whole word.
     */
    private boolean matchesWholeWord(String text, String pattern) {
        if (text == null) {
            return false;
        }
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text).find();
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

    private LocalDate parseStringToDate(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Adjust format based on your date format
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            Log.e("MoodHistory", "Invalid date format", e);
            return LocalDate.now(); // Default to current date if parsing fails
        }
    }

    private LocalTime parseStringToTime(String timeString) {
        // Log the raw string received
        Log.d("MoodHistory", "Parsing time string: " + timeString);

        if (timeString == null || timeString.isEmpty()) {
            Log.e("MoodHistory", "Time string is null or empty");
            return LocalTime.now(); // Default to current time if parsing fails
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // 24-hour format
            LocalTime parsedTime = LocalTime.parse(timeString, formatter);

            // Log the parsed time
            Log.d("MoodHistory", "Parsed time: " + parsedTime);

            return parsedTime;
        } catch (DateTimeParseException e) {
            Log.e("MoodHistory", "Error parsing time: " + timeString, e);
            return LocalTime.now(); // Default to current time if parsing fails
        }
    }
}



//    private boolean matchesSearch(MoodEvent event, String keyword) {
//        return event.getEmotionalState().toLowerCase().contains(keyword) ||
//                event.getPlace().toLowerCase().contains(keyword) ||
//                event.getTrigger().toLowerCase().contains(keyword) ||
//                event.getExplainText().toLowerCase().contains(keyword);
//    }

