package com.navi_baekgu.ui.recycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.navi_baekgu.R;
import com.navi_baekgu.databinding.LayoutCardviewBinding;
//import com.navi_baekgu.ui.recipe.CocktaillistViewModel;

import java.util.ArrayList;
import java.util.List;

//public class CocktailAdapter extends RecyclerView.Adapter<CocktailAdapter.ViewHolder> {
//    private CocktaillistViewModel viewModel;
public class CocktailAdapter extends BaseAdapter {
    private static final String TAG = "CocktailAdapter";

//    public CocktailAdapter(CocktaillistViewModel viewModel) {
//        this.viewModel = viewModel;
//    }

    private ArrayList<Cocktail> mCocktails;
    private Context mContext;

    private TextView cocktailName;
    private TextView cocktailBase;

    public CocktailAdapter(Context mContext, ArrayList<Cocktail> mCocktails){
        this.mContext = mContext;
        this.mCocktails = mCocktails;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Cocktail getItem(int i) {
        return mCocktails.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(mContext).inflate(R.layout.layout_cardview, null);
        Log.d(TAG, "hi");
        cocktailName = view.findViewById(R.id.textView11);
        Log.d(TAG, mCocktails.get(i).getName());
        cocktailName.setText(mCocktails.get(i).getName());
        Log.d(TAG, mCocktails.get(i).getBase());
        cocktailBase = view.findViewById(R.id.textView12);
        cocktailBase.setText(mCocktails.get(i).getBase());
        return view;
    }

//    @NonNull
//    @Override
//    public CocktailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutCardviewBinding binding = LayoutCardviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//        return new ViewHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CocktailAdapter.ViewHolder holder, int position) {
//        holder.onBind(viewModel, position);
//    }
//
//    @Override
//    public int getItemCount() {
//        return viewModel.getCocktails() == null ? 0 : viewModel.getCocktails().size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        private LayoutCardviewBinding binding;
//
//        public ViewHolder(LayoutCardviewBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//
//            binding.getRoot().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int pos = getAdapterPosition();
//                    if (pos != RecyclerView.NO_POSITION) {
//                        if (mListener != null) {
//                            mListener.onItemClick(view, pos);
//                        }
//
//                    }
//                }
//            });
//
//        }
//
//        public void onBind(CocktaillistViewModel viewModel, int pos) {
//            binding.setViewmodel(viewModel);
//            binding.setPos(pos);
//            binding.executePendingBindings();
//        }
//    }
//
//    private OnItemClickListener mListener = null;
//
//    public interface OnItemClickListener {
//        void onItemClick(View v, int position);
//    }
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.mListener = listener;
//    }
//

}
