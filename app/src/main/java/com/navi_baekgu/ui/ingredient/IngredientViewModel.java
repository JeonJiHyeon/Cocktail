package com.navi_baekgu.ui.ingredient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IngredientViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public IngredientViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ingredient fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}