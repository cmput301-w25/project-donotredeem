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
import android.location.Geocoder;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.Glide;
import com.example.donotredeem.Classes.NetworkUtils;
import com.example.donotredeem.Classes.MoodEvent;
import com.example.donotredeem.Enumertions.MoodTypeEnum;
import com.example.donotredeem.R;
import com.example.donotredeem.Enumertions.SocialSituationEnum;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A Fragment that allows users to edit an existing mood event. This fragment provides functionality to modify mood details, including description, location, date, time, and trigger.
 * It also allows users to upload a new image, select a mood emoji, and specify the social context of the event.
 * This fragment interacts with Firebase Firestore to update the mood event in the database and Firebase Storage to upload images.
 */
public class EditMoodEventFragment extends Fragment {
    public int FromWhere;
    private ImageView image;
    private EditText description, locationEdit, dateEdit, timeEdit, triggerEdit;
    private Button submit;
    private ImageButton mediaUpload, calendarButton, clockButton, closeButton;
    private RadioButton locationButton, dateButton, timeButton;

    // Variables to store selected values
    private String selectedMoodName = null;

    private GeoPoint selectedGeoPoint;
    private String selectedSocial = null;

    // Request codes
    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;
    private static final int LOCATION_REQUEST = 300;

    // ActivityResultLaunchers for camera and gallery
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> placeAutocompleteLauncher;

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
    int[] socialButtonIds = {R.id.alone_social, R.id.pair_social, R.id.few_social, R.id.crowd_social};
    private ImageButton selectedEmoji = null;
    private ImageButton selectedSocialButton = null;

    // To hold the original moodEventId for update
    private String moodEventId;

    private String firebaseImageUrl = null;

    /**
     * Default constructor for the EditMoodEvent fragment.
     */
    public EditMoodEventFragment() {
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
        Context context = getContext();
        if (context != null && NetworkUtils.isNetworkAvailable(context)) {
            Places.initialize(context, "AIzaSyBYd9sEWv1sNFl7S8pwKjTmYhEGOTgtZVc");
        }

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("mood_images");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Setup camera launcher
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

        // Setup gallery launcher
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

        placeAutocompleteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {

                Place place = Autocomplete.getPlaceFromIntent(result.getData());

                if (place.getLatLng() != null) {

                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;

                    selectedGeoPoint = new GeoPoint(latitude, longitude);

                    locationEdit.setText(place.getName());

                    Log.d("Location", "Name: " + place.getName() + ", Lat: " + latitude + ", Lng: " + longitude);
                }

            } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(result.getData());
                Log.e("Autocomplete", "Error: " + status.getStatusMessage());

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
        try{
        // Inflate the layout (assumed to be similar to add_mood.xml)
        View view = inflater.inflate(R.layout.edit_mood, container, false);
        View fragmentRoot = view.findViewById(R.id.fragment_root_edit);

        // Bind UI elements
        description = view.findViewById(R.id.desc);
        locationEdit = view.findViewById(R.id.loc);
        dateEdit = view.findViewById(R.id.date);
        timeEdit = view.findViewById(R.id.Time);
        triggerEdit = view.findViewById(R.id.reasoning);
        image = view.findViewById(R.id.imageView);
        submit = view.findViewById(R.id.button);
        mediaUpload = view.findViewById(R.id.upload_button);
        calendarButton = view.findViewById(R.id.calendarButton);
        clockButton = view.findViewById(R.id.clockButton);
        locationButton = view.findViewById(R.id.radioButton);
        dateButton = view.findViewById(R.id.dateButton);
        timeButton = view.findViewById(R.id.timeButton);
        closeButton = view.findViewById(R.id.closeButton);
        SwitchMaterial private_button;
        private_button = view.findViewById(R.id.edit_private_button);

        // this is not pre populating with prev statae idk how to do that someome help - heer

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
            Boolean privacy = args.getBoolean("privacy");
            double[] location = args.getDoubleArray("locationpts");
            if (location != null && location.length == 2) {
                selectedGeoPoint = new GeoPoint(location[0], location[1]);
            }

            // For this example, assume selectedMoodName comes from emotionalState
            selectedMoodName = emotionalState;

            private_button.setChecked(privacy);

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
                    MoodTypeEnum moodTypeEnum = getMoodForButtonId(id);
                    if (moodTypeEnum != null && moodTypeEnum.getMood().equalsIgnoreCase(emotionalState)) {
                        highlightSelectedEmoji(btn);
                        break;
                    }
                }
            }


