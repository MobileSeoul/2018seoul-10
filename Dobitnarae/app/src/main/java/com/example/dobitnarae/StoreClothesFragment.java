package com.example.dobitnarae;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("ValidFragment")
public class StoreClothesFragment extends Fragment {
    private ArrayList<Clothes> originItems;
    private ClothesListRecyclerAdapter mAdapter;
    private ClothesCategoryListRecyclerAdapter cAdapter;
    private Store store;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public StoreClothesFragment(Store store) {
        this.store = store;
        //판매중인 옷 목록 가져옴
        this.originItems = JSONTask.getInstance().getClothesAll(store.getAdmin_id());
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static StoreClothesFragment newInstance(int sectionNumber, Store store) {
        StoreClothesFragment fragment = new StoreClothesFragment(store);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_store_clothes_list, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_clothes);
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        //옷추가

        // 성별로 분류
        ArrayList<Clothes> tmp = new ArrayList<>();
        int sex = ((StoreActivity)getContext()).getSelectedSex();
        for(Clothes item : originItems){
            if(item.getSex() == sex)
                tmp.add(item);
        }

        mAdapter = new ClothesListRecyclerAdapter(getContext(), tmp, store, R.layout.fragment_store_clothes_list);
        recyclerView.setAdapter(mAdapter);

        // clothes category 설정
        RecyclerView recyclerViewCategory = (RecyclerView) rootView.findViewById(R.id.clothes_category);
        LinearLayoutManager layoutManagerCategory = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategory.setLayoutManager(layoutManagerCategory);

        cAdapter = new ClothesCategoryListRecyclerAdapter(getContext(), originItems, mAdapter);
        recyclerViewCategory.setAdapter(cAdapter);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_layout_clothes_list);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mAdapter.setClothes(JSONTask.getInstance().getClothesAll(store.getAdmin_id()));
                originItems = JSONTask.getInstance().getClothesAll(store.getAdmin_id());
                swipeRefreshLayout.setRefreshing(false);
                refresh();
            }
        });

        return rootView;
    }

    public void refresh(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}