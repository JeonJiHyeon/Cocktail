package com.navi_baekgu.ui.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.navi_baekgu.databinding.FragmentRecipeBinding;

public class RecipeFragment extends Fragment {
    private FragmentRecipeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.Gin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("진");
            }
        });
        binding.Vodka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("보드카");
            }
        });
        binding.Rum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("럼");
            }
        });
        binding.Tequila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("데킬라");
            }
        });
        binding.Whiskey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("위스키");
            }
        });
        binding.Brandy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("브랜디");
            }
        });
        binding.Liqueur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("리큐르");
            }
        });
        binding.Wine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("와인");
            }
        });
        binding.Beer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("맥주");
            }
        });
        binding.Soju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("소주");
            }
        });

        binding.Non.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("논알콜");
            }
        });
        binding.AllRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("All");
            }
        });
        return root;
    }


    public void changeActivity(String subject) {
        Intent intent = new Intent(this.getActivity(), CocktaillistActivity.class);
        String str = subject;
        intent.putExtra("subject", str);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}