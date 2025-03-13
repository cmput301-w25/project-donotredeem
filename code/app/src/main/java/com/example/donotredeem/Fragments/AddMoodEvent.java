package com.example.donotredeem.Fragments;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ToggleButton;

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

import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodType;
import com.example.donotredeem.R;
import com.example.donotredeem.SocialSituation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.FieldValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A Fragment that allows users to add a mood event, including selecting an emoji mood, social setting,
 * location, and uploading an image. The event details are saved to Firebase Firestore and Firebase Storage.
 */
public class AddMoodEvent extends Fragment {

    private ImageView image;
    private EditText location;
    private String selectedMoodName = null;
    private String selectedSocial = null;

    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;
    private static final int LOCATION_REQUEST = 300;

    private ImageButton selectedEmoji = null;
    private ImageButton selectedSocialButton = null;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    // Firebase variables
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri imageUri;
    private View fragmentRoot;

    public AddMoodEvent() {
        // Required empty public constructor
    }

    int[] emojiButtonIds = {
            R.id.emoji_happy, R.id.emoji_sad, R.id.emoji_fear,
            R.id.emoji_angry, R.id.emoji_confused, R.id.emoji_disgusted,
            R.id.emoji_shameful, R.id.emoji_surprised, R.id.emoji_shy,
            R.id.emoji_tired
    };

    int[] socialButtonIds = {R.id.alone_social, R.id.pair_social, R.id.crowd_social};

    /**
     * Initializes the fragment, sets up Firebase, and prepares image handling via camera and gallery.
     * @param savedInstanceState Saved instance state bundle.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AddMoodEvent", "onCreate called");

        // Initialize Firestore and Firebase Storage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("mood_images");

        // https://developer.android.com/develop/sensors-and-location/location/retrieve-current
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        //the code below is taken from https://developer.android.com/media/camera/camera-deprecated/photobasics
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                image.setImageURI(imageUri);

            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                image.setImageURI(imageUri);
            }
        });

    }

    /**
     * Creates a temporary image file to store captured images.
     * @return The created image file.
     * @throws IOException If file creation fails.
     */
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


    /**
     * Inflates the fragment's layout and sets up the user interface elements for mood event creation.
     * @param inflater The LayoutInflater to inflate the fragment's view.
     * @param container The parent view that the fragment's UI will be attached to.
     * @param savedInstanceState Saved instance state bundle.
     * @return The inflated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_mood, container, false);
        View fragmentRoot = view.findViewById(R.id.fragment_root);
        EditText description = view.findViewById(R.id.desc);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();
                int wordCount;

                if (input.isEmpty()) {
                    wordCount = 0;
                } else {
                    wordCount = input.split("\\s+").length;
                }

                int charCount = input.length();

                if (wordCount > 3 && charCount > 20) {
                    description.setError("Max 3 words or less than 20 characters");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        EditText addTrigger = view.findViewById(R.id.trigger);
        Button submit = view.findViewById(R.id.button);

        SwitchMaterial private_button;
        private_button = view.findViewById(R.id.private_button);

        image = view.findViewById(R.id.imageView);
        ImageButton media_upload = view.findViewById(R.id.upload_button);

        media_upload.setOnClickListener(v -> {
            showSourceDialog();

        });

        location = view.findViewById(R.id.loc);
        RadioButton location_button = view.findViewById(R.id.radioButton);

        final boolean[] isSelected_loc = {false};

        location_button.setOnClickListener(v -> {
            if (isSelected_loc[0]) {
                location.setText("");
                location_button.setChecked(false);
            } else {
                checkLocationPermission();
                location_button.setChecked(true);
            }

            isSelected_loc[0] = !isSelected_loc[0];
        });

        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && location_button.isChecked()) {
                    location_button.setChecked(false);
                    isSelected_loc[0] = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        EditText date = view.findViewById(R.id.date);
        RadioButton date_button = view.findViewById(R.id.dateButton);
        ImageButton calendar_button = view.findViewById(R.id.calendarButton);


        // this code is taken from - https://www.geeksforgeeks.org/how-to-get-current-time-and-date-in-android/
        final boolean[] isSelected_date = {false};

        date_button.setOnClickListener(v -> {
            if (isSelected_date[0]) {
                date.setText("");
                date_button.setChecked(false);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String currentDate = sdf.format(new Date());
                date.setText(currentDate);
                date_button.setChecked(true);
            }

            isSelected_date[0] = !isSelected_date[0];
        });

        calendar_button.setOnClickListener(v -> {

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
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && date_button.isChecked()) {
                    date_button.setChecked(false);
                    isSelected_date[0] = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        EditText time = view.findViewById(R.id.Time);
        RadioButton time_button = view.findViewById(R.id.timeButton);
        ImageButton clock_button = view.findViewById(R.id.clockButton);

        final boolean[] isSelected_time = {false};

        time_button.setOnClickListener(v -> {
            if (isSelected_time[0]) {
                time.setText("");
                time_button.setChecked(false);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String currentTime = sdf.format(new Date());
                time.setText(currentTime);
                time_button.setChecked(true);
            }

            isSelected_time[0] = !isSelected_time[0];
        });

        clock_button.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), (view1, selectedHour, selectedMinute) -> {

                String selectedTime = String.format("%02d:%02d",selectedHour, selectedMinute);
                time.setText(selectedTime);
            },
                    hour, minute, true // 24-hour format, false = am/pm
            );

            timePickerDialog.show();
        });

        time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && time_button.isChecked()) {
                    time_button.setChecked(false);
                    isSelected_time[0] = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        for (int id : socialButtonIds) {
            ImageButton socialButton = view.findViewById(id);
            socialButton.setOnClickListener(v -> highlightSelectedSocial((ImageButton) v));
        }


        for (int id : emojiButtonIds) {
            ImageButton emojiButton = view.findViewById(id);
            emojiButton.setOnClickListener(v -> highlightSelectedEmoji((ImageButton) v));
        }


        //View fragmentRoot = view.findViewById(R.id.fragment_root);

        submit.setOnClickListener(v -> {
            String descText = description.getText().toString();
            String triggerText = addTrigger.getText().toString();
            String dateText = date.getText().toString();
            String locationText = location.getText().toString();
            String timeText = time.getText().toString();

            Boolean privacy;
            if (private_button.isChecked()){
                privacy = true;
            }
            else {privacy = false;}
            Log.e("PRIVATE CHECK", privacy.toString());


            if (selectedMoodName == null || selectedMoodName.isEmpty()) {
                Snackbar.make(getView(), "Please select a mood!", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if ((descText.isEmpty()) && (imageUri == null)) {
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

            Log.e("PRIVATE CHECK", privacy.toString() );

            if (imageUri != null) {
                Log.d("AddMoodEvent", "Uploading image: " + imageUri.toString());
                uploadImageAndSaveMood(privacy,descText, triggerText, dateText, locationText, imageUri, selectedMoodName, selectedSocial, timeText);
            } else {
                Log.e("AddMoodEvent", "Image URI is null, cannot upload!");
                saveMoodToFirestore(privacy, descText, triggerText, dateText, locationText, null, selectedMoodName, selectedSocial, timeText);
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
//                //requireActivity().getSupportFragmentManager().popBackStack();
//            }
        });

        ImageButton close = view.findViewById(R.id.closeButton);

        close.setOnClickListener(v -> {
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
     * Displays a dialog to choose between camera or gallery options.
     * Opens the respective permission checks for camera or gallery.
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
     * Checks if the camera permission is granted. If granted, attempts to capture an image using the camera.
     * If not granted, requests camera permission.
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
     * Checks if the gallery permission is granted. If granted, opens the gallery to pick an image.
     * If not granted, requests gallery permission.
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
     * Checks if the location permission is granted. If granted, retrieves the user's current location and displays it.
     * If not granted, requests location permission.
     */
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
                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

