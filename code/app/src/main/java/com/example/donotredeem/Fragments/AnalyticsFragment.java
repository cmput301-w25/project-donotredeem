package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private Map<String, Integer> dateEmojiMap = new HashMap<>();  // Changed to Integer values

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.analytics, container, false);
        initWidgets(view);
        selectedDate = LocalDate.now();
        loadMoodDataForMonth(selectedDate);
        return view;
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.CalendarViewRecycler);
        monthYearText = view.findViewById(R.id.monthYear);

        view.findViewById(R.id.previousMonthButton).setOnClickListener(v -> previousMonthAction());
        view.findViewById(R.id.nextMonthButton).setOnClickListener(v -> nextMonthAction());
    }


    private void loadMoodDataForMonth(LocalDate selectedDate) {
        // Get username from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username == null || username.isEmpty()) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("MoodEvents")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<MoodEvent> moodList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MoodEvent mood = document.toObject(MoodEvent.class);
                            moodList.add(mood);
                        }
                        processMoodData(moodList, selectedDate);
                        setMonthView();
                    } else {
                        Toast.makeText(requireContext(), "Error loading moods", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void processMoodData(List<MoodEvent> moods, LocalDate selectedDate) {
        dateEmojiMap.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (MoodEvent mood : moods) {
            try {
                LocalDate moodDate = LocalDate.parse(mood.getDate(), formatter);

                // Explicitly check month and year
                if (moodDate.getMonth() == selectedDate.getMonth() &&
                        moodDate.getYear() == selectedDate.getYear()) {

                    // Ensure day is parsed without leading zero
                    String day = String.valueOf(moodDate.getDayOfMonth());

                    // Get emoji resource ID
                    int emojiResId = getEmojiForState(mood.getEmotionalState());

                    // Add to map only if valid emoji found
                    if (emojiResId != 0) {
                        dateEmojiMap.put(day, emojiResId);
                    }
                }
            } catch (Exception e) {
                // Optional: log the error if needed
            }
        }
    }


    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, dateEmojiMap);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }


//    private int getDominantEmoji(List<Mood> moods) {
//        if (moods == null || moods.isEmpty()) return 0;
//
//        Map<String, Integer> emotionCounts = new HashMap<>();
//
//        for (Mood mood : moods) {
//            String emotion = mood.getEmotionalState();
//            emotionCounts.put(emotion, emotionCounts.getOrDefault(emotion, 0) + 1);
//        }
//
//        int maxCount = Collections.max(emotionCounts.values());
//        for (Map.Entry<String, Integer> entry : emotionCounts.entrySet()) {
//            if (entry.getValue() == maxCount) {
//                return getEmojiForState(entry.getKey());
//            }
//        }
//        return 0;
//    }
//
//    private long parseTimeToTimestamp(String time) {
//        try {
//            String[] parts = time.split(":");
//            int hours = Integer.parseInt(parts[0]);
//            int minutes = Integer.parseInt(parts[1]);
//            return hours * 60L + minutes;
//        } catch (Exception e) {
//            return 0;
//        }
//    }

    private int getEmojiForState(String emotionalState) {
        if (emotionalState == null) return 0;

        switch (emotionalState) {
            case "Happy":
                return R.drawable.hapi;
            case "Sad":
                return R.drawable.sad;
            case "Fear":
                return R.drawable.fear;
            case "Angry":
                return R.drawable.angry;
            case "Confused":
                return R.drawable.confused;
            case "Disgusted":
                return R.drawable.disgust;
            case "Shameful":
                return R.drawable.shame;
            case "Surprised":
                return R.drawable.surprised;
            case "Shy":
                return R.drawable.shy;
            case "Tired":
                return R.drawable.tired;
            default:
                return 0;
        }
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    private void previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1);
        loadMoodDataForMonth(selectedDate);
    }

    private void nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1);
        loadMoodDataForMonth(selectedDate);
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.isEmpty()) {
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
    // Add this Mood class if you don't have it already