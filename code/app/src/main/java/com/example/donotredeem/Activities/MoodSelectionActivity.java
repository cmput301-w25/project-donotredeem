package com.example.donotredeem.Activities;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;

import com.example.donotredeem.Adapters.ImageAdapter;
import com.example.donotredeem.R;

/**
 * Activity for selecting a mood from a carousel of mood images.
 * Allows users to either select a mood or skip the selection process.
 * Integrates with MainActivity to pass the selected mood information.
 */
public class MoodSelectionActivity extends AppCompatActivity {
    // UI Components
    private ViewPager2 viewPager;
    private Button nextButton;
    private Button skipButton;
    private List<Integer> imageList;

    /**
     * Initializes the activity layout and configures mood carousel behavior.
     *
     * @param savedInstanceState Persisted state bundle (unused in this implementation)
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test4);

        // Initialize UI components
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

    /**
     * Maps carousel position to mood name string
     *
     * @param position Current position in the mood list
     * @return Localized mood name from string resources
     */
    private String getMoodName(int position) {
        String[] moodNames = getResources().getStringArray(R.array.mood_names);
        return moodNames[position];
    }

    /**
     * Launches MainActivity without mood selection data
     */
    private void launchMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
