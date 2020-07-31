package com.willyfalone.viewmodelexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class BiographyFragment extends Fragment {
    ProfileViewModel profileViewModel;
    EditText biographyEditText;
    TextView fullNameTextView;
    Button btnUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_biography, container, false);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        biographyEditText = view.findViewById(R.id.biographyEditText);
        fullNameTextView = view.findViewById(R.id.fullNameTextView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileViewModel.getProfileLiveData().observe(this, this::updateView);

        btnUpdate.setOnClickListener(v -> updateProfileInfo());
    }

    public void updateView(Profile profile) {
        fullNameTextView.setText(profile.getFullName());
        biographyEditText.setText(profile.getBiography());
    }

    public void updateProfileInfo() {
        String biography = biographyEditText.getText().toString();

        Profile profile = profileViewModel.getProfile();
        profile.setBiography(biography);

        profileViewModel.setProfile(profile);
    }
}