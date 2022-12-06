//package com.navi_baekgu.ui.recipe;
//
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.ViewModel;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.navi_baekgu.ui.recycler.Cocktail;
//import com.navi_baekgu.ui.recycler.CocktailAdapter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class CocktaillistViewModel extends ViewModel {
//    private static final String TAG = "CocktaillistViewModel";
//
//    private List<Cocktail> cocktails;
//    private CocktailAdapter adapter;
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    public CocktaillistViewModel() {
//        if (cocktails == null) {
//            cocktails = new ArrayList<>();
//        }
//
//        if (adapter == null) {
//            adapter = new CocktailAdapter(this);
//        }
//
//        getCocktailList();
//    }
//
//    public void getCocktailList() {
//        db.collection("cocktails")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            ArrayList<String>[] recipe = new ArrayList[3];
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                db.collection("cocktails").document(document.getId()).collection("recipe")
//                                        .get()
//                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                if (task.isSuccessful()) {
//                                                    int i = 0;
//                                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                                        recipe[i] = new ArrayList<>();
//                                                        recipe[i].add(document.get("order").toString());
//                                                        recipe[i].add(document.getString("ingredient"));
//                                                        recipe[i].add(document.get("quantity").toString());
//                                                        recipe[i].add(document.getString("unit"));
//                                                        recipe[i].add(document.getString("recipe_text"));
//                                                        i++;
//                                                        Log.d(TAG, document.getId() + " => " + document.getData());
//                                                    }
//                                                } else {
//                                                    Log.w(TAG, "Error getting documents.", task.getException());
//                                                }
//                                            }
//                                        });
//                                Cocktail cocktail = new Cocktail(document.getString("id"), document.getString("name"), document.getString("base"), recipe);
//                                cocktails.add(cocktail);
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }
//
//    public void onCreate() {
//        adapter.notifyDataSetChanged();
//    }
//
//    public void onResume() {
//    }
//
//    public CocktailAdapter getAdapter() {
//        return adapter;
//    }
//
//    public List<Cocktail> getCocktails() {
//        return cocktails;
//    }
//
//    public String getName(int pos) {
//        return cocktails.get(pos).getName();
//    }
//
//    public String getBase(int pos) {
//        return cocktails.get(pos).getBase();
//    }
//
//}
//
