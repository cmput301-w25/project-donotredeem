package com.example.donotredeem;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MoodJarFragment extends Fragment {
    private ImageView jarImage, back_button;
    private Button addNoteButton, unlockButton;
    private TextView countdownText;
    private LottieAnimationView fireworksAnimation;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private String currentUsername;
    private int noteCount = 0;
//    private long targetDateMillis = new GregorianCalendar(2025, Calendar.MARCH, 27, 9, 58, 30).getTimeInMillis();
    private long targetDateMillis = System.currentTimeMillis() + 6000;

    private boolean celebrationTriggered = false;
    private Handler countdownHandler = new Handler();
    private Runnable countdownRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mood_jar, container, false);

        jarImage = view.findViewById(R.id.jar_image);
        addNoteButton = view.findViewById(R.id.add_note_button);
        countdownText = view.findViewById(R.id.countdown_text);
        unlockButton = view.findViewById(R.id.unlock_button);
        fireworksAnimation = view.findViewById(R.id.fireworks_animation);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);
        setupButton();
        setupCountdown();
        setupUnlockButton();
        loadNotes();

        back_button = view.findViewById(R.id.jar_back);

        back_button.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        return view;
    }

    private void setupButton() {
        addNoteButton.setOnClickListener(v -> showAddNoteDialog());
    }

    private void showAddNoteDialog() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Positive Note");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String note = input.getText().toString().trim();
            if (!note.isEmpty()) {
                saveNoteToFirestore(note);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

//    private void saveNoteToFirestore(String note) {
//        if (currentUsername == null) return;
//
//        Map<String, Object> noteData = new HashMap<>();
//        noteData.put("text", note);
//        noteData.put("timestamp", FieldValue.serverTimestamp());
//
//        db.collection("User")
//                .document(currentUsername)
//                .collection("moodNote")
//                .add(noteData)
//                .addOnSuccessListener(documentReference -> {
//                    noteCount++;
//                    updateJarImage();
//                    showToast("Note added!");
//                })
//                .addOnFailureListener(e -> showToast("Error saving note"));
//    }
    private void saveNoteToFirestore(String note) {
        if (currentUsername == null) return;

        // First create the note in moodNote collection
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("text", note);
        noteData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("User")
                .document(currentUsername)
                .collection("moodNote")
                .add(noteData)
                .addOnSuccessListener(documentReference -> {
                    // After note is created, add its reference to moodJar array
                    addNoteReferenceToMoodJar(documentReference);
                    noteCount++;
                    updateJarImage();
                    showToast("Note added!");
                })
                .addOnFailureListener(e -> showToast("Error saving note"));
    }
    private void addNoteReferenceToMoodJar(DocumentReference noteRef) {
        db.collection("User")
                .document(currentUsername)
                .update("moodJar", FieldValue.arrayUnion(noteRef))
                .addOnSuccessListener(aVoid -> Log.d("MoodJar", "Note reference added to moodJar"))
                .addOnFailureListener(e -> Log.e("MoodJar", "Error adding note reference to moodJar", e));
    }
//    private void addNoteReferenceToMoodJar(DocumentReference noteRef) {
//        db.collection("User")
//                .document(currentUsername)
//                .update("moodJar", FieldValue.arrayUnion(noteRef))
//                .addOnSuccessListener(aVoid -> {
//                    noteCount++;
//                    updateJarImage();
//                    Log.d("MoodJar", "Note reference added to moodJar");
//                })
//                .addOnFailureListener(e -> Log.e("MoodJar", "Error adding note reference", e));
//    }


//    private void loadNotes() {
//        if (currentUsername == null) return;
//
//        db.collection("User")
//                .document(currentUsername)
//                .collection("moodNote")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    noteCount = queryDocumentSnapshots.size();
//                    updateJarImage();
//                });
//    }
    private void loadNotes() {
        if (currentUsername == null) return;

        db.collection("User")
                .document(currentUsername)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<DocumentReference> itemRefs = (List<DocumentReference>) documentSnapshot.get("moodJar");
                    noteCount = (itemRefs != null) ? itemRefs.size() : 0;
                    updateJarImage();
                })
                .addOnFailureListener(e -> Log.e("MoodJar", "Error loading moodJar items", e));
    }

    private void updateJarImage() {
        if (getContext() == null) return;

        int drawableId = R.drawable.jar_empty;
        if (noteCount >= 9) drawableId = R.drawable.jar_full;
        else if (noteCount >= 6) drawableId = R.drawable.jar_half;
        else if (noteCount >= 3) drawableId = R.drawable.jar_quarter;

        jarImage.setImageResource(drawableId);
    }

    private void setupCountdown() {
        countdownHandler.removeCallbacks(countdownRunnable);
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) return;

                long currentTime = System.currentTimeMillis();
                long diff = targetDateMillis - currentTime;

                if (diff > 0) {
                    updateCountdownText(diff);
                    countdownHandler.postDelayed(this, 1000);
                } else {
                    showCelebration();
                    countdownHandler.removeCallbacks(this);
                }
            }
        };
        countdownHandler.post(countdownRunnable);

        long currentTime = System.currentTimeMillis();
        if (currentTime >= targetDateMillis) {
            showCelebration();
        }
    }

    private void updateCountdownText(long millisUntilFinished) {
        long seconds = millisUntilFinished / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        String countdown = String.format("%dd %02d:%02d:%02d",
                days,
                hours % 24,
                minutes % 60,
                seconds % 60);

        countdownText.setText(countdown);
    }

