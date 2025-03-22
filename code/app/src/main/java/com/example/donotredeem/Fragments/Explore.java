package com.example.donotredeem.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.donotredeem.R;

import java.util.Arrays;
import java.util.List;

public class Explore extends Fragment {

    private List<String> displaylist = Arrays.asList("AG", "Ayaan"); // Example list
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.explore, container, false);

        // Initialize ListView
        listView = view.findViewById(R.id.list_view);

        // Use ArrayAdapter for simple string lists
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,  // Predefined layout
                displaylist
        );

        // Set adapter to listView
        listView.setAdapter(adapter);

        // Set onItemClickListener to handle clicks
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected username
                String selectedUsername = displaylist.get(position);

                // Create a new instance of DetailFragment and pass the username
                SearchedUser searchedUser = SearchedUser.newInstance(selectedUsername);

                // Replace the current fragment with DetailFragment
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, SearchedUser());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}
