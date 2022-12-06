package com.navi_baekgu.ui.recycler;

import java.io.Serializable;
import java.util.ArrayList;

//인텐트로 보내주려면 직렬화해야해서 관련된 것들을 상속시킴
public class Cocktail implements Serializable {
    String id;
    String name;
    String base;
    ArrayList<ArrayList<String>> recipe;

    public Cocktail(String id, String name, String base, ArrayList<ArrayList<String>> recipe) {
        this.id = id;
        this.name = name;
        this.base = base;
        this.recipe = recipe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public ArrayList<ArrayList<String>> getRecipe() {
        return recipe;
    }

    public void setRecipe(ArrayList<ArrayList<String>> recipe) {
        this.recipe = recipe;
    }

}
