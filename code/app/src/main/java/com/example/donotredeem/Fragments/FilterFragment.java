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
import com.example.donotredeem.MoodEventAdapter;
import com.example.donotredeem.MoodType;
import com.example.donotredeem.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashSet;
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
    private FilterMoodListener listener;
    private ArrayList<MoodEvent> moodEvents;

    private HashSet<String> selectedEmojiNames = new HashSet<>();
    int[] emojiButtonIds = {
            R.id.filter_emoji_happy, R.id.filter_emoji_sad, R.id.filter_emoji_fear,
            R.id.filter_emoji_angry, R.id.filter_emoji_confused, R.id.filter_emoji_disgusted,
            R.id.filter_emoji_shameful, R.id.filter_emoji_surprised, R.id.filter_emoji_shy,
            R.id.filter_emoji_tired
    };

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
                    boolean matchesKeyword = !searchKeyword.isEmpty() && matchesSearch(event, searchKeyword);
                    boolean matchesTime = false;
                    boolean matchesEmotion = selectedEmojiNames.isEmpty() || selectedEmojiNames.contains(event.getEmotionalState());

                    if (btn != null) {
                        if (btn.equals("month")) {
                            matchesTime = month(event);
                        } else if (btn.equals("week")) {
                            matchesTime = week(event);
                        } else if (btn.equals("all")) {
                            matchesTime = true;  // If "all" is selected, ignore time filtering.
                        }
                    } else {
                        matchesTime = true;  // No time filter applied.
                    }

                    // Apply filtering dynamically
                    if (matchesEmotion && matchesTime && (searchKeyword.isEmpty() || matchesKeyword)) {
                        filteredList.add(event);
                    }
                }
            }

            if (listener != null) {
                listener.filterMood(filteredList);
            }
            dismiss();
        });

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
        int buttonId = selected.getId();
        MoodType selectedMood = getMoodForButtonId(buttonId);

        if (selectedMood == null) return;

        String moodName = selectedMood.getMood();

        if (selectedEmojiNames.contains(moodName)) {
            // If already selected, remove it
            selectedEmojiNames.remove(moodName);
            selected.setBackground(null); // Remove highlight
            selected.setElevation(0);
        } else {
            // If not selected, add it
            selectedEmojiNames.add(moodName);
            selected.setBackgroundResource(R.drawable.highlight_background);
            selected.setElevation(8);
        }
    }

    private MoodType getMoodForButtonId(int buttonId) {

        for (int i = 0; i < emojiButtonIds.length; i++) {
            if (emojiButtonIds[i] == buttonId) {
                //Log.d("SelectedMood", "Mood for buttonId " + buttonId + ": " + selectedMood.getMood());
                return MoodType.values()[i];
            }
        }
        return null;
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

}



