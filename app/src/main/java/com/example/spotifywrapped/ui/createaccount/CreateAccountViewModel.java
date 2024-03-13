package com.example.spotifywrapped.ui.createaccount;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateAccountViewModel extends ViewModel {

    // implementation pending: needs firebase to handle the backend. Sprint 1 targets are UI
    // The following constructor is a sample that will be modified in the next sprint.
    private final MutableLiveData<String> mText;

    public CreateAccountViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
