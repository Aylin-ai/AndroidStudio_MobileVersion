package com.example.mobileversion.ui.piece;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PiecesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PiecesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is pieces fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
