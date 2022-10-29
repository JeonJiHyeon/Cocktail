package com.navi_baekgu.ui.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.navi_baekgu.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.GuideCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이부분 맞나
                Intent intent = new Intent(getBaseContext(), CameraguideActivity.class);
                startActivity(intent);

                finish();
            }
        });


    }
}
