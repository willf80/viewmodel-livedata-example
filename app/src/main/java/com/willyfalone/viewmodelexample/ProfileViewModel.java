package com.willyfalone.viewmodelexample;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<Profile> profileLiveData;

    public LiveData<Profile> getProfileLiveData() {
        if(profileLiveData == null) {
            profileLiveData = new MutableLiveData<>();

            // Initialiser le profil
            profileLiveData.setValue(emptyProfile());
        }
        return profileLiveData;
    }

    public Profile getProfile() {
        return profileLiveData.getValue();
    }

    public void setProfile(Profile profile) {
        profileLiveData.setValue(profile);
    }

    private Profile emptyProfile() {
        return new Profile();
    }
}
