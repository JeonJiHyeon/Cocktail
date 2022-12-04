package com.navi_baekgu.ui.recipe;

import androidx.lifecycle.ViewModel;

import com.navi_baekgu.ui.recycler.Cocktail;
import com.navi_baekgu.ui.recycler.CocktailAdapter;

import java.util.ArrayList;
import java.util.List;


public class CocktaillistViewModel extends ViewModel {

    private List<Cocktail> cocktails;
    private CocktailAdapter adapter;

    public CocktaillistViewModel() {
        if (cocktails == null) {
            cocktails = new ArrayList<>();
        }

        if (adapter == null) {
            adapter = new CocktailAdapter(this);
        }

        testLogic();
    }

    public void testLogic() {
        for (int i = 0; i < 30; i++) {
            Cocktail cocktail = new Cocktail(123, "마티니", "알아서 해먹으삼", "안먹어봐서 몰루");
            cocktails.add(cocktail);
        }
    }

    public void onCreate() {
        adapter.notifyDataSetChanged();
    }

    public void onResume() {
    }

    public CocktailAdapter getAdapter() {
        return adapter;
    }

    public List<Cocktail> getCocktails() {
        return cocktails;
    }

    public String getname(int pos) {
        return cocktails.get(pos).getname();
    }

    public String gettags(int pos) {
        return cocktails.get(pos).gettag();
    }

    public void setname(int pos, String str) {
        cocktails.get(pos).setname(str);
    }


}

