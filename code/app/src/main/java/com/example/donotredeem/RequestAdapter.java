package com.example.donotredeem;

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

import com.example.donotredeem.Classes.UserProfileManager;

import java.util.List;

public class RequestAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> requests;
    private final UserProfileManager userProfileManager = new UserProfileManager();

    public RequestAdapter(Context context, List<String> requests) {
        super(context, R.layout.item_request, requests);
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        }

        String requesterUsername = requests.get(position);
        TextView username = convertView.findViewById(R.id.username);
        Button acceptButton = convertView.findViewById(R.id.acceptButton);
        Button declineButton = convertView.findViewById(R.id.declineButton);

        username.setText(requesterUsername);

        acceptButton.setOnClickListener(v -> handleAccept(requesterUsername, position));
        declineButton.setOnClickListener(v -> handleDecline(requesterUsername, position));

        return convertView;
    }

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
}
