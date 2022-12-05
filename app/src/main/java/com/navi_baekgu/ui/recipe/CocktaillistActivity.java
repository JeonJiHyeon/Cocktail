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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private RecyclerView Rv;
    private ArrayList<String> list;
    private CocktailAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktaillist);
        Rv = findViewById(R.id.cocktail_lv);
        getCocktailList();

    }

    public void getCocktailList() {
        db.collection("cocktails")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Cocktail> cocktails = new ArrayList<Cocktail>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<ArrayList<String>> recipe = new ArrayList<>();
                                db.collection("cocktails").document(document.getId()).collection("recipe")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        ArrayList<String> recipe_steps = new ArrayList<>();
                                                        recipe_steps.add(document.get("order").toString());
                                                        recipe_steps.add(document.getString("ingredient"));
                                                        recipe_steps.add(document.get("quantity").toString());
                                                        recipe_steps.add(document.getString("unit"));
                                                        recipe_steps.add(document.getString("recipe_text"));
                                                        recipe.add(recipe_steps);
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                    }
                                                    // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                                                    Rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                                                    // 리사이클러뷰에 SimpleTextAdapter 객체 지정. 이부분에 따라 추가됨 지금은 2개 추
                                                    CocktailAdapter adapter = new CocktailAdapter(cocktails);
                                                    Log.d(TAG,  "로그"+cocktails.get(0).getId()+cocktails.get(0).getRecipe().get(0));
                                                    Rv.setAdapter(adapter);
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