                    List<Address> addresses = null;

                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    String Address = addresses.get(0).getAddressLine(0);

                    location.setText(Address);

                    fusedLocationClient.removeLocationUpdates(locationCallback); //stop location updates
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
     * Uploads an image to Firebase Storage and saves mood event data to Firestore.
     *
     * @param desc         Description of the mood event.
     * @param trigger      Trigger for the mood event.
     * @param date         Date of the mood event.
     * @param locationText Location of the mood event.
     * @param imageUri     URI of the image.
     * @param mood         The mood associated with the event.
     * @param social       Social situation associated with the event.
     * @param time         Time of the mood event.
     */
    private void uploadImageAndSaveMood(Boolean privacy, String desc, String trigger,
                                        String date, String locationText, Uri imageUri,
                                        String mood, String social, String time) {
            if (imageUri == null) {
                saveMoodToFirestore(privacy, desc, trigger, date, locationText, null, mood, social, time);
                return;
            }
            String imageFileName = UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storageRef.child(imageFileName);

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                Log.d("Upload", "Image uploaded, saving to Firestore: " + uri.toString());
                                saveMoodToFirestore(privacy, desc, trigger, date, locationText, uri.toString(), mood, social, time);
                            }))
                .addOnFailureListener(e ->
                    //Toast.makeText(getContext(), "Image upload failed!", Toast.LENGTH_SHORT).show()
                    Snackbar.make(getView(), "Image upload failed!", Snackbar.LENGTH_SHORT).show());
        }



        /**
         * Saves the mood event data to Firestore.
         *
         * @param desc         Description of the mood event.
         * @param trigger      Trigger for the mood event.
         * @param date         Date of the mood event.
         * @param locationText Location of the mood event.
         * @param imageUrl     URL of the uploaded image (can be null).
         * @param mood         The mood associated with the event.
         * @param social       Social situation associated with the event.
         * @param time         Time of the mood event.
         */
