package com.example.donotredeem.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import de.hdodenhof.circleimageview.CircleImageView;

import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.Classes.Users;
import com.example.donotredeem.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Fragment for editing the user's profile details such as username, email, phone number, and bio.
 * Retrieves user data from SharedPreferences and Firestore, allowing updates and saving changes.
 */
public class EditProfile extends Fragment {

    String username;

    private EditText editUsername;
    private EditText editPassword;
    private EditText editPhoneNumber;
    private EditText editEmail;
    private EditText bio;
    private Button done;
    private ImageButton cancel;
    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri imageUri;
    private ImageView profileImage;
    Users userProfile = new Users();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("profile_images");


        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                profileImage.setImageURI(imageUri);
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                profileImage.setImageURI(imageUri);
            }
        });
    }


    /**
     * Inflates the fragment layout, initializes UI components, and retrieves user details.
     *
     * @param inflater  The LayoutInflater used to inflate the layout.
     * @param container The parent ViewGroup (if applicable).
     * @param savedInstanceState The saved state of the fragment (if any).
     * @return The root View of the fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        done =view.findViewById(R.id.button2);
        editUsername = view.findViewById(R.id.username_desc);
        editPassword = view.findViewById(R.id.password_desc);
        editEmail = view.findViewById(R.id.email_desc);
        bio = view.findViewById(R.id.bio_desc);
        cancel= view.findViewById(R.id.closeButton);
        profileImage = view.findViewById(R.id.profile_image);

        profileImage.setOnClickListener(v -> showSourceDialog());

        UserProfileManager userProfileManager = new UserProfileManager();
        userProfileManager.getUserProfile(username, new UserProfileManager.OnUserProfileFetchListener() {
            @Override
            public boolean onUserProfileFetched(Users user) {
                setDetails(user);
                userProfile = user;
                return false;
            }
            @Override
            public void onUserProfileFetchError(Exception e) {
                //oast.makeText(getContext(), "Failed to fetch profile", Toast.LENGTH_SHORT).show();
                Snackbar.make(getView(), "Failed to fetch profile", Snackbar.LENGTH_SHORT).show();
            }
        });

        done.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (editUsername.getText().toString().isEmpty() || editEmail.getText().toString().isEmpty()) {
                    //Toast.makeText(getContext(), "Please enter name and email", Toast.LENGTH_SHORT).show();
                    Snackbar.make(getView(), "Please enter name and email", Snackbar.LENGTH_SHORT).show();
                }
                userProfile.setUsername(editUsername.getText().toString());
                userProfile.setPassword(editPassword.getText().toString());
                userProfile.setEmail(editEmail.getText().toString());
                userProfile.setBio(bio.getText().toString());

                if (imageUri != null) {
                    if (username != null) {
                    uploadImageAndSaveMood(imageUri, userProfile, userProfileManager);
                    Log.d("DEBUG", "Updating user with username: " + username);}

                } else {
                    userProfileManager.updateUser(userProfile, username);
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();



            }
        });

        cancel.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });
        return view;
    }

    /**
     * Populates the UI fields with the user's existing details.
     *
     * @param user The user object containing profile details.
     */
    public void setDetails(Users user){
        editUsername.setText(user.getUsername());
        editPassword.setText(user.getPassword());
        editEmail.setText(user.getEmail());
        bio.setText(user.getBio());

        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getProfilePictureUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(profileImage);
        }

    }

    private void showSourceDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_box_of_cam_gallery, null);

        builder.setView(dialogView);

        Button cameraButton = dialogView.findViewById(R.id.button_camera);
        Button galleryButton = dialogView.findViewById(R.id.button_gallery);

        AlertDialog dialog = builder.create();

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
            try {

                File imageFile = createImageFile();
                imageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", imageFile);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraLauncher.launch(takePictureIntent);

            } catch (IOException e) {
                Log.e("CameraError", "Error creating image file", e);
            }

        } else {
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

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        Log.d("ImageFile", "Image file created: " + image.getAbsolutePath());

        imageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", image);
        Log.d("ImageUri", "Image URI: " + imageUri.toString());

        return image;
    }
    private void uploadImageAndSaveMood(Uri imageUri, Users u,  UserProfileManager up) {
        String username = u.getUsername();

        String imageFileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child(imageFileName);


        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Log.d("Upload", "Image uploaded, saving to Firestore: " + uri.toString());

                            u.setProfilePictureUrl(uri.toString());
                            up.updateUser(u, username);
                        }))
                .addOnFailureListener(e ->
                        Snackbar.make(getView(), "Image upload failed!", Snackbar.LENGTH_SHORT).show());
    }

}
