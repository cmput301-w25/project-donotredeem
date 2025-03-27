package com.example.donotredeem;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Set cache settings (memory or disk-based)
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build()) // Enable memory cache
                .build();

        // Apply settings to Firestore
        db.setFirestoreSettings(settings);

        // Log to confirm Firestore is initialized
        System.out.println("Firestore initialized with offline support!");
    }
}