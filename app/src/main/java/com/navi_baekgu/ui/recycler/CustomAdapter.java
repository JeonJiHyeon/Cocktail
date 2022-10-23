package com.navi_baekgu.ui.recycler;

import android.widget.LinearLayout;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter {
    @BindingAdapter("bind:verAdapter")
    public static void verAdapter(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}