//    private void showCelebration() {
//        if (!celebrationTriggered && isAdded() && isVisible()) {
//            celebrationTriggered = true;
//            if (fireworksAnimation != null) {
//                fireworksAnimation.setVisibility(View.VISIBLE);
//                fireworksAnimation.playAnimation();
//            }
//            if (unlockButton != null) {
//                unlockButton.setVisibility(View.VISIBLE);
//            }
//            if (countdownText != null) {
//                countdownText.setText("Ready to unlock!");
//            }
//        }
//    }
private void showCelebration() {
    if (!celebrationTriggered && isVisible() && getContext() != null) {
        celebrationTriggered = true;

        // Ensure animation is properly configured
        fireworksAnimation.setAnimation(R.raw.fireworks_anim);
        fireworksAnimation.setRepeatCount(LottieDrawable.INFINITE);
        fireworksAnimation.setSpeed(1f);

        // Bring animation to front
        fireworksAnimation.bringToFront();

        fireworksAnimation.setVisibility(View.VISIBLE);
        fireworksAnimation.playAnimation();

        // Add animation listener to detect errors
        fireworksAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("Animation", "Fireworks started");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("Animation", "Fireworks ended");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("Animation", "Fireworks cancelled");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d("Animation", "Fireworks repeat");
            }
        });

        unlockButton.setVisibility(View.VISIBLE);
        countdownText.setText("Ready to unlock!");
    }
}

    private void setupUnlockButton() {
        unlockButton.setOnClickListener(v -> {
            unlockButton.setEnabled(false);
            unlockJarContents();
        });
    }

//    private void unlockJarContents() {
//
//        showToast("Jar unlocked!");
//
//    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        countdownHandler.removeCallbacks(countdownRunnable);
        if (fireworksAnimation != null) {
            fireworksAnimation.cancelAnimation();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (countdownText != null) {
            countdownText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Restart countdown when fragment becomes visible
        setupCountdown();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Clean up handlers when fragment is not visible
        countdownHandler.removeCallbacks(countdownRunnable);
    }

//    private void unlockJarContents() {
//        if (currentUsername == null) return;
//
//        db.collection("User")
//                .document(currentUsername)
//                .collection("moodJar")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<DocumentSnapshot> items = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        items.add(document);
//                    }
//                    if (!items.isEmpty()) {
//                        showMoodJarItemsDialog(items);
//                    } else {
//                        showToast("Mood Jar is empty!");
//                    }
//                })
//                .addOnFailureListener(e -> showToast("Error loading items"));
//    }
    private void unlockJarContents() {
        if (currentUsername == null) return;

        // Get the moodJar array which contains references to both mood events and notes
        db.collection("User")
                .document(currentUsername)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<DocumentReference> itemRefs = (List<DocumentReference>) documentSnapshot.get("moodJar");
                    if (itemRefs != null && !itemRefs.isEmpty()) {
                        noteCount = itemRefs.size();
                        updateJarImage();
                        fetchMoodJarItems(itemRefs);
                    } else {
                        noteCount = 0; // Reset count if empty
                        updateJarImage();
                        showToast("Mood Jar is empty!");
                        unlockButton.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    showToast("Error loading items");
                    unlockButton.setEnabled(true);
                });
    }
    private void fetchMoodJarItems(List<DocumentReference> itemRefs) {
        List<DocumentSnapshot> items = new ArrayList<>();

        // We'll use a counter to track how many items we've loaded
        final int[] loadedCount = {0};
        final int totalCount = itemRefs.size();

        for (DocumentReference ref : itemRefs) {
            ref.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    items.add(documentSnapshot);
                }

                loadedCount[0]++;
                if (loadedCount[0] == totalCount) {
                    // All items loaded, show the dialog
                    if (!items.isEmpty()) {
                        showMoodJarItemsDialog(items);
                    } else {
                        showToast("No valid items found in Mood Jar!");
                    }
                    unlockButton.setEnabled(true);
                }
            }).addOnFailureListener(e -> {
                loadedCount[0]++;
                if (loadedCount[0] == totalCount) {
                    if (!items.isEmpty()) {
                        showMoodJarItemsDialog(items);
                    } else {
                        showToast("No valid items found in Mood Jar!");
                    }
                    unlockButton.setEnabled(true);
                }
            });
        }
    }

