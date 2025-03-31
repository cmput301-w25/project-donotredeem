package com.example.donotredeem;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

/**
 * Custom Application class for initializing Firebase services with enhanced offline capabilities.
 * Configures Firestore to maintain persistent local cache for offline data access.
 *
 * @see <a href="https://firebase.google.com/docs/firestore/manage-data/enable-offline">Firebase Offline Documentation</a>
 */
public class MyApp extends Application {
    /**
     * Called when the application is starting. Initializes Firebase services and
     * configures Firestore with offline persistence settings.
     *
     * Key configurations:
     * - Enables disk persistence for offline data access
     * - Sets unlimited cache size for local data storage
     * - Initializes the default FirebaseApp instance
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Configure Firestore instance with offline support
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);
        // Initialize Firebase core components
        FirebaseApp.initializeApp(this);
        // Log to confirm Firestore is initialized
        System.out.println("Firestore initialized with offline support!");
    }
}