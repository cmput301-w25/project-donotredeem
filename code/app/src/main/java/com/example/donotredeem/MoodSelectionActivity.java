package com.example.donotredeem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.donotredeem.R;
/**
 * Activity that provides a mood selection interface using a ViewPager2 to display various mood images.
 *
 * This activity sets up a ViewPager2 with an adapter that cycles through an array of mood images.
 * The ViewPager2 is initialized to a middle position to allow for infinite scrolling.
 *
 */
public class MoodSelectionActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    /**
     * Array of drawable resource IDs representing mood images.
     */
    private int[] moodImages = {
            R.drawable.main_happy,
            R.drawable.main_sad,
            R.drawable.main_fear,
            R.drawable.main_angry,
            R.drawable.main_confused,
            R.drawable.main_disgusted,
            R.drawable.main_shameful,
            R.drawable.main_surprised,
            R.drawable.main_shy,
            R.drawable.main_tired
    };
    /**
     * Called when the activity is first created.
     *
     * This method initializes the ViewPager2, sets its adapter, and registers a page change callback
     * to handle mood selection based on the current page.
     *
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied; otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test4);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MoodPagerAdapter());
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2, false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int actualPosition = position % moodImages.length;
                if (actualPosition < 0) {
                    actualPosition += moodImages.length;
                }
                // Use actualPosition for selection
            }
        });
    }
    /**
     * Adapter class for supplying mood images to the ViewPager2.
     *
     * This adapter provides an effectively infinite scrolling experience by returning Integer.MAX_VALUE as the item count.
     * It calculates the actual position for each mood image using modulo arithmetic.
     *
     */
    private class MoodPagerAdapter extends RecyclerView.Adapter<MoodViewHolder> {
        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
        /**
         * Binds the mood image to the view holder at the specified position.
         *
         * The actual position is determined by taking the modulo of the position with the number of mood images.
         *
         *
         * @param holder   the view holder which should be updated to represent the contents of the item at the given position.
         * @param position the position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
            int actualPosition = position % moodImages.length;
            if (actualPosition < 0) {
                actualPosition += moodImages.length;
            }
            holder.imageView.setImageResource(moodImages[actualPosition]);
        }
        /**
         * Called when RecyclerView needs a new {@link MoodViewHolder} of the given type to represent an item.
         *
         * @param parent   the ViewGroup into which the new View will be added after it is bound to an adapter position.
         * @param viewType the view type of the new View.
         * @return a new MoodViewHolder that holds a View of the given view type.
         */
        // Rest of the adapter remains the same
        @NonNull
        @Override
        public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image, parent, false);
            return new MoodViewHolder(view);
        }
    }
    /**
     * ViewHolder class for holding the mood image view.
     *
     * This static inner class holds a reference to the ImageView that displays a mood image.
     *
     */
    private static class MoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        MoodViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.moodImage);
        }
    }
}