package com.example.dobitnarae;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderListRecyclerAdapter extends RecyclerView.Adapter<OrderListRecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Order> orders;
    private Store store;

    public OrderListRecyclerAdapter(Context context, ArrayList<Order> orders, Store store) {
        this.context = context;
        this.orders = orders;
        this.store = store;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.component_listview_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Order item = orders.get(position);

        int sum = 0;
        for(int i = 0; i < item.getBasket().size(); i++)
            sum += item.getBasket().get(i).getCnt();

        ServerImg.getClothesImageGlide(context, item.getBasket().get(0).getClothes().getCloth_id(), holder.iv_main);
        if(sum!=0 && sum != 1)
            holder.tv_basket.setText(item.getBasket().get(0).getClothes().getName() + " 등 " + sum + "벌");
        else if(sum==1)
            holder.tv_basket.setText(item.getBasket().get(0).getClothes().getName() + " 1벌");
        else
            holder.tv_basket.setText("비어있음");
        String[] date = item.getRentalDate().split(":");
        holder.tv_date.setText(String.format("%s:%s", date[0], date[1]));
        holder.tv_accept.setId(item.getAcceptStatus());

        if(item.getAcceptStatus()==0) {
            holder.layout_accept.setBackgroundResource(R.drawable.border_all_layout_item_gray);
            holder.tv_accept.setText("승인 대기");
            holder.tv_accept.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.contentColor));
        } else if(item.getAcceptStatus()==1) {
            holder.layout_accept.setBackgroundResource(R.drawable.border_all_layout_item_green);
            holder.tv_accept.setText("승인");
            holder.tv_accept.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.storeOpeningColor));
        } else if(item.getAcceptStatus()==2) {
            holder.layout_accept.setBackgroundResource(R.drawable.border_all_layout_item_red);
            holder.tv_accept.setText("거절");
            holder.tv_accept.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.storeClosingColor));
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderSpecificActivity.class);
                intent.putExtra("order", item);
                intent.putExtra("store", store);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout, layout_accept;
        public ImageView iv_main;
        public TextView tv_basket, tv_date, tv_accept;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.order_list_item);
            iv_main = (ImageView) itemView.findViewById(R.id.order_list_img);
            tv_basket = (TextView) itemView.findViewById(R.id.order_basket);
            tv_date = (TextView) itemView.findViewById(R.id.order_date);
            layout_accept = (LinearLayout) itemView.findViewById(R.id.order_accept_layout);
            tv_accept = (TextView) itemView.findViewById(R.id.order_accept_tv);
        }
    }

    public void setOrders(ArrayList<Order> order) {
        this.orders = order;
        this.notifyDataSetChanged();
    }
}