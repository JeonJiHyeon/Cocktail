package com.navi_baekgu.ui.ingredient;

import android.graphics.Outline;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.navi_baekgu.databinding.FragmentIngredientBinding;

public class IngredientFragment extends Fragment {

    private IngredientViewModel ingredientViewModel;
    private FragmentIngredientBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ingredientViewModel =
                new ViewModelProvider(this).get(IngredientViewModel.class);

        binding = FragmentIngredientBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final ImageView imageView2 = binding.imageView2;

        ViewOutlineProvider mViewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(final View view, final Outline outline) {
                float cornerRadiusDP = 16f;
                float cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cornerRadiusDP, getResources().getDisplayMetrics());
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadius);
            }
        };
        imageView2.setOutlineProvider(mViewOutlineProvider);
        imageView2.setClipToOutline(true);
//        final TextView textView = binding.textIngredient;
//        ingredientViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}