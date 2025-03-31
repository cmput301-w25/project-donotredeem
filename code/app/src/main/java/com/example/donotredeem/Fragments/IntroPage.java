package com.example.donotredeem.Fragments;

import android.animation.ObjectAnimator;

import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.donotredeem.LogIn;
import com.example.donotredeem.R;

/**
 * Introductory activity displaying an animated emotional spectrum visualization.
 * Features 8 core emotions represented as emojis following a complex path animation
 * before transitioning to the login screen.
 *
 * <p>Animation Details:
 * <ul>
 * <li>Uses quadratic Bézier curves for smooth bounce effects</li>
 * <li>Staggered start delays create wave-like motion</li>
 * <li>Linear interpolator maintains constant speed</li>
 * <li>4500ms total duration matches transition timeout</li>
 * </ul>
 */
public class IntroPage extends AppCompatActivity {

    /**
     * Configures introductory animation sequence and automatic transition.
     *
     * <p>Implementation Flow:
     * 1. Define complex path using consecutive quadratic curves
     * 2. Create staggered ObjectAnimators for each emoji
     * 3. Start parallel animations with sequential delays
     * 4. Schedule login screen transition post-animation
     *
     * @param savedInstanceState Persisted state bundle (unused in this splash screen)
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_page);

        ImageView happy = findViewById(R.id.intro_happy);
        ImageView sad = findViewById(R.id.intro_sad);
        ImageView angry = findViewById(R.id.intro_angry);
        ImageView shameful = findViewById(R.id.intro_shameful);
        ImageView confused = findViewById(R.id.intro_confused);
        ImageView shy = findViewById(R.id.intro_shy);
        ImageView tired = findViewById(R.id.intro_tired);
        ImageView surprised = findViewById(R.id.intro_surprised);


        Path path = new Path();
        path.moveTo(-400f, 1000f);
        path.rQuadTo(200f, -350f, 500f, 0f);  // First big bounce up
        path.rQuadTo(200f, 350f, 500f, 0f);   // Bounce down
        path.rQuadTo(200f, -350f, 500f, 0f);  // Second bounce up
        path.rQuadTo(200f, 350f, 500f, 0f);   // Bounce down
        path.rQuadTo(200f, -350f, 500f, 0f);  // Third bounce up
        path.rQuadTo(200f, 350f, 500f, 0f);
        path.rQuadTo(200f, -350f, 500f, 0f);  // Third bounce up
        path.rQuadTo(200f, 3500f, 500f, 0f);


        ObjectAnimator happy_animator = ObjectAnimator.ofFloat(happy, View.X, View.Y, path);
        happy_animator.setDuration(4500);
        happy_animator.setInterpolator(new LinearInterpolator());
        happy_animator.setStartDelay(0);
        happy_animator.start();

        ObjectAnimator sad_animator = ObjectAnimator.ofFloat(sad, View.X, View.Y, path);
        sad_animator.setDuration(4500);
        sad_animator.setInterpolator(new LinearInterpolator());
        sad_animator.setStartDelay(300);
        sad_animator.start();

        ObjectAnimator shame_animator = ObjectAnimator.ofFloat(shameful, View.X, View.Y, path);
        shame_animator.setDuration(4500);
        shame_animator.setInterpolator(new LinearInterpolator());
        shame_animator.setStartDelay(600);
        shame_animator.start();


        ObjectAnimator surprised_animator = ObjectAnimator.ofFloat(surprised, View.X, View.Y, path);
        surprised_animator.setDuration(4500);
        surprised_animator.setInterpolator(new LinearInterpolator());
        surprised_animator.setStartDelay(2100);
        surprised_animator.start();

        ObjectAnimator angry_animator = ObjectAnimator.ofFloat(angry, View.X, View.Y, path);
        angry_animator.setDuration(4500);
        angry_animator.setInterpolator(new LinearInterpolator());
        angry_animator.setStartDelay(900);
        angry_animator.start();


        ObjectAnimator confused_animator = ObjectAnimator.ofFloat(confused, View.X, View.Y, path);
        confused_animator.setDuration(4500);
        confused_animator.setInterpolator(new LinearInterpolator());
        confused_animator.setStartDelay(1200);
        confused_animator.start();

        ObjectAnimator shy_animator = ObjectAnimator.ofFloat(shy, View.X, View.Y, path);
        shy_animator.setDuration(4500);
        shy_animator.setInterpolator(new LinearInterpolator());
        shy_animator.setStartDelay(1500);
        shy_animator.start();


        ObjectAnimator tired_animator = ObjectAnimator.ofFloat(tired, View.X, View.Y, path);
        tired_animator.setDuration(4500);
        tired_animator.setInterpolator(new LinearInterpolator());
        tired_animator.setStartDelay(1800);
        tired_animator.start();




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This will be executed after the specified delay
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();  // Close the current activity
            }
        }, 4500);

    }
}
