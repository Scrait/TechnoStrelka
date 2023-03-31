package ru.scrait.technostrelka.ui.diagram;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DiagramViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DiagramViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}