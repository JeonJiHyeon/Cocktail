package com.navi_baekgu.ui.recipe;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.navi_baekgu.databinding.ActivityCameraguideBinding;

public class CameraguideActivity extends AppCompatActivity {
    private ActivityCameraguideBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraguideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}
