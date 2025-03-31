package com.example.donotredeem.Fragments;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import com.example.donotredeem.Classes.NetworkUtils;
import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodType;
import com.example.donotredeem.R;
import com.example.donotredeem.SocialSituation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.FieldValue;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * A Fragment that allows users to add a mood event with various details including mood type, social setting,
 * location, date/time, and image upload. Manages data synchronization with Firebase Firestore and Storage.
 */
public class AddMoodEvent extends Fragment {
    // UI Components
    private ImageView image;
    private EditText location;
    private GeoPoint selectedGeoPoint;

    // Selection State
    private String selectedMoodName = null;
    private String selectedSocial = null;

    // Permission Request Codes
    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;
    private static final int LOCATION_REQUEST = 300;

    // UI State
    private ImageButton selectedEmoji = null;
    private ImageButton selectedSocialButton = null;

    // Activity Result Handlers
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> placeAutocompleteLauncher;

    // Location Services
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    // Firebase variables
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri imageUri;
    private View fragmentRoot;

    /**
     * Required empty public constructor for fragment instantiation
     */
    public AddMoodEvent() {
        // Required empty public constructor
    }

    int[] emojiButtonIds = {
            R.id.emoji_happy, R.id.emoji_sad, R.id.emoji_fear,
            R.id.emoji_angry, R.id.emoji_confused, R.id.emoji_disgusted,
            R.id.emoji_shameful, R.id.emoji_surprised, R.id.emoji_shy,
            R.id.emoji_tired
    };

    int[] socialButtonIds = {R.id.alone_social, R.id.pair_social, R.id.few_social, R.id.crowd_social};

    /**
     * Initializes fragment components and Firebase services. Sets up activity result handlers
     * for camera, gallery, and location selection.
     *
     * @param savedInstanceState If non-null, fragment is being re-constructed from previous state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize location services only if network is available
        Context context = getContext();
        if (context != null && NetworkUtils.isNetworkAvailable(context)) {
            Places.initialize(context, "AIzaSyBYd9sEWv1sNFl7S8pwKjTmYhEGOTgtZVc");
        }


        Log.d("AddMoodEvent", "onCreate called");

        // Initialize Firestore and Firebase Storage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("mood_images");

        // Set up location client using fused provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Configure camera result handler
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                File imageFile = new File(imageUri.getPath());

                if (imageFile.length() > 65536) {
                    Snackbar.make(getView(), "Image too large. Please capture a smaller image.", Snackbar.LENGTH_SHORT).show();
                    imageUri = null;
                    return;
                }
                image.setImageURI(imageUri);
            }
        });

        // Configure gallery result handler
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                try (InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri)) {
                    int fileSize = inputStream.available();
                    if (fileSize > 65536) {
                        Snackbar.make(getView(), "Image too large. Please select a smaller image.", Snackbar.LENGTH_SHORT).show();
                        imageUri = null;
                        return;
                    }
                } catch (IOException e) {
                    Log.e("GalleryError", "Error checking image size", e);
                }
                image.setImageURI(imageUri);
            }
        });

        // Configure location autocomplete handler
        placeAutocompleteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {

                Place place = Autocomplete.getPlaceFromIntent(result.getData());

                if (place.getLatLng() != null) {
                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;
                    selectedGeoPoint = new GeoPoint(latitude, longitude);
                    location.setText(place.getName());
                    Log.d("Location", "Name: " + place.getName() + ", Lat: " + latitude + ", Lng: " + longitude);
                }
            } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(result.getData());
                Log.e("Autocomplete", "Error: " + status.getStatusMessage());
            }
        });
    }

    /**
     * Creates temporary image file for camera captures with timestamped filename
     *
     * @return Newly created image file in app-specific storage
     * @throws IOException If file creation fails due to storage issues
     */
    private File createImageFile() throws IOException {
        // Generate unique filename using timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Store in app-specific pictures directory
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Generate content URI for FileProvider
        Log.d("ImageFile", "Image file created: " + image.getAbsolutePath());
        imageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", image);
        Log.d("ImageUri", "Image URI: " + imageUri.toString());
        return image;
    }


