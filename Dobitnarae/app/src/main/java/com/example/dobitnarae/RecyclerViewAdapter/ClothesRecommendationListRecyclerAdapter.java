package com.example.dobitnarae.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dobitnarae.Clothes;
import com.example.dobitnarae.ClothesReservationActivity;
import com.example.dobitnarae.JSONTask;
import com.example.dobitnarae.R;
import com.example.dobitnarae.ServerImg;
import com.example.dobitnarae.Store;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ClothesRecommendationListRecyclerAdapter extends RecyclerView.Adapter<ClothesRecommendationListRecyclerAdapter.ViewHolder> {
    Context context;
    List<Clothes> clothes;
    ArrayList<Store> stores;

    public ClothesRecommendationListRecyclerAdapter(Context context, List<Clothes> items) {
        this.context = context;
        this.clothes = items;

        stores = new ArrayList<>();
        for(Clothes item : items){
            String adminID = JSONTask.getInstance().changeToAdminID(item.getStore_id());
            final Store store = JSONTask.getInstance().getAdminStoreAll(adminID).get(0);
            stores.add(store);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_clothes_recommend_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Clothes item = clothes.get(position);
        final Store store = stores.get(position);

        ServerImg.getClothesImageGlide(context, item.getCloth_id(), holder.image);

        int clothesCnt = item.getCount();
        Drawable itemForegroundColor;
        if(clothesCnt == 0) {
            int color = 0x7f000000;
            itemForegroundColor = new ColorDrawable(color);
        }
        else{
            int color = 0x00000000;
            itemForegroundColor = new ColorDrawable(color);
        }
        holder.cardview.setForeground(itemForegroundColor);

        holder.storeName.setText(store.getName());
        holder.clothesName.setText(item.getName());

        holder.cardview.setId(item.getCloth_id());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClothesReservationActivity.class);
                intent.putExtra("clothes", item);
                intent.putExtra("store", store);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.clothes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView storeName, clothesName;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.recommend_clothes_image);
            storeName = (TextView) itemView.findViewById(R.id.recommend_store_name);
            clothesName = (TextView) itemView.findViewById(R.id.recommend_clothes_name);
            cardview = (CardView) itemView.findViewById(R.id.recommend_cardview);
        }
    }

    public void setClothes(List<Clothes> clothes) {
        this.clothes = clothes;
        this.notifyDataSetChanged();
    }
}