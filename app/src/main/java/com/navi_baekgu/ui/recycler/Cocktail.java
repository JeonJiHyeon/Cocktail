package com.navi_baekgu.ui.recycler;

import java.util.ArrayList;

public class Cocktail {
    String id;
    String name;
    String base;
    ArrayList<String>[] recipe;

    public Cocktail(String id, String name, String base, ArrayList<String>[] recipe) {
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

    public ArrayList<String>[] getRecipe() {
        return recipe;
    }

    public void setRecipe(ArrayList<String>[] recipe) {
        this.recipe = recipe;
    }

}
