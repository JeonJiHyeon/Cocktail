package com.navi_baekgu.ui.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.navi_baekgu.R;
import com.navi_baekgu.ui.recycler.Cocktail;
import com.navi_baekgu.ui.recycler.CocktailAdapter;

import java.util.ArrayList;

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
        Intent intent = getIntent();
        String Base = (String) intent.getSerializableExtra("subject"); // 직렬화된 객체를 받는 방법
        Rv = findViewById(R.id.cocktail_lv);
        if (Base.equals("All")) {
            getCocktailList_all();
        } else {
            getCocktailList(Base);
        }

    }

    public void getCocktailList(String Base) {
        db.collection("cocktails")
                .whereEqualTo("base", Base)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //getCocktailList 여기 매개변수로 Base 받은곳에 사용자가 선택한 버튼(진 버튼이면 진, 럼이면 럼, 보드카면 보드카)이뭔지 알수있음
                        //all은 전부 띄워야하구 나머지는 그에 맞는 칵테일만 띄워주면됨!!
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
                                                    CocktailAdapter adapter = new CocktailAdapter(cocktails, getApplicationContext());
                                                    Rv.setAdapter(adapter);
                                                } else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                }
                                            }
                                        });
                                Cocktail cocktail = new Cocktail(document.getId(), document.getString("name"), document.getString("base"), recipe);
                                cocktails.add(cocktail);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void getCocktailList_all() {
        db.collection("cocktails")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //getCocktailList 여기 매개변수로 Base 받은곳에 사용자가 선택한 버튼(진 버튼이면 진, 럼이면 럼, 보드카면 보드카)이뭔지 알수있음
                        //all은 전부 띄워야하구 나머지는 그에 맞는 칵테일만 띄워주면됨!!
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
                                                    CocktailAdapter adapter = new CocktailAdapter(cocktails, getApplicationContext());
                                                    Rv.setAdapter(adapter);
                                                } else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                }
                                            }
                                        });
                                Cocktail cocktail = new Cocktail(document.getId(), document.getString("name"), document.getString("base"), recipe);
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