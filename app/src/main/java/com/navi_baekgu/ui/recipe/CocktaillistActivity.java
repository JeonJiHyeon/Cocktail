package com.navi_baekgu.ui.recipe;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.navi_baekgu.databinding.ActivityCocktaillistBinding;

public class CocktaillistActivity extends AppCompatActivity {

    private ActivityCocktaillistBinding binding;
    private CocktaillistViewModel cocktaillistViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCocktaillistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cocktaillistViewModel =
                new ViewModelProvider(this).get(CocktaillistViewModel.class);
        cocktaillistViewModel.onCreate();

        binding.setViewmodel(cocktaillistViewModel);
        binding.setLifecycleOwner(this);


    }

}