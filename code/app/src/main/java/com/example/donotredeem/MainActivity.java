package com.example.donotredeem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

//import com.example.donotredeem.Fragments.AddMoodEvent;
import com.example.donotredeem.Fragments.AddMoodEvent;
import com.example.donotredeem.Fragments.Analytics;
import com.example.donotredeem.Fragments.MainPage;
import com.example.donotredeem.Fragments.Map;
import com.example.donotredeem.Fragments.moodhistory;
import com.example.donotredeem.Fragments.ProfilePage;
import com.example.donotredeem.Fragments.Requests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private ImageButton addEvent, mapButton, homeButton, heartButton, profilePage;
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MainPage())
                .addToBackStack(null)
                .commit();


        //auth = FirebaseAuth.getInstance();
//        button = findViewById(R.id.temp_sign_out);
//        textView = findViewById(R.id.user);
//        user = auth.getCurrentUser();
//        if (user ==null){
//            Intent intent = new Intent(getApplicationContext(), LogIn.class);
//            startActivity(intent);
//            finish();
//        } else{
//            textView.setText(user.getEmail());
//        }
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), LogIn.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        addEvent = findViewById(R.id.add_button);
        mapButton = findViewById(R.id.map_button);
        homeButton = findViewById(R.id.grid_button);
        heartButton = findViewById(R.id.heart_button);
        profilePage = findViewById(R.id.profilepage);

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment existingFragment = fragmentManager.findFragmentByTag("AddMoodEvent");

                if (fragmentManager.findFragmentByTag("AddMoodEvent") == null) {

                    AddMoodEvent addMoodEvent = new AddMoodEvent();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                            .add(R.id.fragment_container, addMoodEvent, "AddMoodEvent")
                            .addToBackStack("AddMoodEvent")
                            .commit();
                } else {

                    View fragmentView = existingFragment.getView();
                    Animation slideOut = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_out_bottom);
                    fragmentView.startAnimation(slideOut);

                    slideOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            fragmentManager.popBackStack("AddMoodEvent", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            }
        });


        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new Map())
                        .addToBackStack(null) // Adds this transaction to the back stack
                        .commit();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new MainPage())
                        .addToBackStack(null) // Adds this transaction to the back stack
                        .commit();
            }
        });

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new Requests())
                        .addToBackStack(null) // Adds this transaction to the back stack
                        .commit();
            }
        });

        profilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new ProfilePage())
                        .addToBackStack(null) // Adds this transaction to the back stack
                        .commit();

            }
        });

    }

}
