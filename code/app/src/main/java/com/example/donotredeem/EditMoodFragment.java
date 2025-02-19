package com.example.donotredeem;

import android.content.Context;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class EditMoodFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View add_edit_fragment = inflater.inflate(R.layout., container, false);

        return add_edit_fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        EditText New_Text_Description = getView().getRootView().findViewById();
        EditText New_Location = getView().getRootView().findViewById();
        EditText New_Trigger =  getView().getRootView().findViewById();
        EditText New_Situation = getView().getRootView().findViewById();
        EditText New_Date = getView().getRootView().findViewById();
        EditText New_Time = getView().getRootView().findViewById();
        EditText New_Place = getView().getRootView().findViewById();
        Spinner New_Emotion = getView().getRootView().findViewById();

    }
}
