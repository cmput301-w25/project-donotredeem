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

public class MoodSelectionActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
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

    private class MoodPagerAdapter extends RecyclerView.Adapter<MoodViewHolder> {
        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
            int actualPosition = position % moodImages.length;
            if (actualPosition < 0) {
                actualPosition += moodImages.length;
            }
            holder.imageView.setImageResource(moodImages[actualPosition]);
        }

        // Rest of the adapter remains the same
        @NonNull
        @Override
        public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image, parent, false);
            return new MoodViewHolder(view);
        }
    }

    private static class MoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        MoodViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.moodImage);
        }
    }
}