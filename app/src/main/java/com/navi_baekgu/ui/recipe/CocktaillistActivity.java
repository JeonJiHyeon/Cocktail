package com.navi_baekgu.ui.recipe;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.navi_baekgu.R;
import com.navi_baekgu.databinding.ActivityCocktaillistBinding;
import com.navi_baekgu.ui.recycler.CocktailAdapter;

import com.navi_baekgu.ui.recipe.DetailFragment;

public class CocktaillistActivity extends AppCompatActivity {
    private DetailFragment detailFragment;
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


        cocktaillistViewModel.getAdapter().setOnItemClickListener(new CocktailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(v.getContext(), position+1+"번째 칵테일을 골랐음", Toast.LENGTH_SHORT).show();
                //굳이 프래그먼트로 전환할 필요는 없따

            }
        });

        binding.setViewmodel(cocktaillistViewModel);
        binding.setLifecycleOwner(this);




    }

}