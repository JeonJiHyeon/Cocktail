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
                            ArrayList<ArrayList<String>> datas = new ArrayList<ArrayList<String>>();
                            ArrayList<String> arr = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "바깥 포문"+document.getId() + " => " + document.getData());
                                arr.add(document.getId());
                                arr.add(document.getString("name"));
                                arr.add(document.getString("base"));
                                db.collection("cocktails").document(document.getId()).collection("recipe")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        arr.add(document.getId());
                                                        arr.add(document.get("order").toString());
                                                        arr.add(document.getString("ingredient"));
                                                        arr.add(document.get("quantity").toString());
                                                        arr.add(document.getString("unit"));
                                                        arr.add(document.getString("recipe_text"));
                                                        datas.add(arr);
                                                        Log.d(TAG, "안쪽 포문"+document.getId() + " => " + document.getData());
                                                    }
                                                    // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                                                    Rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                                                    // 리사이클러뷰에 SimpleTextAdapter 객체 지정. 이부분에 따라 추가됨 지금은 2개 추
                                                    CocktailAdapter adapter = new CocktailAdapter(datas);
                                                    Rv.setAdapter(adapter);
                                                } else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                }
                                            }
                                        });

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}