//    @SuppressLint("RestrictedApi")
//    private void showMoodJarItemsDialog(List<DocumentSnapshot> items) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_layout, null);
////        builder.setView(dialogView,
////                (int) getResources().getDimension(R.dimen.dialog_margin_horizontal),
////                (int) getResources().getDimension(R.dimen.dialog_margin_top),
////                (int) getResources().getDimension(R.dimen.dialog_margin_horizontal),
////                (int) getResources().getDimension(R.dimen.dialog_margin_bottom)
////        );
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int dialogHeight = (int) (displayMetrics.heightPixels * 0.9);
//
//        builder.setView(dialogView);
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        // Set dialog dimensions
//        Window window = dialog.getWindow();
//        if (window != null) {
//            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);
//            window.setGravity(Gravity.CENTER);
//        }
//
//
//        ViewPager2 viewPager = dialogView.findViewById(R.id.view_pager);
//        Button closeButton = dialogView.findViewById(R.id.closeButton);
//
//        MoodJarPagerAdapter adapter = new MoodJarPagerAdapter(items);
//        viewPager.setAdapter(adapter);
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        closeButton.setOnClickListener(v -> dialog.dismiss());
//    }
private void showMoodJarItemsDialog(List<DocumentSnapshot> items) {
    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
    View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_layout, null);

    // Set the view first before creating dialog
    builder.setView(dialogView);

    // Create dialog before accessing its window
    AlertDialog dialog = builder.create();

    // Configure window after creation but before show()
    Window window = dialog.getWindow();
    if (window != null) {
        // Set dialog dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (displayMetrics.heightPixels * 0.9));
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    // Initialize views
    ViewPager2 viewPager = dialogView.findViewById(R.id.view_pager);
    Button closeButton = dialogView.findViewById(R.id.closeButton);

    // Configure ViewPager2
    MoodJarPagerAdapter adapter = new MoodJarPagerAdapter(items);
    viewPager.setAdapter(adapter);

    // Set click listener
    closeButton.setOnClickListener(v -> dialog.dismiss());

    // Show dialog last
    dialog.show();
}

    // Create MoodJarPagerAdapter class
    private class MoodJarPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_MOOD = 0;
        private static final int TYPE_NOTE = 1;
        private final List<DocumentSnapshot> items;

        public MoodJarPagerAdapter(List<DocumentSnapshot> items) {
            this.items = items;
        }

        @Override
        public int getItemViewType(int position) {
            DocumentSnapshot item = items.get(position);
            String path = item.getReference().getPath();
            return path.contains("MoodEvents") ? TYPE_MOOD : TYPE_NOTE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == TYPE_MOOD) {
                View view = inflater.inflate(R.layout.moodref_layout, parent, false);
                return new MoodViewHolder(view);
            } else {
                View view = inflater.inflate(R.layout.note_layout, parent, false);
                return new NoteViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            DocumentSnapshot item = items.get(position);
            if (holder.getItemViewType() == TYPE_MOOD) {
                bindMoodViewHolder((MoodViewHolder) holder, item);
            } else {
                bindNoteViewHolder((NoteViewHolder) holder, item);
            }
            View itemView = holder.itemView;
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            if (params != null) {
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            itemView.setLayoutParams(params);
        }

        private void bindMoodViewHolder(MoodViewHolder holder, DocumentSnapshot document) {

            MoodEvent moodEvent = document.toObject(MoodEvent.class);
            if (moodEvent != null) {
                // Set core fields
                holder.emotionalState.setText(moodEvent.getEmotionalState());
                holder.date.setText(moodEvent.getDate());
                holder.time.setText(moodEvent.getTime());

                // Set privacy status
                if (moodEvent.getPrivacy()) {
                    holder.privacyText.setText("Private");
                    holder.privacyImage.setImageResource(R.drawable.locked);
                } else {
                    holder.privacyText.setText("Public");
                    holder.privacyImage.setImageResource(R.drawable.unlocked);
                }

                // Set social situation
                int situationId = SocialSituation.getImageIdBySituation(moodEvent.getSituation());
                if (situationId != -1) {
                    holder.situationImage.setImageResource(situationId);
                    holder.situationText.setText(moodEvent.getSituation());
                }

                // Handle optional fields visibility
                setFieldVisibility(holder.placeText, holder.locationIcon, moodEvent.getPlace());
                setFieldVisibility(holder.triggerText, holder.reasonIcon, moodEvent.getTrigger());
                setFieldVisibility(holder.explainText, holder.descIcon, moodEvent.getExplainText());

                // Load main image
                if (!TextUtils.isEmpty(moodEvent.getExplainPicture())) {
                    Glide.with(requireContext())
                            .load(moodEvent.getExplainPicture())
                            .placeholder(R.drawable.rounded_background)
                            .error(R.drawable.rounded_background)
                            .into(holder.timelineImage);
                }

                // Set mood color scheme
                int colorId = getResources().getIdentifier(
                        moodEvent.getEmotionalState(), "color", requireContext().getPackageName());
                if (colorId != 0) {
                    int color = ContextCompat.getColor(requireContext(), colorId);
                    holder.cardView.setCardBackgroundColor(color);
                    holder.emotionalState.setTextColor(color);
                }

                // Set mood emoji
                int emojiId = MoodType.getImageIdByMood(moodEvent.getEmotionalState());
                holder.emojiIcon.setImageResource(emojiId);
            }

        }

        private void setFieldVisibility(TextView textView, ImageView iconView, String content) {
            if (!TextUtils.isEmpty(content)) {
                textView.setText(content);
                textView.setVisibility(View.VISIBLE);
                iconView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
                iconView.setVisibility(View.GONE);
            }
        }

//    private void bindMoodViewHolder(MoodViewHolder holder, DocumentSnapshot document) {
//        MoodEvent moodEvent = document.toObject(MoodEvent.class);
//        if (moodEvent != null) {
//            // Mandatory fields
//            holder.emotionalState.setText(moodEvent.getEmotionalState() != null ?
//                    moodEvent.getEmotionalState() : "No mood");
//
//            // Optional fields with null checks
//            holder.date.setText(moodEvent.getDate() != null ?
//                    moodEvent.getDate() : "No date");
//
//            holder.time.setText(moodEvent.getTime() != null ?
//                    moodEvent.getTime() : "No time");
//
//            // Handle optional description
//            TextView description = holder.itemView.findViewById(R.id.View_Additional_details);
//            description.setText(moodEvent.getExplainText() != null ?
//                    moodEvent.getExplainText() : "No description");
//
//            Glide.with(requireContext())
//                    .load(moodEvent.getExplainPicture())
//                    .placeholder(R.drawable.default_image) // Add fallback
//                    .error(R.drawable.error_image) // Add error state
//                    .into(holder.timelineImage);
//        }
//
//    }


        private void bindNoteViewHolder(NoteViewHolder holder, DocumentSnapshot document) {
            // Add null safety checks
            String noteText = document.getString("text");
            Date timestamp = document.getDate("timestamp");

            holder.noteText.setText(noteText != null ? noteText : "No text");

            if (timestamp != null) {
                // Format timestamp properly
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
                holder.timestamp.setText(sdf.format(timestamp));
            } else {
                holder.timestamp.setText("No timestamp");
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class MoodViewHolder extends RecyclerView.ViewHolder {
//            ImageView timelineImage, emojiIcon;
//            TextView emotionalState, date, time;
            CardView cardView;
            ImageView timelineImage, emojiIcon, situationImage, privacyImage;
            ImageView descIcon, locationIcon, reasonIcon;
            TextView emotionalState, date, time, placeText;
            TextView situationText, triggerText, explainText, privacyText;

            MoodViewHolder(View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.rounded_bg);
                timelineImage = itemView.findViewById(R.id.View_timelineImage);
                emojiIcon = itemView.findViewById(R.id.emojiIcon);
                situationImage = itemView.findViewById(R.id.View_SituationImage);
                privacyImage = itemView.findViewById(R.id.View_Privacy_Image);
                descIcon = itemView.findViewById(R.id.DescIcon);
                locationIcon = itemView.findViewById(R.id.LocationIcon);
                reasonIcon = itemView.findViewById(R.id.ReasonIcon);

                emotionalState = itemView.findViewById(R.id.View_Emotional_State);
                date = itemView.findViewById(R.id.View_Specific_Date);
                time = itemView.findViewById(R.id.View_Specific_Time);
                placeText = itemView.findViewById(R.id.View_Specific_Location);
                situationText = itemView.findViewById(R.id.View_Situation_Text);
                triggerText = itemView.findViewById(R.id.View_Specific_Trigger);
                explainText = itemView.findViewById(R.id.View_Additional_details);
                privacyText = itemView.findViewById(R.id.View_Privacy_Text);
            }
            }
        }

        class NoteViewHolder extends RecyclerView.ViewHolder {
            TextView noteText, timestamp;

            NoteViewHolder(View itemView) {
                super(itemView);
                noteText = itemView.findViewById(R.id.note_text);
                timestamp = itemView.findViewById(R.id.note_timestamp);
            }
        }

}