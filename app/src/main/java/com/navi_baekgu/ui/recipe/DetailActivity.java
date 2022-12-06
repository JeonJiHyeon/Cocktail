package com.navi_baekgu.ui.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.navi_baekgu.databinding.ActivityDetailBinding;
import com.navi_baekgu.ui.recycler.Cocktail;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private ArrayList<ArrayList<String>> recipe;
    private String ingredient_string = "";
    private String recipe_string = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Cocktail selected_cocktail = (Cocktail) intent.getSerializableExtra("selected_cocktail"); // 직렬화된 객체를 받는 방법
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        binding.cocktailName.setText(selected_cocktail.getName());
        //이미지 바꾸는 부분을 추가해야함
        ViewGroup.LayoutParams params = binding.detailLl.getLayoutParams();
        recipe = selected_cocktail.getRecipe();
        for(int i = 0; i<recipe.size(); i++){
            if (recipe.get(i).get(1).length()>0){
                ingredient_string = ingredient_string + recipe.get(i).get(1) + ",\n";
            }
            recipe_string = recipe_string + (i+1+". ") + recipe.get(i).get(4) + "\n";
            if (i>6){
                params.height = params.height + 20;
                Log.i("info", ""+params.height);
                binding.detailLl.setLayoutParams(params);
            }
        }
        setContentView(binding.getRoot());
        ingredient_string  = ingredient_string .substring(0, ingredient_string .length() - 2);
        binding.cocktailIngredients.setText(ingredient_string);
        binding.cocktailRecipe.setText(recipe_string);
        binding.GuideCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이부분 맞나
                Intent intent = new Intent(getBaseContext(), CameraguideActivity.class);
                intent.putExtra("selected_cocktail", selected_cocktail);
                startActivity(intent);

                finish();
            }
        });


    }
}
