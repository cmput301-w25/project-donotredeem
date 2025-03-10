package com.example.donotredeem.Fragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodEventAdapter;
import com.example.donotredeem.MoodType;
import com.example.donotredeem.R;
import com.example.donotredeem.SocialSituation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * A Fragment that allows users to edit an existing mood event. This fragment provides functionality to modify mood details, including description, location, date, time, and trigger.
 * It also allows users to upload a new image, select a mood emoji, and specify the social context of the event.
 * This fragment interacts with Firebase Firestore to update the mood event in the database and Firebase Storage to upload images.
 */
public class EditMoodEvent extends Fragment {
    public int FromWhere;
    private ImageView image;
    private EditText description, locationEdit, dateEdit, timeEdit, triggerEdit;
    private Button submit;
    private ImageButton mediaUpload, calendarButton, clockButton, closeButton;
    private RadioButton locationButton, dateButton, timeButton;

    // Variables to store selected values
    private String selectedMoodName = null;
    private String selectedSocial = null;

    // Request codes
    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;
    private static final int LOCATION_REQUEST = 300;

    // ActivityResultLaunchers for camera and gallery
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    // Location variables
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    // Firebase variables
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri imageUri;

    private View fragmentRoot;

    // For emoji/social selection (IDs as in AddMoodEvent)
    int[] emojiButtonIds = {
            R.id.emoji_happy, R.id.emoji_sad, R.id.emoji_fear,
            R.id.emoji_angry, R.id.emoji_confused, R.id.emoji_disgusted,
            R.id.emoji_shameful, R.id.emoji_surprised, R.id.emoji_shy,
            R.id.emoji_tired
    };
    int[] socialButtonIds = {R.id.alone_social, R.id.pair_social, R.id.crowd_social};
    private ImageButton selectedEmoji = null;
    private ImageButton selectedSocialButton = null;

    // To hold the original moodEventId for update
    private String moodEventId;

    private String firebaseImageUrl = null;

    /**
     * Default constructor for the EditMoodEvent fragment.
     */
    public EditMoodEvent() {
        // Required empty public constructor
    }

