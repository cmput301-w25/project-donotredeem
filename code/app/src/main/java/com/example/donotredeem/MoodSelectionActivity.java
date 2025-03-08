package com.example.donotredeem;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;


/**
 * Activity that manages the mood selection view, displaying a carousel of mood-related images
 * and providing navigation options for the user to proceed or skip the mood selection process.
 */
public class MoodSelectionActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private Button nextButton;
    private Button skipButton;

    /**
     * Called when the activity is created. Initializes the mood image carousel and the navigation buttons.
     * It sets up the ViewPager2 with a list of mood-related images and prepares buttons for the next action.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test4);

        viewPager = findViewById(R.id.viewPager);
        nextButton = findViewById(R.id.next_button);
        skipButton = findViewById(R.id.skip_button);

        List<Integer> imageList = Arrays.asList(
                R.drawable.main_happy, R.drawable.main_sad, R.drawable.main_fear,
                R.drawable.main_angry, R.drawable.main_confused, R.drawable.main_disgusted,
                R.drawable.main_shameful, R.drawable.main_surprised, R.drawable.main_shy,
                R.drawable.main_tired
        );

        ImageAdapter adapter = new ImageAdapter(imageList);
        viewPager.setAdapter(adapter);

        // Start from a middle position to allow infinite looping
        int startPosition = Integer.MAX_VALUE / 2;
        startPosition = startPosition - (startPosition % imageList.size()); // Align with list
        viewPager.setCurrentItem(startPosition, false);


    }
}
