package com.navi_baekgu.ui.recycler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.navi_baekgu.R;
import com.navi_baekgu.databinding.LayoutCardviewBinding;
import com.navi_baekgu.ui.recipe.CocktaillistActivity;
import com.navi_baekgu.ui.recipe.DetailActivity;
//import com.navi_baekgu.ui.recipe.CocktaillistViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class CocktailAdapter extends RecyclerView.Adapter<CocktailAdapter.ViewHolder> {
    ArrayList<Cocktail> mdatas;

    public CocktailAdapter(ArrayList<Cocktail> datas) {
        this.mdatas = datas;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView cocktailName, cocktailBase;
        ViewHolder(View itemView){
            super(itemView);
            cocktailName = itemView.findViewById(R.id.cardname);
            cocktailBase = itemView.findViewById(R.id.cardbase);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position_info = getAdapterPosition();
                    if(position_info != RecyclerView.NO_POSITION){
                        Toast.makeText(v.getContext(), position_info + 1 + "번째 칵테일을 골랐음", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(v.getContext(), DetailActivity.class);
                        v.getContext().startActivity(intent);

                    }
                }
            });
        }
    }

    @NonNull
    @Override
    //	viewType 형태의 아이템 뷰를 위한 뷰홀더 객체 생성.
    public CocktailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.layout_cardview, parent, false) ;
        CocktailAdapter.ViewHolder viewHolder = new CocktailAdapter.ViewHolder(view) ;

        return viewHolder;
    }

    @Override
    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cocktailName.setText(mdatas.get(position).getName());
        holder.cocktailBase.setText(mdatas.get(position).getBase());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    //	전체 아이템 갯수 리턴.
    public int getItemCount() {
        return mdatas.size() ;
    }

}
