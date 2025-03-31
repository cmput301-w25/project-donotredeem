package com.example.donotredeem;

import android.app.Application;
import android.content.Context;

/**
 * Custom Application class that provides global application context access.
 * Maintains a singleton instance for application-wide context retrieval.
 *
 * Usage example:
 *
 * Context appContext = MyApplication.getInstance().getApplicationContext();
 *
 *
 */
public class MyApplication extends Application {
    /** Singleton instance of the application */
    private static MyApplication instance;

    /**
     * Returns the singleton instance of the application.
     * <p><b>Note:</b> Consider using {@link #getApplicationContext()} through Activity context
     * for most cases to avoid potential memory leaks.</p>
     *
     * @return Singleton application instance
     */
    public static Context getInstance() {
        return instance;
    }

    /**
     * Initializes the application and sets up the singleton instance.
     * Called when the application is first created.
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize singleton instance
        instance = this;
    }
}
