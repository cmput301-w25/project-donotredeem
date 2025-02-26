package com.example.donotredeem.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddMoodEvent extends Fragment {

    //private MoodType moods;
    //private Uri imageUri = null; //path to image from camera or gallery
    private ImageView image;
    private EditText location;

    //when you request something android doesn't automatically know which request it belongs to.
    //so you use a request code to match the response to the original request.

    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;
    private static final int LOCATION_REQUEST = 300;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback; //https://developer.android.com/develop/sensors-and-location/location/request-updates


    public AddMoodEvent() {

    }


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // https://developer.android.com/develop/sensors-and-location/location/retrieve-current
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        //the code below is taken from https://developer.android.com/media/camera/camera-deprecated/photobasics
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {

                Bundle extras = result.getData().getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                image.setImageBitmap(imageBitmap);

            }
        });

        // change bitmap to uri after database is added

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                image.setImageURI(imageUri);
            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_mood, container, false);

        EditText description = view.findViewById(R.id.desc);
        EditText socialSituation = view.findViewById(R.id.social);
        EditText addTrigger = view.findViewById(R.id.trigger);

        image = view.findViewById(R.id.imageView);
        ImageButton media_upload = view.findViewById(R.id.upload_button);

        media_upload.setOnClickListener(v -> {
            showSourceDialog();

        });

        location = view.findViewById(R.id.loc);
        RadioButton location_button = view.findViewById(R.id.radioButton);

        location_button.setOnClickListener(v -> {
            checkLocationPermission();
        });

        EditText date = view.findViewById(R.id.date);
        RadioButton date_button = view.findViewById(R.id.dateButton);

        date_button.setOnClickListener(v -> { // this code is taken from - https://www.geeksforgeeks.org/how-to-get-current-time-and-date-in-android/
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = sdf.format(new Date());
            date.setText(currentDate);
        });

        //USING QUERY FIND JO BHI USER IS UPLOADING AND STORING IN DATABASE - until then show it in mood historyyyy

        return view;
    }

    private void showSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_box_of_cam_gallery, null);

        builder.setView(dialogView);

        Button cameraButton = dialogView.findViewById(R.id.button_camera);
        Button galleryButton = dialogView.findViewById(R.id.button_gallery);

        AlertDialog dialog = builder.create();  // Store the dialog instance

        cameraButton.setOnClickListener(v -> {
            dialog.dismiss();
            checkCameraPermission();
        });

        galleryButton.setOnClickListener(v -> {
            dialog.dismiss();
            checkGalleryPermission();
        });

        dialog.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            //https://developer.android.com/media/camera/camera-intents
            //startActivityForResult is deprecated version

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE );
            cameraLauncher.launch(takePictureIntent);

        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);

            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
        }

    }

//add selected photo thing
    private void checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {

            Intent galleryOpenIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryOpenIntent);

        } else {

            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, GALLERY_REQUEST);

            Toast.makeText(requireContext(), "Gallery permission denied", Toast.LENGTH_SHORT).show();

        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // the code below is taken from https://developer.android.com/develop/sensors-and-location/location/change-location-settings
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                    .setMinUpdateIntervalMillis(5000)
                    .build();

            locationCallback = new LocationCallback() {

                //https://developer.android.com/develop/sensors-and-location/location/request-updates
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    String currentLocation = latitude + ", " + longitude;
                    location.setText(currentLocation);

                    fusedLocationClient.removeLocationUpdates(locationCallback); //stop location updates
                }

            };

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null); //

        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);

            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }


}




