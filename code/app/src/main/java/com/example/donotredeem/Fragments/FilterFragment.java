package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodType;
import com.example.donotredeem.R;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Dialog fragment for applying advanced filters to mood event listings.
 * Supports multi-criteria filtering including:
 * - Emoji-based mood type selection
 * - Time range filters (week/month/all)
 * - Keyword search across multiple fields
 * - Persistent filter settings across sessions
 *
 * <p>Key Features:
 * <ul>
 * <li>Visual emoji toggle system with highlight states</li>
 * <li>Compound filter logic with AND/OR combinations</li>
 * <li>SharedPreferences-based filter persistence</li>
 * <li>Regex-powered whole-word keyword matching</li>
 * </ul>
 */
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

    /**
     * Callback interface for delivering filtered results
     */
    public interface FilterMoodListener {
        /**
         * Receives filtered results when done button clicked
         * @param filteredList Events matching all active filters
         */
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

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view of the fragment.
     */
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
            Toast.makeText(requireContext(), "You have pressed Past Month", Toast.LENGTH_LONG).show();
            btn = "month";
        });
        pastweek.setOnClickListener(v ->{
            Toast.makeText(requireContext(), "You have pressed Past Week", Toast.LENGTH_LONG).show();
            btn = "week";
        });
        all.setOnClickListener( v -> {
            Toast.makeText(requireContext(), "You have pressed All", Toast.LENGTH_LONG).show();
            btn = "all";
        });
        close.setOnClickListener( v -> {
            dismiss();
        });

        // Load saved filters when the filter fragment is opened
        loadSavedFilters(view);

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

            saveFilters(searchKeyword, btn, selectedEmojiNames);
            dismiss();
        });

        for (int id : emojiButtonIds) {
            ImageButton emojiButton = view.findViewById(id);
            emojiButton.setOnClickListener(v -> highlightSelectedEmoji((ImageButton) v));
        }
        return view;
    }

    /**
     * Restores saved filter states from SharedPreferences:
     * - Last used keyword
     * - Active time filter
     * - Selected emoji states
     * @param view Root view of fragment
     */
    private void loadSavedFilters(View view) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE);

        String savedKeyword = sharedPreferences.getString("keyword", "");
        String savedTimeFilter = sharedPreferences.getString("timeFilter", "all");
        String savedEmojis = sharedPreferences.getString("selectedEmojis", "");

        // Restore keyword
        Keyword.setText(savedKeyword);

        if ("month".equals(savedTimeFilter)) {
            btn = "month";
            Snackbar.make(view, "You pressed Past Month previously", Snackbar.LENGTH_SHORT).show();
        } else if ("week".equals(savedTimeFilter)) {
            btn = "week";
            Snackbar.make(view, "You pressed Past Week previously", Snackbar.LENGTH_SHORT).show();
        } else {
            btn = "all";
            Snackbar.make(view, "You pressed All previously", Snackbar.LENGTH_SHORT).show();
        }

        // Restore selected emojis
        if (!savedEmojis.isEmpty()) {
            String[] emojiArray = savedEmojis.split(",");
            for (String emoji : emojiArray) {
                selectedEmojiNames.add(emoji);
            }
            highlightRestoredEmojis(view);
        }
    }

    /**
     * Restores visual state of emoji buttons from persisted selections.
     *
     * <p>During filter restoration this:
     * <ul>
     * <li>Iterates through all emoji UI elements</li>
     * <li>Matches buttons to saved mood selections</li>
     * <li>Applies highlight styling to previously selected emojis</li>
     * <li>Maintains visual consistency with elevation changes</li>
     * </ul>
     *
     * @param view Root view containing emoji buttons to be validated
     *
     */
    private void highlightRestoredEmojis(View view) {
        for (int id : emojiButtonIds) {
            ImageButton emojiButton = view.findViewById(id);
            MoodType moodType = getMoodForButtonId(id);

            if (moodType != null && selectedEmojiNames.contains(moodType.getMood())) {
                emojiButton.setBackgroundResource(R.drawable.highlight_background);
                emojiButton.setElevation(8);
            }
        }
    }

    /**
     * Persists current filter configuration:
     * - Keyword in lowercase
     * - Time filter selection
     * - CSV of selected emoji names
     * @param keyword Search phrase
     * @param timeFilter Active time range
     * @param emojiSet Selected mood types
     */
    private void saveFilters(String keyword, String timeFilter, HashSet<String> emojiSet) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("keyword", keyword);
        editor.putString("timeFilter", timeFilter);

        // Save selected emojis as a comma-separated string
        editor.putString("selectedEmojis", String.join(",", emojiSet));

        editor.apply();
    }

    /**
     * Date range validation for current month
     * @param event Event to check
     * @return True if event occurred in current calendar month
     */
    public boolean month(MoodEvent event){
        LocalDate currentDate = LocalDate.now();

        // Get event date from MoodEvent (assuming you have a getDate() method)
        LocalDate eventDate = parseStringToDate(event.getDate());

        // Compare year and month components
        return eventDate.getYear() == currentDate.getYear() &&
                eventDate.getMonth() == currentDate.getMonth();
    }

    /**
     * Date range validation for rolling 7-day window
     * @param event Event to check
     * @return True if within last 6 days including today
     */
    public boolean week(MoodEvent event) {
        LocalDate currentDate = LocalDate.now();
        LocalDate eventDate = parseStringToDate(event.getDate());

        // Check if eventDate is within the past 7 days
        return !eventDate.isAfter(currentDate) &&
                !eventDate.isBefore(currentDate.minusDays(6));
    }


    /**
     * Regex-based whole-word search across multiple event fields
     * @param event MoodEvent to evaluate
     * @param keyword Search phrase (lowercase)
     * @return True if any field contains exact word match
     */
    public boolean matchesSearch(MoodEvent event, String keyword) {
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
     * Determines if the provided text contains the keyword as a whole word.
     *
     * @param text    The text to check.
     * @param pattern The regex pattern.
     * @return {@code true} if the text contains the keyword, {@code false} otherwise.
     */
    public boolean matchesWholeWord(String text, String pattern) {
        if (text == null) {
            return false;
        }
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text).find();
    }

    /**
     * Toggles emoji button visual state and selection tracking
     * @param selected Button receiving click interaction
     */
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

    /**
     * Maps view IDs to MoodType enum values
     * @param buttonId Clicked view resource ID
     * @return Corresponding MoodType or null
     */
    public MoodType getMoodForButtonId(int buttonId) {

        for (int i = 0; i < emojiButtonIds.length; i++) {
            if (emojiButtonIds[i] == buttonId) {
                return MoodType.values()[i];
            }
        }
        return null;
    }

    /**
     * Parses a date string into a {@link LocalDate} object.
     *
     * @param dateString The date string in "dd/MM/yyyy" format.
     * @return The parsed {@code LocalDate}, or the current date if parsing fails.
     */
    public LocalDate parseStringToDate(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Adjust format based on your date format
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            return LocalDate.now(); // Default to current date if parsing fails
        }
    }
}



