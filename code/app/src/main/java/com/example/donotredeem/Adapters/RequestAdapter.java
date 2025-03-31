package com.example.donotredeem.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for displaying and managing follow requests in a list view.
 * Supports accepting and declining requests with Firebase integration.
 */
public class RequestAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> requests;
    private final UserProfileManager userProfileManager = new UserProfileManager();
    private FirebaseFirestore db;

    /**
     * Constructor for initializing the adapter.
     *
     * @param context  The context of the parent activity.
     * @param requests List of usernames sending follow requests.
     */
    public RequestAdapter(Context context, List<String> requests) {
        super(context, R.layout.item_request, requests);
        this.context = context;
        this.requests = requests;
    }

    /**
     * Provides the view for each request item in the ListView.
     *
     * @param position    The position of the item in the list.
     * @param convertView The recycled view to populate.
     * @param parent      The parent view group.
     * @return The populated item view.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        }
        db = FirebaseFirestore.getInstance();

        String requesterUsername = requests.get(position);
        TextView username = convertView.findViewById(R.id.username);
        Button acceptButton = convertView.findViewById(R.id.acceptButton);
        Button declineButton = convertView.findViewById(R.id.declineButton);

        CircleImageView profilePic = convertView.findViewById(R.id.pfp);


        username.setText(requesterUsername);

        loadProfilePicture(requesterUsername, profilePic);

        acceptButton.setOnClickListener(v -> handleAccept(requesterUsername, position));
        declineButton.setOnClickListener(v -> handleDecline(requesterUsername, position));

        return convertView;
    }

    /**
     * Handles accepting a follow request.
     *
     * @param requesterUsername The username of the requester.
     * @param position          The position of the request in the list.
     */
    private void handleAccept(String requesterUsername, int position) {
        // Get current user
        SharedPreferences prefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String currentUser = prefs.getString("username", "");

        // Update both users' relationships
        userProfileManager.acceptFollowRequest(currentUser, requesterUsername, new UserProfileManager.OnUpdateListener() {
            @Override
            public void onSuccess() {
                requests.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Accept failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles declining a follow request.
     *
     * @param requesterUsername The username of the requester.
     * @param position          The position of the request in the list.
     */
    private void handleDecline(String requesterUsername, int position) {
        SharedPreferences prefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String currentUser = prefs.getString("username", "");

        userProfileManager.declineFollowRequest(currentUser, requesterUsername, new UserProfileManager.OnUpdateListener() {
            @Override
            public void onSuccess() {
                requests.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Decline failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Loads the profile picture from Firebase Firestore into the ImageView.
     *
     * @param username  The username of the requester.
     * @param imageView The ImageView to display the profile picture.
     */
    private void loadProfilePicture(String username, CircleImageView imageView) {
        db.collection("User")
                .document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String pfpUrl = documentSnapshot.getString("pfp");
                        if (pfpUrl != null && !pfpUrl.isEmpty()) {
                            Glide.with(context)
                                    .load(pfpUrl)
                                    .placeholder(R.drawable.ic_account_circle)  // Fallback image while loading
                                    .error(R.drawable.ic_account_circle)        // Fallback if loading fails
                                    .into(imageView);
                        } else {
                            imageView.setImageResource(R.drawable.ic_account_circle);  // Fallback if no image
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_account_circle);
                    }
                })
                .addOnFailureListener(e -> imageView.setImageResource(R.drawable.ic_account_circle));
    }
}
