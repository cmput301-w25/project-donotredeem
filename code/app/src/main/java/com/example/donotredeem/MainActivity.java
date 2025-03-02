package com.example.donotredeem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.donotredeem.Fragments.ProfilePage;
import com.example.donotredeem.Fragments.Requests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private ImageButton addEvent, mapButton, gridButton, heartButton, profilePage;
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


        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.temp_sign_out);
        textView = findViewById(R.id.user);
        user = auth.getCurrentUser();
        if (user ==null){
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
            finish();
        } else{
            textView.setText(user.getEmail());
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });

        addEvent = findViewById(R.id.add_button);
        mapButton = findViewById(R.id.map_button);
        gridButton = findViewById(R.id.grid_button);
        heartButton = findViewById(R.id.heart_button);
        profilePage = findViewById(R.id.profilepage);

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddMoodEvent addMoodEvent = new AddMoodEvent();

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .add(R.id.fragment_container, addMoodEvent)
                        .addToBackStack(null)
                        .commit();
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

        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new Analytics())
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