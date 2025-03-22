package com.example.donotredeem.Fragments;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.LogIn;
import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodType;
import com.example.donotredeem.R;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class Map extends Fragment implements OnMapReadyCallback, FilterFragment.FilterMoodListener {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private String loggedInUsername;

    public ArrayList<MoodEvent> moodHistoryList  = new ArrayList<>();;

    @Override
    public void filterMood(ArrayList<MoodEvent> filteredList) {
        Log.d("MAP", "Filtered list size = " + filteredList.size());

        for (MoodEvent event : filteredList) {
            Log.d("MAP", "Mood: " + event.getEmotionalState() + ", Location: " + event.getLocation().getLatitude() + ", " + event.getLocation().getLongitude());
        }
        mMap.clear();
        updateMapMarkers(filteredList,"Friends");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        db = FirebaseFirestore.getInstance();

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

        ImageButton filter_btn = view.findViewById(R.id.filter_icon);
        ImageButton me_btn = view.findViewById(R.id.me_icon);
        ImageButton friends_btn = view.findViewById(R.id.friends_icon);
        ImageButton distance_btn = view.findViewById(R.id.km_icon);


        me_btn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Show only personal moods on the map
                        fetchUserMoodEvents(loggedInUsername);
                    }
                }
        );

        friends_btn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Show only freinds mood events
                        FetchFollowingUsers(loggedInUsername);
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

    private void FetchFollowingUsers(String username) { //freinds method 1
        if (username == null) {
            Log.e("Main Page", "No username found in SharedPreferences");
            return;
        }

        db.collection("User")
                .whereEqualTo("username", username)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        Log.d("Main Page", "User found: " + userDoc.getData());
                        List<String> FollowedUsers = (List<String>) userDoc.get("Following");
                        if (FollowedUsers != null && !FollowedUsers.isEmpty()) {
                            FetchPublicEvents(FollowedUsers);
                        } else {
                            Log.d("Main Page", "No followed users.");
                            updateMapMarkers(new ArrayList<>(),"Friends");
                        }
                    } else {
                        Log.e("Main Page", "No user found with username: " + username);
                    }
                });
    }

    private void FetchMoods(List<DocumentReference> moodRefs, ArrayList<MoodEvent> tempList, int totalUsers, int[] fetchedCount) { //ffreidns method
        ArrayList<MoodEvent> userMoodEvents = new ArrayList<>();
        final int[] moodsFetched = {0}; // Moods of this user

        if (moodRefs.isEmpty()) {
            fetchedCount[0]++;
            if (fetchedCount[0] == totalUsers) {
                updateMapMarkers(tempList,"Friends");
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
                        Log.e("MoodHistory", "Error converting document", e);
                    }
                }

                moodsFetched[0]++;

                if (moodsFetched[0] == moodRefs.size()) { // All moods for this user are fetched
                    tempList.addAll(userMoodEvents);
                    fetchedCount[0]++;

                    if (fetchedCount[0] == totalUsers) {
                        moodHistoryList.clear();
                        moodHistoryList.addAll(tempList);
                        updateMapMarkers(moodHistoryList,"Friends");
                    }
                }
            });
        }
    }


    private void FetchPublicEvents(List<String> FollowedUsers) { //freinds method 2
        if (!isAdded()) return; // Stop if fragment is not attached

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

                            if (moodRefsList != null && !moodRefsList.isEmpty()) {
                                FetchMoods(moodRefsList, tempList, FollowedUsers.size(), fetchedCount);
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

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LogIn.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private BitmapDescriptor getResizedBitmap(int drawableResId, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), drawableResId);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    private void updateMapMarkers(ArrayList<MoodEvent> moods_list, String display) {
        if (mMap == null || moods_list.isEmpty()) {
            Log.e("MAP", "updateMapMarkers: No mood events to display.");
            return;
        }

        mMap.clear();

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


    @Override
    public void onMapReady(GoogleMap googleMap) { //edmonton me zooooooooooom lol not needed anyways
        mMap = googleMap;

        Log.d("API_KEY", "AIzaSyCoAZY3RwbhOJq-Dg1S3gAIOlIcQFfusnA");

        LatLng edmonton = new LatLng(53.5461, -113.4938); // Edmonton's latitude & longitude
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 12));




        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

    }
}
