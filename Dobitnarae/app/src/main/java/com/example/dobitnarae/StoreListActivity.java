package com.example.dobitnarae;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Objects;

public class StoreListActivity extends AppCompatActivity {
    ArrayList<Store> stores;
    StoreListRecyclerAdapter mAdapter;
    int sector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton backButton = (ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        LinearLayout basket = (LinearLayout)findViewById(R.id.store_basket);
        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreListActivity.this, BasketActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_store_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        // 구역에 따른 store 목록 가져오기
        Intent intent = getIntent();
        sector = (int)intent.getIntExtra("sector", 1);
        stores = JSONTask.getInstance().getStoreBySector(sector);

        mAdapter = new StoreListRecyclerAdapter(this, stores);
        recyclerView.setAdapter(mAdapter);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout_store_list);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.setStores(JSONTask.getInstance().getStoreBySector(sector));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