//        private void saveMoodToFirestore(String desc, String trigger,
//                                         String date, String locationText, String imageUrl,
//                                         String mood, String social, String time) {
//            // Generate a unique moodEventId
//            String moodEventId = UUID.randomUUID().toString();
//            DocumentReference moodEventRef = db.collection("MoodEvents").document(moodEventId);
//
//            MoodEvent moodEvent = new MoodEvent(moodEventId, mood, date, time, locationText, social, trigger, desc, imageUrl);
//            moodEventRef.set(moodEvent)
//                    .addOnSuccessListener(aVoid -> {
//                        //Toast.makeText(getContext(), "Mood Event Saved!", Toast.LENGTH_SHORT).show();
//                        //Snackbar.make(requireView(), "Mood Event Saved!", Snackbar.LENGTH_LONG).show();
//
//                        // Retrieve the logged-in username from SharedPreferences
//                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
//                        String loggedInUsername = sharedPreferences.getString("username", null);
//
//                        if (loggedInUsername != null) {
//                            DocumentReference userDocRef = db.collection("User").document(loggedInUsername);
//                            // Store the moodEventRef (DocumentReference) instead of a String
//                            userDocRef.update("MoodRef", FieldValue.arrayUnion(moodEventRef))
//                                    .addOnSuccessListener(aVoid1 -> Log.d(TAG, "User document updated with mood event reference"))
//                                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update user document", e));
//                        } else {
//                            Log.e(TAG, "Logged-in username not found in SharedPreferences");
//                        }
//                    })
//                    .addOnFailureListener(e ->
//                            //Toast.makeText(getContext(), "Error saving data!", Toast.LENGTH_SHORT).show());
//                            Snackbar.make(getView(), "Error saving data!", Snackbar.LENGTH_SHORT).show());
//        }
        private void saveMoodToFirestore(Boolean privacy, String desc, String trigger,
                                         String date, String locationText, String imageUrl,
                                         String mood, String social, String time) {

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            String loggedInUsername = sharedPreferences.getString("username", null);

            // Generate a unique moodEventId
            String moodEventId = UUID.randomUUID().toString();
            DocumentReference moodEventRef = db.collection("MoodEvents").document(moodEventId);

            MoodEvent moodEvent = new MoodEvent(loggedInUsername,privacy, moodEventId, mood, date, time, locationText, social, trigger, desc, imageUrl);

            // We'll wait for two tasks: saving the mood event and updating the user doc.
            final int totalTasks = 2;
            final int[] completedTasks = {0};

            moodEventRef.set(moodEvent)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Mood event saved!");
                        Snackbar.make(requireView(), "Mood Event Saved!", Snackbar.LENGTH_LONG).show();
                        incrementAndCheck(completedTasks, totalTasks);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving mood event", e);
                        showError("Error saving data!");
                    });

            if (isAdded() && getActivity() != null) {
//                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
//                String loggedInUsername = sharedPreferences.getString("username", null);


                if (loggedInUsername != null) {
                    DocumentReference userDocRef = db.collection("User").document(loggedInUsername);
                    userDocRef.update("MoodRef", FieldValue.arrayUnion(moodEventRef))
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "User document updated with mood event reference");
                                incrementAndCheck(completedTasks, totalTasks);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to update user document", e);
                                showError("Error updating user document!");
                            });
                } else {
                    Log.e(TAG, "Logged-in username not found in SharedPreferences");
                    showError("User not found!");
                }
            }
        }

    /**
     * Increments the counter and checks if all tasks have finished.
     */
    private void incrementAndCheck(int[] completedTasks, int totalTasks) {
        completedTasks[0]++;
        if (completedTasks[0] == totalTasks) {
            // All tasks complete – now run the exit animation and pop the fragment.
            popFragment();
        }
    }

    /**
     * Runs an exit animation and then pops the fragment from the back stack.
     */
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

    /**
     * Helper method to show error messages.
     */
    private void showError(String message) {
        if (isAdded() && getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }



    /**
     * Highlights the selected emoji button and resets the previous selection if any.
     * If the same emoji button is clicked again, it will unselect it.
     *
     * @param selected The selected ImageButton.
     */
    private void highlightSelectedEmoji(ImageButton selected) {
        if (selectedEmoji != null) {
            selectedEmoji.setBackground(null); // Remove highlight from previous selection
            selectedEmoji.setElevation(0);
        }

        if (selectedEmoji == selected) {
            selectedEmoji = null; // Unselect if clicking the same emoji
            selectedMoodName = null;  // Reset selected mood
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
     * Retrieves the MoodType for the given button ID.
     *
     * @param buttonId The ID of the selected button.
     * @return The corresponding MoodType.
     */
    private MoodType getMoodForButtonId(int buttonId) {

        for (int i = 0; i < emojiButtonIds.length; i++) {
            if (emojiButtonIds[i] == buttonId) {
                //Log.d("SelectedMood", "Mood for buttonId " + buttonId + ": " + selectedMood.getMood());
                return MoodType.values()[i];
            }
        }
        return null;
    }

    /**
     * Highlights the selected social situation button and resets the previous selection if any.
     * If the same social situation button is clicked again, it will unselect it.
     *
     * @param selected The selected ImageButton.
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
     * Retrieves the SocialSituation for the given button ID.
     *
     * @param buttonId The ID of the selected button.
     * @return The corresponding SocialSituation.
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