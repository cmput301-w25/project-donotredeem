//package com.example.donotredeem;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FieldValue;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.journeyapps.barcodescanner.ScanContract;
//import com.journeyapps.barcodescanner.ScanOptions;
//import com.google.zxing.BarcodeFormat;
//import com.journeyapps.barcodescanner.BarcodeEncoder;
//
//public class QRCodeFragment extends Fragment {
//    private FirebaseFirestore firestore;
//    private ActivityResultLauncher<ScanOptions> scannerLauncher;
//    private ActivityResultLauncher<String> requestPermissionLauncher;
//    private SharedPreferences sharedPreferences;
//    private String currentUsername;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_qr_code, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        firestore = FirebaseFirestore.getInstance();
//        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
//        currentUsername = sharedPreferences.getString("username", null);
//
//        // Initialize scanner launcher
//        scannerLauncher = registerForActivityResult(new ScanContract(), result -> {
//            if (result.getContents() != null) {
//                handleScannedQR(result.getContents());
//            }
//        });
//
//        // Initialize permission launcher
//        requestPermissionLauncher = registerForActivityResult(
//                new ActivityResultContracts.RequestPermission(),
//                isGranted -> {
//                    if (isGranted) {
//                        startQRScan();
//                    } else {
//                        Toast.makeText(requireContext(),
//                                "Camera permission required", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//        generateOwnQRCode(view);
//        setupScanButton(view);
//    }
//
//    private void generateOwnQRCode(View view) {
//        try {
//            // Get username directly from SharedPreferences
//            if (currentUsername == null || currentUsername.isEmpty()) {
//                Toast.makeText(requireContext(), "Username not found", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//            Bitmap bitmap = barcodeEncoder.encodeBitmap(
//                    currentUsername,  // Use the username from SharedPreferences
//                    BarcodeFormat.QR_CODE,
//                    400,
//                    400
//            );
//
//            ImageView qrImage = view.findViewById(R.id.qrCodeImage);
//            qrImage.setImageBitmap(bitmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(requireContext(),
//                    "Error generating QR code", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void setupScanButton(View view) {
//        Button scanButton = view.findViewById(R.id.scanButton);
//        scanButton.setOnClickListener(v -> checkCameraPermission());
//    }
//
//    private void checkCameraPermission() {
//        if (ContextCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            startQRScan();
//        } else {
//            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
//        }
//    }
//
//    private void startQRScan() {
//        ScanOptions options = new ScanOptions();
//        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
//        options.setPrompt("Scan another user's QR code");
//        options.setCameraId(0);
//        options.setBeepEnabled(false);
//        options.setOrientationLocked(true);
//        scannerLauncher.launch(options);
//    }
//
//
//    private void handleScannedQR(String scannedUsername) {
//        // Use the already retrieved username
//        if (currentUsername == null || currentUsername.isEmpty()) {
//            Toast.makeText(requireContext(),
//                    "You need to be logged in to send follow requests", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        firestore.collection("User")
//                .document(scannedUsername)
//                .update("requests", FieldValue.arrayUnion(currentUsername))
//                .addOnSuccessListener(aVoid ->
//                        Toast.makeText(requireContext(),
//                                "Follow request sent!", Toast.LENGTH_SHORT).show()
//                )
//                .addOnFailureListener(e ->
//                        Toast.makeText(requireContext(),
//                                "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
//                );
//    }
//}

package com.example.donotredeem;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.donotredeem.Fragments.SearchedUser;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeFragment extends Fragment {
    private ActivityResultLauncher<ScanOptions> scannerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private SharedPreferences sharedPreferences;
    private String currentUsername;
    ImageView back_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_qr_code, container, false);
        back_button = view.findViewById(R.id.qr_back);

        back_button.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);

        // Initialize scanner launcher
        scannerLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                handleScannedQR(result.getContents());
            }
        });

        // Initialize permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startQRScan();
                    } else {
                        Toast.makeText(requireContext(),
                                "Camera permission required", Toast.LENGTH_SHORT).show();
                    }
                });

        generateOwnQRCode(view);
        setupScanButton(view);
    }

    private void generateOwnQRCode(View view) {
        try {
            if (currentUsername == null || currentUsername.isEmpty()) {
                Toast.makeText(requireContext(), "Username not found", Toast.LENGTH_SHORT).show();
                return;
            }

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(
                    currentUsername,
                    BarcodeFormat.QR_CODE,
                    400,
                    400
            );

            ImageView qrImage = view.findViewById(R.id.qrCodeImage);
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(),
                    "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupScanButton(View view) {
        Button scanButton = view.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v -> checkCameraPermission());
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startQRScan();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startQRScan() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan another user's QR code");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        scannerLauncher.launch(options);
    }

    private void handleScannedQR(String scannedUsername) {
        if (scannedUsername == null || scannedUsername.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Invalid QR code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to the scanned user's profile using your existing newInstance method
        FragmentTransaction transaction = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();

        SearchedUser searchedUserFragment = SearchedUser.newInstance(scannedUsername);
        transaction.replace(R.id.fragment_container, searchedUserFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}