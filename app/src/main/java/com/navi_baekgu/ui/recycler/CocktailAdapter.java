package com.navi_baekgu.ui.recycler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.navi_baekgu.R;
import com.navi_baekgu.ui.recipe.DetailActivity;

import java.util.ArrayList;


public class CocktailAdapter extends RecyclerView.Adapter<CocktailAdapter.ViewHolder> {
    ArrayList<Cocktail> mdatas;
    private Context context;

    public CocktailAdapter(ArrayList<Cocktail> datas, Context context) {
        this.mdatas = datas;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cocktailName, cocktailBase;
        ImageView cocktailImage;

        public ViewHolder(View itemView) {
            super(itemView);
            cocktailName = itemView.findViewById(R.id.cardname);
            cocktailBase = itemView.findViewById(R.id.cardbase);
            cocktailImage = itemView.findViewById(R.id.cocktail_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position_info = getAdapterPosition();
                    if (position_info != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(v.getContext(), DetailActivity.class);
                        intent.putExtra("selected_cocktail", mdatas.get(position_info));
                        v.getContext().startActivity(intent);

                    }
                }
            });
        }

        public void setItem(Context context, Cocktail item) {
            FirebaseStorage storage;
            StorageReference storageReference;
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            StorageReference pathReference = storageReference.child(item.getId() + ".jpeg");
            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(cocktailImage);
                }
            });
        }
    }

    @NonNull
    @Override
    //	viewType 형태의 아이템 뷰를 위한 뷰홀더 객체 생성.
    public CocktailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.layout_cardview, parent, false);
        CocktailAdapter.ViewHolder viewHolder = new CocktailAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cocktailName.setText(mdatas.get(position).getName());
        holder.cocktailBase.setText(mdatas.get(position).getBase());
        holder.setItem(context, mdatas.get(position));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    //	전체 아이템 갯수 리턴.
    public int getItemCount() {
        return mdatas.size();
    }

}