    /**
     * Initializes the fragment by setting up Firebase, location services, and activity result launchers.
     * This method is called when the fragment is created.
     *
     * @param savedInstanceState Bundle containing any saved state for the fragment.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("mood_images");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Setup camera launcher
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                image.setImageURI(imageUri);
            }
        });

        // Setup gallery launcher
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                image.setImageURI(imageUri);
            }
        });
    }

    /**
     * Inflates the layout for the fragment and binds all UI elements (buttons, text fields, etc.) to the appropriate views.
     * Also pre-populates fields with the data from the passed arguments.
     *
     * @param inflater           The LayoutInflater used to inflate the fragment's view.
     * @param container          The container in which the fragment's UI will be attached.
     * @param savedInstanceState Bundle containing any saved instance state.
     * @return The inflated and populated view for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout (assumed to be similar to add_mood.xml)
        View view = inflater.inflate(R.layout.edit_mood, container, false);
        View fragmentRoot = view.findViewById(R.id.fragment_root_edit);

        // Bind UI elements
        description = view.findViewById(R.id.desc);
        locationEdit = view.findViewById(R.id.loc);
        dateEdit = view.findViewById(R.id.date);
        timeEdit = view.findViewById(R.id.Time);
        triggerEdit = view.findViewById(R.id.trigger);
        image = view.findViewById(R.id.imageView);
        submit = view.findViewById(R.id.button);
        mediaUpload = view.findViewById(R.id.upload_button);
        calendarButton = view.findViewById(R.id.calendarButton);
        clockButton = view.findViewById(R.id.clockButton);
        locationButton = view.findViewById(R.id.radioButton);
        dateButton = view.findViewById(R.id.dateButton);
        timeButton = view.findViewById(R.id.timeButton);
        closeButton = view.findViewById(R.id.closeButton);

        // Pre-populate values from passed arguments
        Bundle args = getArguments();
        if (args != null) {
            moodEventId = args.getString("moodEventId");
            String emotionalState = args.getString("emotionalState");
            String date = args.getString("date");
            String time = args.getString("time");
            String place = args.getString("place");
            String situation = args.getString("situation");
            String trigger = args.getString("trigger");
            String explainText = args.getString("explainText");
            String explainPicture = args.getString("explainPicture");

            // For this example, assume selectedMoodName comes from emotionalState
            selectedMoodName = emotionalState;

            if (!TextUtils.isEmpty(explainText)) {
                description.setText(explainText);
            }
            if (!TextUtils.isEmpty(date)) {
                dateEdit.setText(date);
            }
            if (!TextUtils.isEmpty(time)) {
                timeEdit.setText(time);
            }
            if (!TextUtils.isEmpty(place)) {
                locationEdit.setText(place);
            }
            if (!TextUtils.isEmpty(trigger)) {
                triggerEdit.setText(trigger);
            }
            if (!TextUtils.isEmpty(explainPicture)) {
                Glide.with(this)
                        .load(explainPicture)
                        .into(image);
                // If the URL is already a Firebase Storage URL, save it for later use
                if (explainPicture.startsWith("https://firebasestorage.googleapis.com/")) {
                    firebaseImageUrl = explainPicture;
                } else {
                    imageUri = Uri.parse(explainPicture); // Use only if it's a local Uri
                }
            }

            if (!TextUtils.isEmpty(emotionalState)) {
                selectedMoodName = emotionalState;
                for (int id : emojiButtonIds) {
                    ImageButton btn = view.findViewById(id);
                    MoodType moodType = getMoodForButtonId(id);
                    if (moodType != null && moodType.getMood().equalsIgnoreCase(emotionalState)) {
                        highlightSelectedEmoji(btn);
                        break;
                    }
                }
            }


            if (!TextUtils.isEmpty(situation)) {
                for (int id : socialButtonIds) {
                    ImageButton btn = view.findViewById(id);
                    SocialSituation socialSituation = getSocialSituationForButtonId(id);
                    if (socialSituation != null && socialSituation.getLabel().equalsIgnoreCase(situation)) {
                        highlightSelectedSocial(btn);
                        break;
                    }
                }
            }

        }

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();
                int wordCount = input.isEmpty() ? 0 : input.split("\\s+").length;
                int charCount = input.length();
                if (wordCount > 3 && charCount > 20) {
                    description.setError("Max 3 words or less than 20 characters");
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        mediaUpload.setOnClickListener(v -> showSourceDialog());

        // Location handling
        locationButton.setOnClickListener(v -> {
            if (locationButton.isChecked()) {
                checkLocationPermission();
            } else {
                locationEdit.setText("");
            }
        });

        locationEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && locationButton.isChecked()) {
                    locationButton.setChecked(false);
                }
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        // Date handling
        dateButton.setOnClickListener(v -> {
            if (dateButton.isChecked()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String currentDate = sdf.format(new Date());
                dateEdit.setText(currentDate);
            } else {
                dateEdit.setText("");
            }
        });

        calendarButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        dateEdit.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        dateEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && dateButton.isChecked()) {
                    dateButton.setChecked(false);
                }
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        // Time handling
        timeButton.setOnClickListener(v -> {
            if (timeButton.isChecked()) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String currentTime = sdf.format(new Date());
                timeEdit.setText(currentTime);
            } else {
                timeEdit.setText("");
            }
        });

        clockButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                    (view1, selectedHour, selectedMinute) -> {
                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                        timeEdit.setText(selectedTime);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        timeEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && timeButton.isChecked()) {
                    timeButton.setChecked(false);
                }
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        // Emoji and social button handling
        for (int id : socialButtonIds) {
            ImageButton socialButton = view.findViewById(id);
            socialButton.setOnClickListener(v -> highlightSelectedSocial(socialButton));
        }
        for (int id : emojiButtonIds) {
            ImageButton emojiButton = view.findViewById(id);
            emojiButton.setOnClickListener(v -> highlightSelectedEmoji(emojiButton));
        }


        submit.setOnClickListener(v -> {

            //View fragmentRoot = view.findViewById(R.id.fragment_root_edit);
            String descText = description.getText().toString();
            String triggerText = triggerEdit.getText().toString();
            String dateText = dateEdit.getText().toString();
            String locationText = locationEdit.getText().toString();
            String timeText = timeEdit.getText().toString();

            if (selectedMoodName == null || selectedMoodName.isEmpty()) {
                Snackbar.make(getView(), "Please select a mood!", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if ((descText.isEmpty()) && (imageUri == null && firebaseImageUrl == null)) {
                Snackbar.make(getView(), "Provide either a description or an image!", Snackbar.LENGTH_LONG).show();
                return;
            }
            if (dateText.isEmpty()) {
                Snackbar.make(getView(), "Please select a date!", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (timeText.isEmpty()) {
                Snackbar.make(getView(), "Please select a time!", Snackbar.LENGTH_SHORT).show();
                return;
            }

            // Always update the mood event:
            // If a new image was selected, upload it first; otherwise, update using the existing firebaseImageUrl.
            if (imageUri != null) {
                Log.d("EditMoodEvent", "Uploading image: " + imageUri.toString());
                uploadImageAndUpdateMood();
            } else {
                updateMoodEventInFirestore(descText, triggerText, dateText, locationText, firebaseImageUrl,
                        selectedMoodName, selectedSocial, timeText);
            }

//            if (fragmentRoot != null) {
//                Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_bottom);
//                fragmentRoot.startAnimation(slideOut);
//
//                slideOut.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {}
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        requireActivity().getSupportFragmentManager().popBackStack(); //go back to whatever it was bruh
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {}
//                });
//            } else {
//                requireActivity().getSupportFragmentManager().popBackStack();
//            }

        });


        closeButton.setOnClickListener(v -> {
            //View fragmentRoot = view.findViewById(R.id.fragment_root_edit);

            if (fragmentRoot != null) {
                Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_bottom);
                fragmentRoot.startAnimation(slideOut);

                slideOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        getParentFragmentManager().popBackStack(); //go to previous fragment
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                        }
                    });

                } else {
                    getParentFragmentManager().popBackStack();
                }

                Snackbar.make(getView(), "Mood event not saved!", Snackbar.LENGTH_LONG).show();
            });


        return view;
    }

    /**
     * Displays a dialog to allow the user to choose the source of an image (camera or gallery).
     * The dialog includes two buttons: one for capturing a photo using the camera and one for selecting an image from the gallery.
     */
    private void showSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
     * Checks whether the app has permission to access the camera.
     * If permission is granted, it launches the camera intent to capture an image.
     * If permission is not granted, it requests the necessary permission from the user.
     */
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
            //Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            Snackbar.make(getView(), "Camera permission denied", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks whether the app has permission to access the gallery.
     * If permission is granted, it opens the gallery to select an image.
     * If permission is not granted, it requests the necessary permission from the user.
     */
    private void checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
            Intent galleryOpenIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryOpenIntent);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, GALLERY_REQUEST);
            //Toast.makeText(requireContext(), "Gallery permission denied", Toast.LENGTH_SHORT).show();
            Snackbar.make(getView(), "Gallery permission denied", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks whether the app has permission to access the user's location.
     * If permission is granted, it retrieves the current location and updates the location EditText.
     * If permission is not granted, it requests the necessary permission from the user.
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                    .setMinUpdateIntervalMillis(5000)
                    .build();
            locationCallback = new com.google.android.gms.location.LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    android.location.Geocoder geocoder = new android.location.Geocoder(requireContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        String Address = addresses.get(0).getAddressLine(0);
                        locationEdit.setText(Address);
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            //Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            Snackbar.make(getView(), "Location permission denied", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a temporary image file for storing the captured image.
     * This method is used by the camera intent.
     *
     * @return A File object representing the created image file.
     * @throws IOException If an error occurs while creating the image file.
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d("ImageFile", "Image file created: " + imageFile.getAbsolutePath());
        imageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", imageFile);
        Log.d("ImageUri", "Image URI: " + imageUri.toString());
        return imageFile;
    }

    /**
     * Uploads a new image to Firebase Storage and updates the associated mood event in Firestore.
     * If no new image is selected, it updates the event with the existing image URL.
     */
    private void uploadImageAndUpdateMood() {
        // If imageUri is null, use the existing firebaseImageUrl
        if (imageUri == null) {
            Log.d("Firebase", "No new image to upload, updating event with existing image.");
            updateMoodEventInFirestore(
                    description.getText().toString(),
                    triggerEdit.getText().toString(),
                    dateEdit.getText().toString(),
                    locationEdit.getText().toString(),
                    firebaseImageUrl,
                    selectedMoodName,
                    selectedSocial,
                    timeEdit.getText().toString());
            return;
        }

        String imageUrl = imageUri.toString();

        // If the image is already uploaded, update without re-uploading
        if (imageUrl.startsWith("https://firebasestorage.googleapis.com/")) {
            Log.d("Firebase", "Image is already uploaded, skipping re-upload.");
            updateMoodEventInFirestore(
                    description.getText().toString(),
                    triggerEdit.getText().toString(),
                    dateEdit.getText().toString(),
                    locationEdit.getText().toString(),
                    imageUrl,
                    selectedMoodName,
                    selectedSocial,
                    timeEdit.getText().toString());
            return;
        }

        StorageReference newStorageRef = FirebaseStorage.getInstance().getReference()
                .child("mood_images/" + System.currentTimeMillis() + ".jpg");

        newStorageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> newStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d("Firebase", "Image uploaded successfully: " + uri.toString());
                    updateMoodEventInFirestore(
                            description.getText().toString(),
                            triggerEdit.getText().toString(),
                            dateEdit.getText().toString(),
                            locationEdit.getText().toString(),
                            uri.toString(),
                            selectedMoodName,
                            selectedSocial,
                            timeEdit.getText().toString());
                }))
                .addOnFailureListener(e -> Log.e("Firebase", "Image upload failed", e));
    }

    /**
     * Updates the mood event in Firestore with the provided information.
     *
     * @param desc The description of the mood event.
     * @param trigger The trigger associated with the mood event.
     * @param date The date of the mood event.
     * @param locationText The location of the mood event.
     * @param imageUrl The URL of the image associated with the mood event.
     * @param mood The selected mood of the event.
     * @param social The social situation of the event.
     * @param time The time of the event.
     */
