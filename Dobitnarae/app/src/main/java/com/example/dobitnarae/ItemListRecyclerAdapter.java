package com.example.dobitnarae;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class ItemListRecyclerAdapter extends RecyclerView.Adapter<ItemListRecyclerAdapter.ViewHolder> {
    Context context;
    List<Clothes> clothes;
    int item_layout;
    Store store;

    public ItemListRecyclerAdapter(Context context, List<Clothes> items, Store store, int item_layout) {
        this.context = context;
        this.clothes = items;
        this.store = store;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_admin_item_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Clothes item = clothes.get(position);
        ServerImg.getAdminClothesImageGlide(context, item.getCloth_id(), holder.image);
        holder.name.setText(item.getName());
        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        holder.price.setText(dc.format(item.getPrice()) + " 원");
        holder.cardview.setId(item.getCloth_id());
    }

    @Override
    public int getItemCount() {
        return this.clothes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price;
        CardView cardview;
        LinearLayout layout_cardview;
        int clicked;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.clothes_name);
            price = (TextView) itemView.findViewById(R.id.clothes_price);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
            layout_cardview = (LinearLayout) itemView.findViewById(R.id.layout_cardview);
            clicked = 0;
        }
    }

    public void setClothes(List<Clothes> clothes) {
        this.clothes = clothes;
        this.notifyDataSetChanged();
    }
}