package com.navi_baekgu.ui.recycler;

public class Cocktail {
    int resourceId;
    String CockTail_name;
    String CockTail_recipe;
    String CockTail_tag;

    public Cocktail(int resourceId, String name, String recipe, String tag) {
        this.resourceId = resourceId;
        this.CockTail_name = name;
        this.CockTail_recipe = recipe;
        this.CockTail_tag = tag;
    }

    public int getresourceId() {
        return resourceId;
    }

    public void setresourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getname() {
        return CockTail_name;
    }

    public void setname(String name) {
        this.CockTail_name = name;
    }

    public String getrecipe() {
        return CockTail_recipe;
    }

    public void setrecipe(String recipe) {
        this.CockTail_recipe = recipe;
    }

    public String gettag() {
        return CockTail_tag;
    }

    public void settag(String tag) {
        this.CockTail_tag = tag;
    }

}