    /**
     * Creates the fragment's view hierarchy and initializes UI components.
     * Sets up click listeners and input validation for all form fields.
     *
     * @param inflater           LayoutInflater to inflate views
     * @param container          Parent view group for attachment
     * @param savedInstanceState Previous saved state
     * @return Inflated view hierarchy
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
        View view = inflater.inflate(R.layout.add_mood, container, false);
        View fragmentRoot = view.findViewById(R.id.fragment_root);
        EditText description = view.findViewById(R.id.desc);

        Bundle args = getArguments();
        if (args != null) {
            String selectedMood = args.getString("SELECTED_MOOD");
            if (selectedMood != null) {
                int buttonId = getButtonIdForMood(selectedMood);
                if (buttonId != -1) {
                    ImageButton emojiButton = view.findViewById(buttonId);
                    if (emojiButton != null) {
                        highlightSelectedEmoji(emojiButton);
                    }
                }
            }
        }

        EditText addTrigger = view.findViewById(R.id.reasoning);
        Button submit = view.findViewById(R.id.button);

        SwitchMaterial private_button;
        private_button = view.findViewById(R.id.private_button);

        image = view.findViewById(R.id.imageView);
        ImageButton media_upload = view.findViewById(R.id.upload_button);

        media_upload.setOnClickListener(v -> {
            showSourceDialog();
        });

        location = view.findViewById(R.id.loc);

        Context context = getContext();
        if (context != null && NetworkUtils.isNetworkAvailable(context)) {
            location.setOnClickListener(v -> {
                location.requestFocus();

                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(requireActivity());
                placeAutocompleteLauncher.launch(intent);
            });
        } else{
            location.setOnClickListener(v -> {
                Snackbar.make(getView(), "Can't add location, you are offline.", Snackbar.LENGTH_SHORT).show();
            });
        }


        RadioButton location_button = view.findViewById(R.id.radioButton);

            if (context != null && NetworkUtils.isNetworkAvailable(context)) {
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
        } else{
            location_button.setOnClickListener(v -> {
                Snackbar.make(getView(), "Can't add location, you are offline.", Snackbar.LENGTH_SHORT).show();
            });
        }

        EditText date = view.findViewById(R.id.date);
        RadioButton date_button = view.findViewById(R.id.dateButton);
        ImageButton calendar_button = view.findViewById(R.id.calendarButton);


        final boolean[] isSelected_date = {false};

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = sdf.format(new Date());
        date.setText(currentDate);
        date_button.setChecked(true);

        date_button.setOnClickListener(v -> {
            if (isSelected_date[0]) {
                date.setText("");
                date_button.setChecked(false);
            } else {
                SimpleDateFormat date_formatting = new SimpleDateFormat("dd/MM/yyyy");
                String current_Date = date_formatting.format(new Date());
                date.setText(current_Date);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && date_button.isChecked()) {
                    date_button.setChecked(false);
                    isSelected_date[0] = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        EditText time = view.findViewById(R.id.Time);
        RadioButton time_button = view.findViewById(R.id.timeButton);
        ImageButton clock_button = view.findViewById(R.id.clockButton);

        final boolean[] isSelected_time = {false};

        SimpleDateFormat time_formatting = new SimpleDateFormat("HH:mm");
        String currentTime = time_formatting.format(new Date());
        time.setText(currentTime);
        time_button.setChecked(true);

        time_button.setOnClickListener(v -> {
            if (isSelected_time[0]) {
                time.setText("");
                time_button.setChecked(false);
            } else {

                SimpleDateFormat s_df = new SimpleDateFormat("HH:mm");
                String current_Time = s_df.format(new Date());
                time.setText(current_Time);
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

            if (context != null && !NetworkUtils.isNetworkAvailable(context)) {
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
            }

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
        } catch (Exception e) {
            Log.e("AddMoodEvent", "Error creating view", e);
            return new FrameLayout(getContext()); // Return empty view as fallback
        }
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

    /**
     * Checks if the location permission is granted.
     * If granted, it retrieves and displays the user's current location.
     * If not granted, it requests the necessary location permission.
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            launchLocation();

        } else {

            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
    /**
     * Handles the result of the location permission request.
     * If permission is granted, the location retrieval process is initiated.
     * If permission is denied, a Snack bar message is displayed to inform the user.
     */
    private final ActivityResultLauncher<String> requestLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchLocation();
                } else {
                    Snackbar.make(requireView(), "Location permission denied", Snackbar.LENGTH_SHORT).show();
                }
            });

    /**
     * Retrieves the user's current location and displays it.
     * Uses FusedLocationProviderClient to request high-accuracy location updates.
     * The retrieved latitude and longitude are converted into a human-readable address using Geocoder.
     * The location update request is removed after obtaining the first result.
     */
    public void launchLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                    .setMinUpdateIntervalMillis(5000)
                    .build();

            locationCallback = new LocationCallback() {

                /**
                 * Called when a new location result is available.
                 * Extracts latitude and longitude, converts them into an address, and updates the UI.
                 * Once the location is retrieved, further location updates are stopped.
                 *
                 * @param locationResult The latest location result.
                 */
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    selectedGeoPoint = new GeoPoint(latitude, longitude);

                    Log.d("POINTS", "onLocationResult: " + latitude + longitude);

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
        }
    }

    /**
     * Uploads image to Firebase Storage and persists mood data to Firestore
     *
     * @param privacy Visibility setting for the mood event
     * @param desc User-provided description text
     * @param trigger Mood trigger information
     * @param date Event date in string format
     * @param locationText Human-readable location string
     * @param imageUri Storage URI for uploaded image
     * @param mood Selected mood type identifier
     * @param social Selected social situation label
     * @param time Event time in string format
     */
    private void uploadImageAndSaveMood(Boolean privacy, String desc, String trigger,
                                        String date, String locationText, Uri imageUri,
                                        String mood, String social, String time) {
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
     * Persists mood event data to Firestore with optional image reference
     *
     * @param privacy Event visibility flag
     * @param desc Textual description of mood
     * @param trigger Trigger context for mood
     * @param date Formatted date string
     * @param locationText Location description
     * @param imageUrl Firebase Storage URL (nullable)
     * @param mood Mood type identifier
     * @param social Social context label
     * @param time Formatted time string
     */
    private void saveMoodToFirestore(Boolean privacy, String desc, String trigger,
                                     String date, String locationText, String imageUrl,
                                     String mood, String social, String time) {
        if (!isAdded() || getActivity() == null) {
            Log.w("AddMoodEvent", "Fragment not attached during save");
            return;
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String loggedInUsername = sharedPreferences.getString("username", null);

        // Generate a unique moodEventId
        String moodEventId = UUID.randomUUID().toString();
        DocumentReference moodEventRef = db.collection("MoodEvents").document(moodEventId);

        MoodEvent moodEvent = new MoodEvent(selectedGeoPoint, loggedInUsername,privacy, moodEventId, mood, date, time, locationText, social, trigger, desc, imageUrl);

        // We'll wait for two tasks: saving the mood event and updating the user doc.
        final int totalTasks = 2;
        final int[] completedTasks = {0};

        moodEventRef.set(moodEvent)
                .addOnSuccessListener(aVoid -> {

                    Log.d(TAG, "Mood event saved!");
                    if (isAdded() && getView() != null) {
                        Snackbar.make(getView(), "Mood Event Saved!", Snackbar.LENGTH_LONG).show();
                    }
//                    Snackbar.make(requireView(), "Mood Event Saved!", Snackbar.LENGTH_LONG).show();
                    incrementAndCheck(completedTasks, totalTasks);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving mood event", e);
                    showError("Error saving data!");
                });

        if (isAdded() && getActivity() != null) {

            if (loggedInUsername != null) {
                DocumentReference userDocRef = db.collection("User").document(loggedInUsername);
                userDocRef.update( "moods", FieldValue.increment(1));
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
     * Tracks completion of async operations and triggers fragment closure
     *
     * @param completedTasks Counter array for completed operations
     * @param totalTasks Required number of successful operations
     */
    private void incrementAndCheck(int[] completedTasks, int totalTasks) {
        completedTasks[0]++;
        if (completedTasks[0] == totalTasks) {
            // All tasks complete – now run the exit animation and pop the fragment.
            popFragment();
        }
    }

    /**
     * Executes fragment exit animation and removes from back stack
     */
    private void popFragment() {
        if (isAdded() && getActivity() != null) {
            requireActivity().runOnUiThread(() -> {
                // Add view existence check
                if (fragmentRoot != null && fragmentRoot.getParent() != null) {
                    Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_bottom);
                    fragmentRoot.startAnimation(slideOut);
                    slideOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isAdded()) {
                                requireActivity().getSupportFragmentManager().popBackStack();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                } else {
                    if (isAdded()) {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
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
    public MoodType getMoodForButtonId(int buttonId) {

        for (int i = 0; i < emojiButtonIds.length; i++) {
            if (emojiButtonIds[i] == buttonId) {
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
    public SocialSituation getSocialSituationForButtonId(int buttonId) {
        for (int i = 0; i < socialButtonIds.length; i++) {
            if (socialButtonIds[i] == buttonId) {
                return SocialSituation.values()[i];
            }
        }
        return null;
    }

    /**
     * Maps mood names to corresponding button resource IDs
     *
     * @param moodName Lowercase mood type identifier
     * @return Corresponding button resource ID or -1 if invalid
     */
    public int getButtonIdForMood(String moodName) {
        switch (moodName.toLowerCase()) {
            case "happy": return R.id.emoji_happy;
            case "sad": return R.id.emoji_sad;
            case "fear": return R.id.emoji_fear;
            case "angry": return R.id.emoji_angry;
            case "confused": return R.id.emoji_confused;
            case "disgusted": return R.id.emoji_disgusted;
            case "shameful": return R.id.emoji_shameful;
            case "surprised": return R.id.emoji_surprised;
            case "shy": return R.id.emoji_shy;
            case "tired": return R.id.emoji_tired;
            default: return -1; // Handle unknown moods
        }
    }

}