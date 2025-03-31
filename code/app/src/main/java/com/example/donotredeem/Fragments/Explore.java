package com.example.donotredeem.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donotredeem.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment providing user discovery functionality with real-time search capabilities.
 * Features include:
 * <ul>
 * <li>Full-text search across all registered users</li>
 * <li>Case-insensitive username filtering</li>
 * <li>Dynamic result updating during typing</li>
 * <li>Navigation to user profile views</li>
 * </ul>
 */
public class Explore extends Fragment {

    private List<String> displaylist = new ArrayList<>();
    private List<String> filteredList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText searchBar;
    private UserAdapter adapter;
    ImageView back_button;
    private String username;


    /**
     * Initializes exploration interface and data loading:
     * - Configures RecyclerView with empty adapter
     * - Sets up Firestore data connection
     * - Implements search bar text monitoring
     * - Handles back navigation
     *
     * @return Configured exploration view hierarchy
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.explore, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.list_view);
        searchBar = view.findViewById(R.id.search_bar);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(new ArrayList<>(), this::onItemClicked);
        recyclerView.setAdapter(adapter);
        back_button = view.findViewById(R.id.search_back);

        back_button.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        fetchUsernamesFromFirestore();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    /**
     * Handles user profile selection by launching detail view
     * @param username Selected user's identifier
     */
    private void onItemClicked(String username) {
        SearchedUser searchedUserFragment = SearchedUser.newInstance(username);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, searchedUserFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Loads all registered usernames from Firestore:
     * - Excludes current user from results
     * - Maintains separate source/filtered lists
     * - Automatically updates UI on data load
     */
    private void fetchUsernamesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                displaylist.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (!document.getId().equals(username)) {
                        displaylist.add(document.getId());
                    }
                }
                // Update both lists
                filteredList.clear();
                filteredList.addAll(displaylist);
            }
            else {
                Log.e("Explore", "Error fetching usernames", task.getException());
            }
        });
    }

    /**
     * Filters user list based on search query:
     * - Uses contains() match for partial matching
     * - Maintains case insensitivity
     * - Clears previous filters on empty query
     * - Updates adapter with safe list copy
     *
     * @param query Search term entered by user
     */
    public void filterList(String query) {
        List<String> filtered = new ArrayList<>();
        if (query.isEmpty()) {
        } else {
            for (String username : displaylist) {
                if (username.toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(username);
                }
            }
        }
        filteredList.clear();
        filteredList.addAll(filtered);
        adapter.updateList(new ArrayList<>(filtered)); // Pass copy
    }
}