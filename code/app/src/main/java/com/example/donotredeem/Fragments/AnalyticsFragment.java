package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donotredeem.EmojiAxisRenderer;
import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
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
    private LineChart moodChart;
    private LocalDate selectedDate;

    private ImageView back_button;
    private Map<String, Integer> dateEmojiMap = new HashMap<>();
    private Map<String, Integer> emotionCountMap = new HashMap<>();
    private List<String> emotionOrder = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.analytics, container, false);
        initWidgets(view);
        selectedDate = LocalDate.now();
        setupChart();
        loadMoodDataForMonth(selectedDate);

        back_button = view.findViewById(R.id.analytics_back_button);
        back_button.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });
        return view;
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.CalendarViewRecycler);
        monthYearText = view.findViewById(R.id.monthYear);
        moodChart = view.findViewById(R.id.mood_chart);

        view.findViewById(R.id.previousMonthButton).setOnClickListener(v -> previousMonthAction());
        view.findViewById(R.id.nextMonthButton).setOnClickListener(v -> nextMonthAction());
    }

    private int[] getEmojiResourcesArray() {
        return new int[]{
                R.drawable.hapi,
                R.drawable.sad,
                R.drawable.fear,
                R.drawable.angry,
                R.drawable.confused,
                R.drawable.disgust,
                R.drawable.shame,
                R.drawable.surprised,
                R.drawable.shy,
                R.drawable.tired
        };
    }
    private void setupChart() {
        // Chart configuration
        moodChart.getDescription().setEnabled(false);
        moodChart.setTouchEnabled(true);
        moodChart.setDragEnabled(true);
        moodChart.setScaleEnabled(true);
        moodChart.setPinchZoom(true);

        // X-axis setup
        XAxis xAxis = moodChart.getXAxis();
        xAxis.setLabelCount(10, true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ""; // ✅ Hides numbers but keeps labels for emojis
            }
        }); // Disable text labels
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setGridColor(Color.TRANSPARENT);
        xAxis.setDrawAxisLine(false);  // ✅ Hides the X-axis line
        xAxis.setDrawGridLines(false);

        int[] emojiResources = getEmojiResourcesArray();
        moodChart.setXAxisRenderer(new EmojiAxisRenderer(
                moodChart.getViewPortHandler(),
                moodChart.getXAxis(),
                moodChart.getTransformer(YAxis.AxisDependency.LEFT),
                requireContext(),
                emojiResources
        ));

        // Y-axis setup
        YAxis leftAxis = moodChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setGridColor(Color.parseColor("#3A3A3A"));
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(20f);  // Adjust if needed
        leftAxis.setLabelCount(11, true); // Ensures steps of 2
        leftAxis.setGranularity(2f); // Force steps of 2
        moodChart.getAxisRight().setEnabled(false);

        // Legend setup
        Legend legend = moodChart.getLegend();
        legend.setEnabled(false);
        float bottomPadding = getResources().getDimension(R.dimen.chart_bottom_padding);
        moodChart.setExtraOffsets(0, 0, 0, bottomPadding);
    }

    private void loadMoodDataForMonth(LocalDate selectedDate) {
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
                    } else {
                        Toast.makeText(requireContext(), "Error loading moods", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processMoodData(List<MoodEvent> moods, LocalDate selectedDate) {
        dateEmojiMap.clear();
        emotionCountMap.clear();
        emotionOrder.clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (MoodEvent mood : moods) {
            try {
                LocalDate moodDate = LocalDate.parse(mood.getDate(), formatter);
                if (moodDate.getMonth() == selectedDate.getMonth() &&
                        moodDate.getYear() == selectedDate.getYear()) {

                    // Update calendar emojis
                    int emojiResId = getEmojiForState(mood.getEmotionalState());
                    if (emojiResId != 0) {
                        String day = String.valueOf(moodDate.getDayOfMonth());
                        dateEmojiMap.put(day, emojiResId);
                    }

                    // Update chart data
                    String emotion = mood.getEmotionalState();
                    emotionCountMap.put(emotion, emotionCountMap.getOrDefault(emotion, 0) + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateChartData();
        setMonthView();
    }

    private void updateChartData() {
        emotionOrder.clear();
        ArrayList<Entry> entries = new ArrayList<>();

        // All 10 moods must always be included
        String[] allEmotions = {"Happy", "Sad", "Fear", "Angry", "Confused",
                "Disgusted", "Shameful", "Surprised", "Shy", "Tired"};

        for (int i = 0; i < allEmotions.length; i++) {
            int count = emotionCountMap.getOrDefault(allEmotions[i], 0);
            entries.add(new Entry(i, count));
            emotionOrder.add(allEmotions[i]);  // Store all 10 emotions
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.WHITE);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        moodChart.setData(new LineData(dataSet));
        moodChart.invalidate();
    }


    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, dateEmojiMap);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private int getEmojiForState(String emotionalState) {
        if (emotionalState == null) return 0;
        switch (emotionalState) {
            case "Happy": return R.drawable.hapi;
            case "Sad": return R.drawable.sad;
            case "Fear": return R.drawable.fear;
            case "Angry": return R.drawable.angry;
            case "Confused": return R.drawable.confused;
            case "Disgusted": return R.drawable.disgust;
            case "Shameful": return R.drawable.shame;
            case "Surprised": return R.drawable.surprised;
            case "Shy": return R.drawable.shy;
            case "Tired": return R.drawable.tired;
            default: return 0;
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