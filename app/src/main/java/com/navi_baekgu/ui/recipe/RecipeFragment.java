package com.navi_baekgu.ui.recipe;
import java.text.MessageFormat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.navi_baekgu.R;
import com.navi_baekgu.databinding.FragmentRecipeBinding;

import java.util.ArrayList;

public class RecipeFragment extends Fragment {
    private Button[] mButton = new Button[12];
    Integer[] tablebutton = {
            R.id.RecipeButton1, R.id.RecipeButton2, R.id.RecipeButton3, R.id.RecipeButton4, R.id.RecipeButton5,
            R.id.RecipeButton6, R.id.RecipeButton7, R.id.RecipeButton8, R.id.RecipeButton9, R.id.RecipeButton10,
            R.id.RecipeButton11,R.id.RecipeButton12,
    };
//    private RecipeViewModel recipeViewModel;
    private FragmentRecipeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        UI와 관련된 데이터들을 조작하고 그럴때 쓰는건데 지금은 그럴 일이 없음. 혹시 모르니 남겨놓기...
//        recipeViewModel =
//                new ViewModelProvider(this).get(RecipeViewModel.class);


        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.RecipeButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity("ALL");
            }
        });
        return root;
    }



    public void changeActivity(String subject){
        Intent intent = new Intent(this.getActivity(),CocktaillistActivity.class);
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