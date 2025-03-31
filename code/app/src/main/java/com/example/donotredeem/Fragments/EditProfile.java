package com.example.donotredeem.Fragments;

import android.Manifest;
import android.app.DatePickerDialog;
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

import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.donotredeem.Classes.User;
import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.LogIn;
import com.example.donotredeem.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Fragment for editing the user's profile details such as username, email, phone number, and bio.
 * Retrieves user data from SharedPreferences and Firestore, allowing updates and saving changes.
 */
public class EditProfile extends Fragment {

    private EditText editUsername;
    private EditText editPassword;
    private EditText editEmail;
    private EditText bio;
    private Button done;
    private ImageButton cancel;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri imageUri;
    private ImageView profileImage;
    private ImageButton calander;
    private EditText date;
    private EditText contact;
    private Button delete;
    User userProfile = new User();

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
        editUsername.setEnabled(false);
        editUsername.setFocusable(false);
        editUsername.setClickable(false);
        editPassword = view.findViewById(R.id.password_desc);
        editEmail = view.findViewById(R.id.email_desc);
        bio = view.findViewById(R.id.bio_desc);
        cancel= view.findViewById(R.id.closeButton);
        profileImage = view.findViewById(R.id.profile_image);
        calander = view.findViewById(R.id.editProfileCalendarButton);
        date = view.findViewById(R.id.dob_desc);
        contact = view.findViewById(R.id.contact_desc);
        delete = view.findViewById(R.id.button3);
        profileImage.setOnClickListener(v -> showSourceDialog());

        UserProfileManager userProfileManager = new UserProfileManager();
        userProfileManager.getUserProfile(username, new UserProfileManager.OnUserProfileFetchListener() {
            @Override
            public boolean onUserProfileFetched(User user) {
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

        calander.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH); //january is 0
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view1, selectedYear, selectedMonth, selectedDay) -> {

                String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                date.setText(selectedDate);

            }, year, month, day
            );

            datePickerDialog.show();
        });

        done.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (editUsername.getText().toString().isEmpty() || editEmail.getText().toString().isEmpty()) {
                    //Toast.makeText(getContext(), "Please enter name and email", Toast.LENGTH_SHORT).show();
                    Snackbar.make(getView(), "Please enter name and email", Snackbar.LENGTH_SHORT).show();
                }
                // Get input values
                String contactInput = contact.getText().toString().trim();
                String dateInput = date.getText().toString().trim();

                // Validate contact number (10 digits)
                if (contactInput.isEmpty()) {
                    Snackbar.make(getView(), "Contact number cannot be empty", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (contactInput.length() != 10 || !contactInput.matches("\\d+")) {
                    Snackbar.make(getView(), "Invalid contact number (must be 10 digits)", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (!dateInput.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/\\d{4}$")) {
                    Snackbar.make(getView(), "Invalid date format (use dd/mm/yyyy)", Snackbar.LENGTH_SHORT).show();
                    return;
                }
//                userProfile.setUsername(editUsername.getText().toString());
                userProfile.setPassword(editPassword.getText().toString());
                userProfile.setEmail(editEmail.getText().toString());
                userProfile.setBio(bio.getText().toString());
                userProfile.setBirthdayDate(date.getText().toString());
                userProfile.setContact(contact.getText().toString());

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

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileManager manager = new UserProfileManager();
                manager.deleteUser(username, new UserProfileManager.OnDeleteListener() {
                    @Override
                    public void onSuccess() {
                        FirebaseAuth.getInstance().signOut();

                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        redirectToLogin();
                    }
                    @Override
                    public void onError(Exception e) {
                        // Handle error
                        Log.e("Deletion", "Error deleting user", e);
                    }
                });
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
    public void setDetails(User user){
        editUsername.setText(user.getUsername());
        editPassword.setText(user.getPassword());
        editEmail.setText(user.getEmail());
        bio.setText(user.getBio());
        date.setText(user.getBirthdayDate());
        contact.setText(user.getContact());

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

    /**
     * Checks if the camera permission is granted.
     * If granted, it launches the camera to capture an image.
     * If not granted, it requests the necessary camera permission.
     */
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();

        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

    }

    /**
     * Handles the result of the camera permission request.
     * If permission is granted, the camera is launched immediately.
     * If permission is denied, a Snack bar message is displayed to inform the user.
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Snackbar.make(requireView(), "Camera permission denied", Snackbar.LENGTH_SHORT).show();
                }
            });

    /**
     * Launches the device's camera to capture an image.
     * Creates a temporary file to store the captured image and provides a URI for it.
     * If an error occurs while creating the file, an error message is logged.
     */
    private void launchCamera() {
        try {

            File imageFile = createImageFile();
            imageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", imageFile);

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            cameraLauncher.launch(takePictureIntent);

        } catch (IOException e) {
            Log.e("CameraError", "Error creating image file", e);
        }
    }
    /**
     * Checks if the gallery permission is granted.
     * If granted, it launches the gallery to allow the user to pick an image.
     * If not granted, it requests the necessary gallery permission.
     */
    private void checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
            launchGallery();
        } else {
            requestGalleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        }
    }

    /**
     * Handles the result of the gallery permission request.
     * If permission is granted, the gallery is launched immediately.
     * If permission is denied, a Snackbar message is displayed to inform the user.
     */
    private final ActivityResultLauncher<String> requestGalleryPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchGallery();
                } else {
                    Snackbar.make(requireView(), "Gallery permission denied", Snackbar.LENGTH_SHORT).show();
                }
            });

    /**
     * Launches the device's gallery to allow the user to pick an image.
     * Starts an intent that opens the media storage for image selection.
     */
    private void launchGallery() {
        Intent galleryOpenIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryOpenIntent);
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
    private void uploadImageAndSaveMood(Uri imageUri, User u, UserProfileManager up) {
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

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LogIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

}