//    private void updateMoodEventInFirestore(String desc, String trigger,
//                                            String date, String locationText, String imageUrl,
//                                            String mood, String social, String time) {
//        if (moodEventId == null) {
//            Log.e(TAG, "MoodEventId is null, cannot update");
//            return;
//        }
//        DocumentReference moodEventRef = db.collection("MoodEvents").document(moodEventId);
//        MoodEvent updatedMoodEvent = new MoodEvent(moodEventId, mood, date, time, locationText, social, trigger, desc, imageUrl);
//        moodEventRef.set(updatedMoodEvent)
//                .addOnSuccessListener(aVoid -> {
//                    //Toast.makeText(getContext(), "Mood Event Updated!", Toast.LENGTH_SHORT).show();
//                    Snackbar.make(getView(), "Mood Event Updated!", Snackbar.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e ->
//                        //Toast.makeText(getContext(), "Error updating data!", Toast.LENGTH_SHORT).show());
//                        Snackbar.make(getView(), "Error updating data!", Snackbar.LENGTH_SHORT).show());
//    }
    private void updateMoodEventInFirestore(String desc, String trigger,
                                            String date, String locationText, String imageUrl,
                                            String mood, String social, String time) {
        if (moodEventId == null) {
            Log.e(TAG, "MoodEventId is null, cannot update");
            showError("Mood Event ID is missing!");
            return;
        }
        DocumentReference moodEventRef = db.collection("MoodEvents").document(moodEventId);
        MoodEvent updatedMoodEvent = new MoodEvent(moodEventId, mood, date, time, locationText, social, trigger, desc, imageUrl);

        final int totalTasks = 1;
        final int[] completedTasks = {0};

        moodEventRef.set(updatedMoodEvent)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Mood event updated!");
                    Snackbar.make(requireView(), "Mood Event Updated!", Snackbar.LENGTH_LONG).show();
                    incrementAndCheck(completedTasks, totalTasks);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating mood event", e);
                    showError("Error updating data!");
                    return;
                });

