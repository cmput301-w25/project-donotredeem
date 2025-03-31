package com.example.donotredeem.Classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class for checking network connectivity status.
 */
public class NetworkUtils {
    /**
     * Checks if the device has an active internet connection.
     *
     * @param context The application context used to access the connectivity service.
     * @return {@code true} if the device is connected to the internet, otherwise {@code false}.
     */
    public static boolean isNetworkAvailable(Context context) {
        // Retrieve the connectivity manager to check the network status
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Ensure the connectivity manager is not null before proceeding
        if (connectivityManager != null) {
            // Get information about the currently active network
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            // Return true if the network is connected, false otherwise
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}