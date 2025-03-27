package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.donotredeem.Classes.UserProfileManager;
import com.example.donotredeem.Classes.Users;
import com.example.donotredeem.R;
import com.example.donotredeem.RequestAdapter;

import java.util.List;

public class RequestsFragment extends Fragment {
    private ListView requestsListView;
    private List<String> requestsList;
    private RequestAdapter adapter;
    private UserProfileManager userProfileManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.requests, container, false);
        requestsListView = view.findViewById(R.id.requestsListView);
        userProfileManager = new UserProfileManager();

        // Get current logged-in user
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("username", "");

        loadRequests(currentUser);
        return view;
    }

    private void loadRequests(String username) {
        userProfileManager.getUserProfileWithFollowers(username, new UserProfileManager.OnUserProfileFetchListener() {
            @Override
            public boolean onUserProfileFetched(Users user) {
                requestsList = user.getRequests();
                adapter = new RequestAdapter(requireContext(), requestsList);
                requestsListView.setAdapter(adapter);
                return false;
            }

            @Override
            public void onUserProfileFetchError(Exception e) {
                Toast.makeText(requireContext(), "Error loading requests", Toast.LENGTH_SHORT).show();
            }
        });
    }
}