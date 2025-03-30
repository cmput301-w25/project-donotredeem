package com.example.donotredeem.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AppComponentFactory;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.text.NoCopySpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.donotredeem.LogIn;
import com.example.donotredeem.R;
import com.example.donotredeem.Register;

public class IntroPage extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_page);

        ImageView happy = findViewById(R.id.intro_happy);
        ImageView sad = findViewById(R.id.intro_sad);
        ImageView angry = findViewById(R.id.intro_angry);
        ImageView shameful = findViewById(R.id.intro_shameful);
        ImageView disgusted = findViewById(R.id.intro_disgusted);
        ImageView confused = findViewById(R.id.intro_confused);
        ImageView shy = findViewById(R.id.intro_shy);
        ImageView fear = findViewById(R.id.intro_fear);
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
        happy_animator.setDuration(6000);
        happy_animator.setInterpolator(new LinearInterpolator());
        happy_animator.setStartDelay(0);
        happy_animator.start();

        ObjectAnimator sad_animator = ObjectAnimator.ofFloat(sad, View.X, View.Y, path);
        sad_animator.setDuration(6000);
        sad_animator.setInterpolator(new LinearInterpolator());
        sad_animator.setStartDelay(500);
        sad_animator.start();

        ObjectAnimator shame_animator = ObjectAnimator.ofFloat(shameful, View.X, View.Y, path);
        shame_animator.setDuration(6000);
        shame_animator.setInterpolator(new LinearInterpolator());
        shame_animator.setStartDelay(1000);
        shame_animator.start();

        ObjectAnimator disgusted_animator = ObjectAnimator.ofFloat(disgusted, View.X, View.Y, path);
        disgusted_animator.setDuration(6000);
        disgusted_animator.setInterpolator(new LinearInterpolator());
        disgusted_animator.setStartDelay(1500);
        disgusted_animator.start();

        ObjectAnimator surprised_animator = ObjectAnimator.ofFloat(surprised, View.X, View.Y, path);
        surprised_animator.setDuration(6000);
        surprised_animator.setInterpolator(new LinearInterpolator());
        surprised_animator.setStartDelay(2000);
        surprised_animator.start();




        ObjectAnimator confused_animator = ObjectAnimator.ofFloat(confused, View.X, View.Y, path);
        confused_animator.setDuration(6000);
        confused_animator.setInterpolator(new LinearInterpolator());
        confused_animator.setStartDelay(2500);
        confused_animator.start();

        ObjectAnimator shy_animator = ObjectAnimator.ofFloat(shy, View.X, View.Y, path);
        shy_animator.setDuration(6000);
        shy_animator.setInterpolator(new LinearInterpolator());
        shy_animator.setStartDelay(3000);
        shy_animator.start();

        ObjectAnimator fear_animator = ObjectAnimator.ofFloat(fear, View.X, View.Y, path);
        fear_animator.setDuration(6000);
        fear_animator.setInterpolator(new LinearInterpolator());
        fear_animator.setStartDelay(3500);
        fear_animator.start();

        ObjectAnimator tired_animator = ObjectAnimator.ofFloat(tired, View.X, View.Y, path);
        tired_animator.setDuration(6000);
        tired_animator.setInterpolator(new LinearInterpolator());
        tired_animator.setStartDelay(4000);
        tired_animator.start();

        ObjectAnimator angry_animator = ObjectAnimator.ofFloat(angry, View.X, View.Y, path);
        angry_animator.setDuration(6000);
        angry_animator.setInterpolator(new LinearInterpolator());
        angry_animator.setStartDelay(4500);
        angry_animator.start();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This will be executed after the specified delay
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();  // Close the current activity
            }
        }, 6000);

    }
}
