package com.example.donotredeem.Fragments;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Map extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { //edmonton me zooooooooooom lol not needed anyways
        mMap = googleMap;

        Log.d("API_KEY", "AIzaSyCoAZY3RwbhOJq-Dg1S3gAIOlIcQFfusnA");


        LatLng myLocation = new LatLng(53.5245, -113.5246);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("SJCCCCC"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
