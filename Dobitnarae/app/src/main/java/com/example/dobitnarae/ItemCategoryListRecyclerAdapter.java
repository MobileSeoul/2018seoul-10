package com.example.dobitnarae;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ItemCategoryListRecyclerAdapter extends RecyclerView.Adapter<ItemCategoryListRecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Clothes> originItems;
    private ItemListRecyclerAdapter mAdapter;
    private ArrayList<ViewHolder> categories;

    public ItemCategoryListRecyclerAdapter(Context context, ArrayList<Clothes> originItems, ItemListRecyclerAdapter mAdapter) {
        this.context = context;
        this.originItems = originItems;
        this.mAdapter = mAdapter;
        this.categories = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.component_clothes_category, parent, false);
        ViewHolder tmp = new ViewHolder(v);
        categories.add(tmp);
        return tmp;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.text.setText(Constant.CATEGORY[position]);
        holder.layout.setTag(position);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 전부 다시 원상태 색으로 되돌림
                for(ViewHolder h : categories){
                    h.text.setTextColor(v.getResources().getColor(R.color.appMainColor));
                    h.layout.setSelected(false);
                }
                // 카테고리 메뉴 선택됨 표시
                holder.text.setTextColor(v.getResources().getColor(R.color.white));
                holder.layout.setSelected(true);

                // 선택된 분야의 한복 얻어옴
                List<Clothes> clothes = getClothesList((int)v.getTag());
                mAdapter.setClothes(clothes);
            }
        });
        // 처음 프레그먼트 생성시 "전체"분류가 체크 되있도록함
        if(position == 0){
            holder.text.setTextColor(context.getResources().getColor(R.color.white));
            holder.layout.setSelected(true);
        }
    }

    @Override
    public int getItemCount() {
        return Constant.CATEGORY_CNT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.clothes_category_layout);
            text = (TextView) itemView.findViewById(R.id.clothes_category_txt);
        }
    }

    public ArrayList<Clothes> getClothesList(int category){
        ArrayList<Clothes> res = new ArrayList<>();

        // 분류: 전체
        if(category == 0) {
            return originItems;
        }

        for(Clothes item : originItems){
            if(item.getCategory() == category)
                res.add(item);
        }
        return res;
    }
}