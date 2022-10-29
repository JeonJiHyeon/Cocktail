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
    private FragmentRecipeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

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