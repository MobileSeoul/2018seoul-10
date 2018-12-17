package com.example.dobitnarae;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BasketRecyclerAdapter extends RecyclerView.Adapter<BasketRecyclerAdapter.ViewHolder> {
    private Context context;
    private Basket basket;
    private ArrayList<BasketItem> basketItem;

    public BasketRecyclerAdapter(Context context) {
        this.context = context;
        this.basket = Basket.getInstance();
        this.basketItem = basket.getBasket();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.component_basket_cardview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BasketItem basketItem = this.basketItem.get(position);
        Clothes clothes = basketItem.getClothes();
        // 옷 이미지 가져옴
        ServerImg.getClothesImageGlide(context, clothes.getCloth_id(), holder.image);

        holder.delBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                showAlert(position);
            }
        });

        holder.name.setText(clothes.getName());
        holder.count.setText("" + basketItem.getCnt());
        holder.layout.setId(clothes.getCloth_id());
    }

    @Override
    public int getItemCount() {
        return this.basket.getClothesCnt();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, delBtn;
        TextView name, count;
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.basket_clothes_image);
            name = (TextView) itemView.findViewById(R.id.basket_clothes_name);
            count = (TextView) itemView.findViewById(R.id.basket_clothes_cnt);
            layout = (LinearLayout) itemView.findViewById(R.id.basket_clothes_layout);
            delBtn = (ImageView) itemView.findViewById(R.id.basket_clothes_delete_btn);
        }
    }

    private void showAlert(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String alertMsg, okBtnMsg, cancelBtnMsg;
        if(Locale.getDefault().getLanguage() == "ko") {
            alertMsg = "이 한복을 장바구니에서 빼시겠습니까?";
            okBtnMsg = "예";
            cancelBtnMsg = "아니오";
        }
        else {
            alertMsg = "Would you like to remove this Hanbok from your basket?";
            okBtnMsg = "Yes";
            cancelBtnMsg = "No";
        }

        builder.setMessage(alertMsg);
        builder.setPositiveButton(okBtnMsg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        basket.deleteClothes(position);
                        notifyDataSetChanged();
                        ((BasketActivity)context).setTotalCost();
                        ((BasketActivity) context).setTotalClothesCnt();
                    }
                });
        builder.setNegativeButton(cancelBtnMsg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}