package com.example.dobitnarae;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class ItemManagementFragment extends Fragment{
    private ArrayList<Clothes> originItems, items;
    private ItemListRecyclerAdapter mAdapter;
    private ItemCategoryListRecyclerAdapter cAdapter;
    private Store store;
    public ArrayList<Clothes> deleteList;

    private Animation fabOpen, fabClose;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    public static boolean changeFlg = false;
    public static boolean checkFlg = false;

    public ItemManagementFragment(Store store) {
        this.store = store;
        this.originItems = JSONTask.getInstance().getClothesAll(store.getAdmin_id());
        this.items = getClothesList(0);
        deleteList = new ArrayList<Clothes>();
    }

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static ItemManagementFragment newInstance(int sectionNumber, Store store) {
        ItemManagementFragment fragment = new ItemManagementFragment(store);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_management_item, container, false);

        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_clothes);
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ItemListRecyclerAdapter(getActivity(), items, store, R.layout.fragment_store_clothes_list){
            @Override
            public void onBindViewHolder(final ViewHolder holder, final int position) {
                super.onBindViewHolder(holder, position);
                final Clothes item = clothes.get(position);
                holder.cardview.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(holder.clicked == 0) {
                            holder.clicked = 1;
                            if(!deleteList.contains(items.get(position)))
                                deleteList.add(items.get(position));
                            holder.layout_cardview.setBackgroundResource(R.drawable.cardview_border);
                        }
                        else {
                            holder.clicked = 0;
                            if(deleteList.contains(items.get(position)))
                                deleteList.remove(items.get(position));
                            holder.layout_cardview.setBackgroundResource(R.drawable.cardview_bordernone);
                        }
                        if(deleteList.size()>0)
                            checkFlg = true;
                        else
                            checkFlg = false;

                        // 리턴값이 있다
                        // 이메서드에서 이벤트에대한 처리를 끝냈음
                        //    그래서 다른데서는 처리할 필요없음 true
                        // 여기서 이벤트 처리를 못했을 경우는 false

                        return true;
                    }
                });

                holder.cardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(deleteList.size()==0)
                            checkFlg = false;
                        if(ItemManagementFragment.checkFlg == false) {
                            Intent intent = new Intent(context, ItemSpecificActivity.class);
                            intent.putExtra("clothesid", item.getCloth_id());
                            intent.putExtra("store", store);
                            context.startActivity(intent);
                        }
                        else {
                            if(holder.clicked == 0) {
                                holder.clicked = 1;
                                if(!deleteList.contains(items.get(position)))
                                    deleteList.add(items.get(position));
                                holder.layout_cardview.setBackgroundResource(R.drawable.cardview_border);
                            }
                            else {
                                holder.clicked = 0;
                                if(deleteList.contains(items.get(position)))
                                    deleteList.remove(items.get(position));
                                holder.layout_cardview.setBackgroundResource(R.drawable.cardview_bordernone);
                            }
                        }

                    }
                });
            }
        };
        recyclerView.setAdapter(mAdapter);

        RecyclerView recyclerViewCategory = (RecyclerView) rootView.findViewById(R.id.clothes_category);
        LinearLayoutManager layoutManagerCategory = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategory.setLayoutManager(layoutManagerCategory);

        cAdapter = new ItemCategoryListRecyclerAdapter(getContext(), originItems, mAdapter);
        recyclerViewCategory.setAdapter(cAdapter);

        // 당겨서 새로고침
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataRefresh();

                // 새로고침 완료
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        fabOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) rootView.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) rootView.findViewById(R.id.fab2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                if(deleteList.size()!=0) {
                    for (Clothes tmp : deleteList)
                        JSONTask.getInstance().deleteCloth(tmp.getCloth_id());
                    originItems = JSONTask.getInstance().getClothesAll(store.getAdmin_id());
                    items = getClothesList(0);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), deleteList.size() + "개 항목이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    deleteList.clear();
                    refresh();
                } else {
                    Toast.makeText(getActivity(), "삭제할 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                Intent intent = new Intent(getContext(), ItemAddActivity.class);
                intent.putExtra("store", store);
                startActivity(intent);
                refresh();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(changeFlg) {
            dataRefresh();
            changeFlg = false;
        }
        deleteList.clear();
    }

    public void anim() {
        if (isFabOpen) {
            fab1.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    public ArrayList<Clothes> getClothesList(int category){
        ArrayList<Clothes> tmp = new ArrayList<>();
        if(category == 0)
            return originItems;

        for(int i=0; i<originItems.size(); i++){
            Clothes item = originItems.get(i);
            if(item.getCategory() == category)
                tmp.add(item);
        }
        return tmp;
    }

    public void refresh(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    public void dataRefresh(){
        // 새로고침
        originItems = JSONTask.getInstance().getClothesAll(store.getAdmin_id());
        items = getClothesList(0);
        mAdapter.setClothes(items);
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "새로고침 되었습니다.", Toast.LENGTH_SHORT).show();
        refresh();
    }
}
