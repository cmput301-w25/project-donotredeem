package com.example.donotredeem;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.donotredeem.ImageAdapter;
import com.example.donotredeem.MainActivity;
import com.example.donotredeem.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

///**
// * Activity that manages the mood selection view, displaying a carousel of mood-related images
// * and providing navigation options for the user to proceed or skip the mood selection process.
// */
//public class MoodSelectionActivity extends AppCompatActivity {
//    private ViewPager2 viewPager;
//    private Button nextButton;
//    private Button skipButton;
//
//    /**
//     * Called when the activity is created. Initializes the mood image carousel and the navigation buttons.
//     * It sets up the ViewPager2 with a list of mood-related images and prepares buttons for the next action.
//     *
//     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
//     *                           this Bundle contains the data it most recently supplied.
//     */
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.test4);
//
//        viewPager = findViewById(R.id.viewPager);
//        nextButton = findViewById(R.id.next_button);
//        skipButton = findViewById(R.id.skip_button);
//
//        List<Integer> imageList = Arrays.asList(
//                R.drawable.main_happy, R.drawable.main_sad, R.drawable.main_fear,
//                R.drawable.main_angry, R.drawable.main_confused, R.drawable.main_disgusted,
//                R.drawable.main_shameful, R.drawable.main_surprised, R.drawable.main_shy,
//                R.drawable.main_tired
//        );
//
//        ImageAdapter adapter = new ImageAdapter(imageList);
//        viewPager.setAdapter(adapter);
//
//        // Start from a middle position to allow infinite looping
//        int startPosition = Integer.MAX_VALUE / 2;
//        startPosition = startPosition - (startPosition % imageList.size()); // Align with list
//        viewPager.setCurrentItem(startPosition, false);
//
//
//    }
//}

public class MoodSelectionActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private Button nextButton;
    private Button skipButton;
    private List<Integer> imageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test4);

        viewPager = findViewById(R.id.viewPager);
        nextButton = findViewById(R.id.next_button);
        skipButton = findViewById(R.id.skip_button);

        imageList = Arrays.asList(
                R.drawable.main_happy, R.drawable.main_sad, R.drawable.main_fear,
                R.drawable.main_angry, R.drawable.main_confused, R.drawable.main_disgusted,
                R.drawable.main_shameful, R.drawable.main_surprised, R.drawable.main_shy,
                R.drawable.main_tired
        );

        ImageAdapter adapter = new ImageAdapter(imageList);
        viewPager.setAdapter(adapter);

        int startPosition = Integer.MAX_VALUE / 2;
        startPosition = startPosition - (startPosition % imageList.size());
        viewPager.setCurrentItem(startPosition, false);

//        nextButton.setOnClickListener(v -> {
//            int currentPosition = viewPager.getCurrentItem() % imageList.size();
//            String selectedMood = getMoodName(currentPosition);
//            launchMainActivityWithMood(selectedMood);
//        });
        nextButton.setOnClickListener(v -> {
            int currentPosition = viewPager.getCurrentItem() % imageList.size();
            String selectedMood = getMoodName(currentPosition);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("SELECTED_MOOD", selectedMood);
            intent.putExtra("SHOW_FRAGMENT", true);
            startActivity(intent);
            finish();
        });

        skipButton.setOnClickListener(v -> launchMainActivity());
    }

    private String getMoodName(int position) {
        String[] moodNames = getResources().getStringArray(R.array.mood_names);
        return moodNames[position];
    }

    private void launchMainActivityWithMood(String mood) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("SELECTED_MOOD", mood);
        intent.putExtra("SHOW_FRAGMENT", true);
        startActivity(intent);
        finish();
    }

    private void launchMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