            if (!TextUtils.isEmpty(situation)) {
                for (int id : socialButtonIds) {
                    ImageButton btn = view.findViewById(id);
                    SocialSituationEnum socialSituationEnum = getSocialSituationForButtonId(id);
                    if (socialSituationEnum != null && socialSituationEnum.getLabel().equalsIgnoreCase(situation)) {
                        highlightSelectedSocial(btn);
                        break;
                    }
                }
            }

        }

        mediaUpload.setOnClickListener(v -> showSourceDialog());

        Context context = getContext();
         if (context != null && NetworkUtils.isNetworkAvailable(context)) {
            locationEdit.setOnClickListener(v -> {
                locationEdit.requestFocus();

                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(requireActivity());
                placeAutocompleteLauncher.launch(intent);
            });
        } else{
            locationEdit.setOnClickListener(v -> {
                Snackbar.make(getView(), "Can't add location, you are offline.", Snackbar.LENGTH_SHORT).show();
            });
        }
        RadioButton location_button = view.findViewById(R.id.radioButton);
        final boolean[] isSelected_loc = {false};
            if (context != null && NetworkUtils.isNetworkAvailable(context)) {
                location_button.setOnClickListener(v -> {
                if (isSelected_loc[0]) {
                    locationEdit.setText("");
                    selectedGeoPoint = null;
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
                uploadImageAndUpdateMood(privacy);
            } else {
                updateMoodEventInFirestore(privacy, descText, triggerText, dateText, locationText, firebaseImageUrl,
                        selectedMoodName, selectedSocial, timeText);
            }

            if (!NetworkUtils.isNetworkAvailable(requireContext())) {
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


        closeButton.setOnClickListener(v -> {

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
            Log.e("EditMoodEvent", "Error creating view", e);
            return new FrameLayout(getContext()); // Return empty view as fallback
        }
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
                    locationEdit.setText(Address);

                    fusedLocationClient.removeLocationUpdates(locationCallback); //stop location updates
                }

            };

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
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
    private void uploadImageAndUpdateMood(Boolean privacy) {
        // If imageUri is null, use the existing firebaseImageUrl
        if (imageUri == null) {
            Log.d("Firebase", "No new image to upload, updating event with existing image.");
            updateMoodEventInFirestore(
                    privacy,
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
                    privacy,
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
                            privacy,
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
    private void updateMoodEventInFirestore(Boolean privacy, String desc, String trigger,
                                            String date, String locationText, String imageUrl,
                                            String mood, String social, String time) {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String loggedInUsername = sharedPreferences.getString("username", null);

        if (moodEventId == null) {
            Log.e(TAG, "MoodEventId is null, cannot update");
            showError("Mood Event ID is missing!");
            return;
        }
        DocumentReference moodEventRef = db.collection("MoodEvents").document(moodEventId);
        MoodEvent updatedMoodEvent = new MoodEvent(selectedGeoPoint, loggedInUsername,privacy,moodEventId, mood, date, time, locationText, social, trigger, desc, imageUrl);

        final int totalTasks = 1;
        final int[] completedTasks = {0};

        moodEventRef.set(updatedMoodEvent)
                .addOnSuccessListener(aVoid -> {
                    if (isAdded() && getActivity() != null) {
                        // Use activity's root view
                        View rootView = getActivity().findViewById(android.R.id.content);
                        Snackbar.make(rootView, "Mood event updated!", Snackbar.LENGTH_SHORT).show();
                        incrementAndCheck(completedTasks, totalTasks);
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded() && getActivity() != null) {
                        View rootView = getActivity().findViewById(android.R.id.content);
                        Snackbar.make(rootView, "Error updating data!", Snackbar.LENGTH_SHORT).show();
                    }
                });

    }
    /**
     * Tracks completion of async operations and triggers UI cleanup
     */
    private void incrementAndCheck(int[] completedTasks, int totalTasks) {
        completedTasks[0]++;
        if (completedTasks[0] == totalTasks) {
            popFragment();
        }
    }
    /**
     * Executes exit animation and fragment removal
     */
    private void popFragment() {
        if (isAdded() && getActivity() != null) {
            requireActivity().runOnUiThread(() -> {
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
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    /**
     * Provides visual feedback for operation errors
     */
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
            MoodTypeEnum selectedMood = getMoodForButtonId(buttonId);
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
    public MoodTypeEnum getMoodForButtonId(int buttonId) {
        for (int i = 0; i < emojiButtonIds.length; i++) {
            if (emojiButtonIds[i] == buttonId) {
                return MoodTypeEnum.values()[i];
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
            SocialSituationEnum selectedSituation = getSocialSituationForButtonId(buttonId);
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
    public SocialSituationEnum getSocialSituationForButtonId(int buttonId) {
        for (int i = 0; i < socialButtonIds.length; i++) {
            if (socialButtonIds[i] == buttonId) {
                return SocialSituationEnum.values()[i];
            }
        }
        return null;
    }

}