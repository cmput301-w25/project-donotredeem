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
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.donotredeem.MoodType;
import com.example.donotredeem.R;
import com.google.firebase.auth.FirebaseAuth;

public class AddMoodEvent extends Fragment {

    //private MoodType moods;
    //private Uri imageUri = null; //path to image from camera or gallery
    private ImageView image;

   //when you request something android doesn't automatically know which request it belongs to.
   //so you use a request code to match the response to the original request.

    //permission requests
    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    public AddMoodEvent() {

    }

    //how to launch an activity and define its result
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this below code is taken from https://developer.android.com/media/camera/camera-deprecated/photobasics

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
        EditText date = view.findViewById(R.id.date);
        EditText location = view.findViewById(R.id.loc);
        image = view.findViewById(R.id.imageView);
        ImageButton media_upload = view.findViewById(R.id.upload_button);

        media_upload.setOnClickListener(v -> {
            showSourceDialog();
        });


        //USING QUERY FIND JO BHI USER IS UPLOADING AND STORING IN DATABASE

        return view;
    }

    private void showSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_box_of_cam_gallery, null);

        builder.setView(dialogView);

        Button cameraButton = dialogView.findViewById(R.id.button_camera);
        Button galleryButton = dialogView.findViewById(R.id.button_gallery);

        cameraButton.setOnClickListener(v -> {
            checkCameraPermission();
        });

        galleryButton.setOnClickListener(v -> {
            checkGalleryPermission();
        });
        builder.create()
                .show();
    }



    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {


            //https://developer.android.com/media/camera/camera-intents
            //startActivityForResult is deprecated version

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //action of same or diff app
            cameraLauncher.launch(takePictureIntent);

        } else {
            //request camera permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        }
    }

    private void checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {

            Intent galleryOpenIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryOpenIntent);

        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, GALLERY_REQUEST);
        }
    }

}