//        if (isAdded() && getActivity() != null) {
//            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
//            String loggedInUsername = sharedPreferences.getString("username", null);
//
//            if (loggedInUsername != null) {
//                DocumentReference userDocRef = db.collection("User").document(loggedInUsername);
//                userDocRef.update("MoodRef", FieldValue.arrayUnion(moodEventRef))
//                        .addOnSuccessListener(aVoid -> {
//                            Log.d(TAG, "User document updated with mood event reference");
//                            incrementAndCheck(completedTasks, totalTasks);
//                        })
//                        .addOnFailureListener(e -> {
//                            Log.e(TAG, "Failed to update user document", e);
//                            showError("Error updating user document!");
//                        });
//            } else {
//                Log.e(TAG, "Logged-in username not found in SharedPreferences");
//                showError("User not found!");
//            }
//        }
    }

    private void incrementAndCheck(int[] completedTasks, int totalTasks) {
        completedTasks[0]++;
        if (completedTasks[0] == totalTasks) {
            popFragment();
        }
    }

    private void popFragment() {
        if (isAdded() && getActivity() != null) {
            requireActivity().runOnUiThread(() -> {
                if (fragmentRoot != null) {
                    Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_bottom);
                    fragmentRoot.startAnimation(slideOut);
                    slideOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isAdded() && getActivity() != null) {
                                requireActivity().getSupportFragmentManager().popBackStack();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                } else {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    private void showError(String message) {
        if (isAdded() && getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }


    /**
     * Highlights the selected emoji button and updates the selected mood name.
     * If the same emoji is selected, it is unselected.
     *
     * @param selected The ImageButton representing the selected emoji.
     */
    private void highlightSelectedEmoji(ImageButton selected) {
        if (selectedEmoji != null) {
            selectedEmoji.setBackground(null);
            selectedEmoji.setElevation(0);
        }
        if (selectedEmoji == selected) {
            selectedEmoji = null;
            selectedMoodName = null;
        } else {
            selected.setBackgroundResource(R.drawable.highlight_background);
            selected.setElevation(8);
            selectedEmoji = selected;
            int buttonId = selected.getId();
            MoodType selectedMood = getMoodForButtonId(buttonId);
            if (selectedMood != null) {
                selectedMoodName = selectedMood.getMood();
            }
        }
    }

    /**
     * Retrieves the mood associated with the specified button ID.
     *
     * @param buttonId The ID of the selected emoji button.
     * @return The corresponding MoodType for the button ID.
     */
    private MoodType getMoodForButtonId(int buttonId) {
        for (int i = 0; i < emojiButtonIds.length; i++) {
            if (emojiButtonIds[i] == buttonId) {
                return MoodType.values()[i];
            }
        }
        return null;
    }


    /**
     * Highlights the selected social button and updates the selected social situation.
     * If the same social situation is selected, it is unselected.
     *
     * @param selected The ImageButton representing the selected social button.
     */
    private void highlightSelectedSocial(ImageButton selected) {
        if (selectedSocialButton != null) {
            selectedSocialButton.setBackground(null);
            selectedSocialButton.setElevation(0);
        }
        if (selectedSocialButton == selected) {
            selectedSocialButton = null;
            selectedSocial = null;
        } else {
            selected.setBackgroundResource(R.drawable.highlight_background);
            selected.setElevation(8);
            selectedSocialButton = selected;
            int buttonId = selected.getId();
            SocialSituation selectedSituation = getSocialSituationForButtonId(buttonId);
            if (selectedSituation != null) {
                selectedSocial = selectedSituation.getLabel();
            }
        }
    }

    /**
     * Retrieves the social situation associated with the specified button ID.
     *
     * @param buttonId The ID of the selected social button.
     * @return The corresponding SocialSituation for the button ID.
     */
    private SocialSituation getSocialSituationForButtonId(int buttonId) {
        for (int i = 0; i < socialButtonIds.length; i++) {
            if (socialButtonIds[i] == buttonId) {
                return SocialSituation.values()[i];
            }
        }
        return null;
    }

}
