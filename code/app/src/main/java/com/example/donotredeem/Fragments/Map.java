package com.example.donotredeem.Fragments;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.LogIn;
import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodType;
import com.example.donotredeem.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying mood events on a Google Map with filtering capabilities.
 * Handles real-time location updates, friend-based mood visualization, and
 * distance-based filtering. Integrates with Firestore for data persistence.
 */
public class Map extends Fragment implements OnMapReadyCallback, FilterFragment.FilterMoodListener {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private String loggedInUsername;
    private static final int LOCATION_REQUEST = 300;
    double currentlatitude;
    double currentlongitude;

    ImageButton me_btn, filter_btn, friends_btn, distance_btn;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    public ArrayList<MoodEvent> moodHistoryList  = new ArrayList<>();;

    /**
     * Applies mood type filters to map display
     * @param filteredList Filtered list of mood events to display
     */
    @Override
    public void filterMood(ArrayList<MoodEvent> filteredList) {
        Log.d("MAP", "Filtered list size = " + filteredList.size());

        mMap.clear();
        updateMapMarkers(filteredList,"Friends");

    }

    /**
     * Initializes map fragment and UI components. Configures:
     * - Map fragment async loading
     * - Firestore database connection
     * - Location service client
     * - Click listeners for filter buttons
     *
     * @param inflater Layout inflater
     * @param container Parent view group
     * @param savedInstanceState Saved state bundle
     * @return Configured view hierarchy
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        db = FirebaseFirestore.getInstance();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());


        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("username")) {
            Log.e("MainPageDebug", "Username not found in SharedPreferences, redirecting to login");
            redirectToLogin();
        } else {
            loggedInUsername = sharedPreferences.getString("username", null);
            Log.d("HistoryDebug", "Retrieved username: " + loggedInUsername);
        }

        if (loggedInUsername == null) {
            // No user logged in, redirect to login screen
            redirectToLogin();
        } else {
            // Fetch user details from Firestore
            Log.d("HistoryDebug", "Fetching user details for: " + loggedInUsername);

        }


        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        filter_btn = view.findViewById(R.id.filter_icon);
        me_btn = view.findViewById(R.id.me_icon);
        friends_btn = view.findViewById(R.id.friends_icon);
        distance_btn = view.findViewById(R.id.km_icon);


        fetchUserMoodEvents(loggedInUsername);


        me_btn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fetchUserMoodEvents(loggedInUsername);
                    }
                }
        );

        friends_btn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FetchFollowingUsers(loggedInUsername,false);
                    }
                }
        );

        distance_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchFollowingUsers(loggedInUsername,true);
            }
        }
        );



        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create FilterFragment and pass the current mood list
                FilterFragment filterFragment = new FilterFragment();
                Bundle args = new Bundle();
                args.putSerializable("moodEvents", moodHistoryList);
                filterFragment.setArguments(args);
                filterFragment.setTargetFragment(Map.this, 0);
                filterFragment.show(getParentFragmentManager(), "filter");
            }
        });

        return view;
    }

    /**
     * Fetches followed users and their public mood events
     * @param username User to get following list for
     * @param limit Whether to apply 5km distance filter
     */
    private void FetchFollowingUsers(String username, Boolean limit) { //freinds method 1
        if (username == null) {
            Log.e("Main Page", "No username found in SharedPreferences");
            return;
        }
        db.collection("User")
                .whereEqualTo("username", username)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        List<String> FollowedUsers = (List<String>) userDoc.get("following_list");

                        if (FollowedUsers != null && !FollowedUsers.isEmpty()) {
                            FetchPublicEvents(FollowedUsers,limit);
                        } else {
                            updateMapMarkers(new ArrayList<>(),"Friends");
                        }

                    } else {
                        Log.e("Main Page", "No user found with username: " + username);
                    }
                });
    }

    /**
     * Processes mood references with location-based filtering
     * @param moodRefs List of Firestore mood references
     * @param tempList Temporary storage for filtered moods
     * @param totalUsers Total followed users count
     * @param fetchedCount Completion counter
     * @param limit Enable distance filtering
     */
    private void FetchMoods(List<DocumentReference> moodRefs, ArrayList<MoodEvent> tempList, int totalUsers, int[] fetchedCount, Boolean limit) { //friends method 3
        ArrayList<MoodEvent> userMoodEvents = new ArrayList<>();
        final int[] moodsFetched = {0}; // Moods of this user
        final boolean[] locationFetched = {false}; // Flag to check if location is fetched

        if (moodRefs.isEmpty()) {
            fetchedCount[0]++;
            if (fetchedCount[0] == totalUsers) {
                updateMapMarkers(tempList, "Friends");
            }
            return;
        }

        for (DocumentReference moodRef : moodRefs) {
            moodRef.addSnapshotListener((documentSnapshot, error) -> {
                if (!isAdded()) return;
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    try {
                        MoodEvent moodEvent = documentSnapshot.toObject(MoodEvent.class);
                        if (moodEvent != null && !moodEvent.getPrivacy() && (moodEvent.getLocation() != null)) {
                            userMoodEvents.add(moodEvent);
                        }
                    } catch (Exception e) {
                    }
                }

                moodsFetched[0]++;

                if (moodsFetched[0] == moodRefs.size()) {
                    Log.e("HEER", "enetring if" );

                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
                    }

                        // Create LocationRequest with high accuracy
                        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                                .setMinUpdateIntervalMillis(5000)
                                .build();

                        locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                if (locationResult == null || locationResult.getLastLocation() == null) {
                                    return;
                                }

                                currentlatitude = locationResult.getLastLocation().getLatitude();
                                currentlongitude = locationResult.getLastLocation().getLongitude();
                                locationFetched[0] = true;

                                fusedLocationClient.removeLocationUpdates(locationCallback);

                                // Now that location is fetched, proceed with adding the moods
                                if (limit) {
                                    // Sort the mood events to get the most recent one
                                    ArrayList<MoodEvent> sortedMoodEvents = Sort(userMoodEvents);

                                    if (!sortedMoodEvents.isEmpty()) {
                                        // Get the most recent mood
                                        MoodEvent recentMood = sortedMoodEvents.get(0);
                                        double distance = calculateHaversineDistance(
                                                recentMood.getLocation().getLatitude(),
                                                recentMood.getLocation().getLongitude(),
                                                currentlatitude,
                                                currentlongitude
                                        );

                                        // Only add the recent mood if it's within 5km
                                        if (distance <= 5) {
                                            tempList.add(recentMood);
                                        }
                                    }
                                } else {
                                    tempList.addAll(userMoodEvents);
                                }

                                fetchedCount[0]++;

                                if (fetchedCount[0] == totalUsers) {
                                    moodHistoryList.clear();
                                    moodHistoryList.addAll(tempList);
                                    updateMapMarkers(moodHistoryList, "Friends");
                                }
                            }
                        };

                        // Request location updates
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());


                }
            });
        }
    }

    /**
     * Calculates distance between coordinates using Haversine formula
     * @param lat1 Start latitude
     * @param lng1 Start longitude
     * @param lat2 End latitude
     * @param lng2 End longitude
     * @return Distance in kilometers
     */
   private double calculateHaversineDistance(double lat1, double lng1, double lat2, double lng2) {
    final double R = 6371; // Earth’s radius in km
    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);

    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
               Math.sin(dLng / 2) * Math.sin(dLng / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c; // Distance in km
}

    /**
     * Aggregates public mood events from followed users
     * @param FollowedUsers List of usernames to fetch moods from
     * @param limit Apply distance constraint
     */
    private void FetchPublicEvents(List<String> FollowedUsers, Boolean limit) { //freinds method 2
        if (!isAdded()) return; // Stop if fragment is not attached
        Log.e("HEER", "Friends 2 cbeing callaed" );
        ArrayList<MoodEvent> tempList = new ArrayList<>();
        final int[] fetchedCount = {0}; // track total moods fetched

        if (FollowedUsers.isEmpty()) {
            updateMapMarkers(tempList,"Friends");
            return;
        }

        for (String FollowedUser : FollowedUsers) {
            db.collection("User")
                    .whereEqualTo("username", FollowedUser)
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                            List<DocumentReference> moodRefsList = (List<DocumentReference>) userDoc.get("MoodRef");

                            if (moodRefsList != null && !moodRefsList.isEmpty()) {;
                                FetchMoods(moodRefsList, tempList, FollowedUsers.size(), fetchedCount, limit);
                            } else {
                                fetchedCount[0]++;
                                if (fetchedCount[0] == FollowedUsers.size()) {
                                    moodHistoryList.clear();
                                    moodHistoryList = tempList;
                                    updateMapMarkers(moodHistoryList,"Friends");
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Fetches current user's mood events from Firestore
     * @param username User to retrieve moods for
     */
    private void fetchUserMoodEvents(String username) { // me method
        if (username == null) {
            Log.e("MoodHistory", "No username found in SharedPreferences");
            return;
        }

        db.collection("User")
                .whereEqualTo("username", username)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        Log.d("MoodHistory", "User found: " + userDoc.getData());
                        List<DocumentReference> moodRefsList = (List<DocumentReference>) userDoc.get("MoodRef");
                        if (moodRefsList != null && !moodRefsList.isEmpty()) {
                            fetchMoodEvents(moodRefsList); // Fetch the referenced mood events
                        }
                        else {
                            Log.d("MoodHistory", "No mood events found.");
                        }
                    }

                    else {
                        Log.e("MoodHistory", "No user found with username: " + username);
                    }
                });
    }

    /**
     * Loads mood events from Firestore references
     * @param moodRefs List of document references
     */
    private void fetchMoodEvents(List<DocumentReference> moodRefs) { // me method
        if (!isAdded()) return; // Stop if fragment is not attached

        ArrayList<MoodEvent> tempList = new ArrayList<>();
        final int[] fetchedCount = {0};

        for (DocumentReference moodRef : moodRefs) {
            moodRef.get().addOnSuccessListener(documentSnapshot -> {
                if (!isAdded()) return;

                if (documentSnapshot.exists()) {
                    try {
                        MoodEvent moodEvent = documentSnapshot.toObject(MoodEvent.class);
                        if (moodEvent != null) {
                            if (moodEvent.getPlace() != null){
                            tempList.add(moodEvent);}
                        }
                    } catch (Exception e) {
                        Log.e("MoodHistory", "Error converting document", e);
                    }
                }

                fetchedCount[0]++;
                if (fetchedCount[0] == moodRefs.size()) {
                    if (!isAdded()) return; // Stop if fragment is not attached
                    moodHistoryList.clear();
                    moodHistoryList.addAll(tempList);
                    updateMapMarkers(moodHistoryList, "Friends");
                }
            }).addOnFailureListener(e -> {
                Log.e("MoodHistory", "Error fetching document", e);
                fetchedCount[0]++;
            });
        }
    }

    /**
     * Redirects to login activity when authentication fails
     */
    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LogIn.class);
        startActivity(intent);
        requireActivity().finish();
    }

    /**
     * Creates map marker icon from drawable resource
     * @param drawableResId Image resource ID
     * @param width Icon width in pixels
     * @param height Icon height in pixels
     * @return BitmapDescriptor for map marker
     */
    private BitmapDescriptor getResizedBitmap(int drawableResId, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), drawableResId);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    /**
     * Updates map with current mood events
     * @param moods_list Mood events to display
     * @param display Mode for marker info ("Me" or "Friends")
     */
    private void updateMapMarkers(ArrayList<MoodEvent> moods_list, String display) {
        if (mMap == null || moods_list.isEmpty()) {
            Log.e("MAP", "updateMapMarkers: No mood events to display.");


        }
        mMap.clear();
        LatLng edmonton = new LatLng(53.5444, -113.4909);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(edmonton, 12); // Zoom level 12 is good for city view
        mMap.moveCamera(cameraUpdate);

        String descriptor = "";

        LatLng lastLocation = new LatLng(0, 0);

        for (MoodEvent each_mood : moods_list) {
            Log.e("top", "the loop is running " );

            if (each_mood.getLocation() != null){

                if (display.equals("Me")){
                    Log.e("CHECK", "display is equla to me" );
                    descriptor = each_mood.getEmotionalState();
                } else if (display.equals("Friends")){
                    Log.e("CHECK", "display is equla to freinds" );
                    descriptor = each_mood.getUsername();
                }

                Log.d("MAP", "Actual list size = " + moods_list.size());

                Log.d("MAP", "Mood: " + each_mood.getEmotionalState() + ", Location: " + each_mood.getLocation().getLatitude() + ", " + each_mood.getLocation().getLongitude());
            lastLocation = new LatLng(each_mood.getLocation().getLatitude(), each_mood.getLocation().getLongitude());
                int imageId = MoodType.getImageIdByMood(each_mood.getEmotionalState());
                Log.d("MAP", "updateMapMarkers:descriptor " + descriptor);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(lastLocation)
                        .title(each_mood.getEmotionalState())
                        .snippet(descriptor)
                        .icon(getResizedBitmap(imageId, 80, 80));

                mMap.addMarker(markerOptions).showInfoWindow();} // Keep track of last location
        }

        // Move camera to last mood event
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

    }

    /**
     * Configures GoogleMap instance when ready
     * @param googleMap Initialized map instance
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng edmonton = new LatLng(53.5461, -113.4938); // Edmonton's latitude & longitude
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 12));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

    }


    /**
     * Sorts moods by date/time in descending order
     * @param moodList List to sort
     * @return Chronologically sorted list
     */
    private ArrayList<MoodEvent> Sort(ArrayList<MoodEvent> moodList) {

        Log.e("BEFORE SORT", "Sort: " + moodList.size());

        ArrayList<MoodEvent> sortedList = new ArrayList<>(moodList);
        sortedList.sort((event1, event2) -> {
            try {
                LocalDate date1 = parseStringToDate(event1.getDate());
                LocalDate date2 = parseStringToDate(event2.getDate());

                int dateCompare = date2.compareTo(date1);
                if (dateCompare != 0) return dateCompare;

                LocalTime time1 = parseStringToTime(event1.getTime());
                LocalTime time2 = parseStringToTime(event2.getTime());
                return time2.compareTo(time1);
            } catch (Exception e) {
                Log.e("Sorting", "Error comparing events", e);
                return 0;
            }
        });
        Log.d("AFTER SORT", "size" + sortedList.size());
        sortedList = new ArrayList<>(sortedList.subList(0, Math.min(1, sortedList.size())));

        return sortedList;
    }

    /**
     * Parses date string to LocalDate
     * @param dateString Date in "dd/MM/yyyy" format
     * @return Parsed date or MIN on error
     */
    private LocalDate parseStringToDate(String dateString) {
        try {
            // Use pattern matching for "DD-MM-YYYY" format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            Log.e("MoodHistory", "Invalid date format: " + dateString, e);
            return LocalDate.MIN;
        }
    }

    /**
     * Parses a date string into a {@link LocalDate}.
     *
     * @param timeString The date string in 'HH:mm[:ss]' format
     * @return The parsed {@link LocalTime}, or {@link LocalTime#MIN} on error
     */
    private LocalTime parseStringToTime(String timeString) {
        try {
            // Handle both with and without seconds
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm[:ss]");
            return LocalTime.parse(timeString, formatter);
        } catch (Exception e) {
            Log.e("MoodHistory", "Invalid time format: " + timeString, e);
            return LocalTime.MIN;
        }
    }
}



