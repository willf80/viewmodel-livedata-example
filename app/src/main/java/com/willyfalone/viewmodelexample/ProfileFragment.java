package com.willyfalone.viewmodelexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;


public class ProfileFragment extends Fragment {
    ProfileViewModel profileViewModel;
    TextInputEditText lastNameEditText;
    TextInputEditText firstNameEditText;
    TextView biographyTextView;
    Button btnValidate;

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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        biographyTextView = view.findViewById(R.id.biographyTextView);
        btnValidate = view.findViewById(R.id.btnValidate);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileViewModel.getProfileLiveData().observe(this, this::updateView);

        btnValidate.setOnClickListener(v -> updateProfileInfo());
    }

    public void updateView(Profile profile) {
        lastNameEditText.setText(profile.getLastName());
        firstNameEditText.setText(profile.getFirstName());
        biographyTextView.setText(profile.getBiography());
    }

    public void updateProfileInfo() {
        String lastName = lastNameEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();

        Profile profile = profileViewModel.getProfile();
        profile.setLastName(lastName);
        profile.setFirstName(firstName);

        profileViewModel.setProfile(profile);
    }

}