package com.navi_baekgu.ui.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.navi_baekgu.R;
import com.navi_baekgu.databinding.ActivityCocktaillistBinding;
import com.navi_baekgu.ui.recycler.Cocktail;
import com.navi_baekgu.ui.recycler.CocktailAdapter;

import java.util.ArrayList;
import java.util.List;

public class CocktaillistActivity extends AppCompatActivity {
    private static final String TAG = "CocktaillistActivity";
//    private ActivityCocktaillistBinding binding;
//    private CocktaillistViewModel cocktaillistViewModel;
    private ListView cocktaillistView;

    private CocktailAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktaillist);

        cocktaillistView = findViewById(R.id.cocktail_lv);

        getCocktailList();
//        binding = ActivityCocktaillistBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        cocktaillistViewModel =
//                new ViewModelProvider(this).get(CocktaillistViewModel.class);
//        cocktaillistViewModel.onCreate();
//
//        cocktaillistViewModel.getAdapter().setOnItemClickListener(new CocktailAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//                Toast.makeText(v.getContext(), position + 1 + "번째 칵테일을 골랐음", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(v.getContext(), DetailActivity.class);
//                startActivity(intent);
//
//                finish();
//
//            }
//        });
//
//        binding.setViewmodel(cocktaillistViewModel);
//        binding.setLifecycleOwner(this);


    }

    public void getCocktailList() {
        db.collection("cocktails")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Cocktail> cocktails = new ArrayList<>();
                            ArrayList<String>[] recipe = new ArrayList[3];
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("cocktails").document(document.getId()).collection("recipe")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    int i = 0;
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        recipe[i] = new ArrayList<>();
                                                        recipe[i].add(document.get("order").toString());
                                                        recipe[i].add(document.getString("ingredient"));
                                                        recipe[i].add(document.get("quantity").toString());
                                                        recipe[i].add(document.getString("unit"));
                                                        recipe[i].add(document.getString("recipe_text"));
                                                        i++;
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                    }
                                                    adapter = new CocktailAdapter(CocktaillistActivity.this, cocktails);
                                                    cocktaillistView.setAdapter(adapter);
                                                    cocktaillistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                            Toast.makeText(view.getContext(), i+1+"번째 칵테일을 골랐음", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                } else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                }
                                            }
                                        });
                                Cocktail cocktail = new Cocktail(document.getString("id"), document.getString("name"), document.getString("base"), recipe);
                                cocktails.add(cocktail);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}