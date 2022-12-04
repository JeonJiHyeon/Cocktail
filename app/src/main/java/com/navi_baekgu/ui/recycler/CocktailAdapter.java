package com.navi_baekgu.ui.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.navi_baekgu.databinding.LayoutCardviewBinding;
import com.navi_baekgu.ui.recipe.CocktaillistViewModel;

public class CocktailAdapter extends RecyclerView.Adapter<CocktailAdapter.ViewHolder> {
    private CocktaillistViewModel viewModel;


    public CocktailAdapter(CocktaillistViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public CocktailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutCardviewBinding binding = LayoutCardviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CocktailAdapter.ViewHolder holder, int position) {
        holder.onBind(viewModel, position);
    }

    @Override
    public int getItemCount() {
        return viewModel.getCocktails() == null ? 0 : viewModel.getCocktails().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LayoutCardviewBinding binding;

        public ViewHolder(LayoutCardviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(view, pos);
                        }

                    }
                }
            });

        }

        public void onBind(CocktaillistViewModel viewModel, int pos) {
            binding.setViewmodel(viewModel);
            binding.setPos(pos);
            binding.executePendingBindings();
        }
    }

    private OnItemClickListener mListener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


}
