package com.example.donotredeem;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.MoodType;
import com.example.donotredeem.R;
import com.google.firebase.auth.FirebaseAuth;

public class AddMoodEvent extends Fragment {

    private MoodType moods;
    private Uri imageUri = null; //path to image from camera or gallery
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private ImageView imageView;
    private Button mediaUploadButton;

    public AddMoodEvent() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_mood, container, false);

        // Initialize UI components
        EditText description = view.findViewById(R.id.desc);
        EditText socialSituation = view.findViewById(R.id.social);
        EditText addTrigger = view.findViewById(R.id.trigger);
        EditText date = view.findViewById(R.id.date);
        EditText location = view.findViewById(R.id.loc);
        imageView = view.findViewById(R.id.image);
        mediaUploadButton = view.findViewById(R.id.upload);
        
        mediaUploadButton.setOnClickListener(v -> checkCameraPermission());

        return view;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //intent allows interaction between different components of an app or between different apps.
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        } else {
            //request camera permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        }
    }
}
