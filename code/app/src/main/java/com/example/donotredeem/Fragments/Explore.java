package com.example.donotredeem.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.donotredeem.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Explore extends Fragment {

//    private List<String> displaylist = Arrays.asList("AG", "Ayaan", "Heer", "isoblade"); // Example list
    private List<String> displaylist = new ArrayList<>();
    private List<String> filteredList = new ArrayList<>();

    private ListView listView;
    private EditText searchBar;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.explore, container, false);
        Log.d("MyTag", "This is a debug message77777777777777777.");

        Log.d("MyTag", "Display list: ");

        // Initialize ListView
        listView = view.findViewById(R.id.list_view);
        searchBar = view.findViewById(R.id.search_bar);

        fetchUsernamesFromFirestore();
        Log.d("MyTag", "Display list: " + displaylist.toString());


        // Use ArrayAdapter for simple string lists
        adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.list_layout, // Use custom layout here
                R.id.item_text,     // Reference to the TextView inside list_item.xml
                filteredList
        );

        // Set adapter to listView
        listView.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Set onItemClickListener to handle clicks
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            // Get the selected username
            String selectedUsername = filteredList.get(position);


            // Create a new instance of SearchedUser and pass the username
            SearchedUser searchedUserFragment = SearchedUser.newInstance(selectedUsername);

            // Replace the current fragment with SearchedUser
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, searchedUserFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    private void fetchUsernamesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                displaylist.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    displaylist.add(document.getId()); // Document ID is the username
                }
                filterList(""); // Show all results initially
            } else {
                Log.e("Explore", "Error fetching usernames", task.getException());
            }
        });
    }
    /**
     * Filters the list based on the search query.
     *
     * @param query The string to filter by.
     */
    private void filterList(String query) {
        filteredList.clear(); // Clear the previous results
        if (query.isEmpty()) {
            filteredList.addAll(displaylist); // Show all items if query is empty
        } else {
            for (String username : displaylist) {
                if (username.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(username); // Add matching items to filtered list
                }
            }
        }
        adapter.notifyDataSetChanged(); // Refresh ListView with filtered items
    }
}
