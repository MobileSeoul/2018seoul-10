package com.example.dobitnarae;


import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ReservationInfoActivity extends AppCompatActivity {

    private Reserve reserve;
    private ArrayList<BasketItem> clothes;
    private TextView totalClothesCnt, reservationCost;
    private ReservationInfoRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_info);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton backButton = (ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        Intent intent = getIntent();
        reserve = (Reserve) intent.getSerializableExtra("reserveInfo");

        clothes = JSONTask.getInstance().getBascketCustomerAll(reserve.getId());

        // 예약 목록 레이아웃 설정
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reservation_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 290, this.getResources().getDisplayMetrics());
        recyclerView.setMinimumHeight(displayMetrics.heightPixels - px);
        recyclerView.setLayoutManager(layoutManager);

        LinearLayout qrButton = (LinearLayout)findViewById(R.id.reservation_qr);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationInfoActivity.this, MyQRCodeActivity.class);
                intent.putExtra("reservationID", reserve.getId());
                startActivity(intent);
            }
        });

        //전체 옷 개수 설정
        totalClothesCnt = findViewById(R.id.reservation_clothes_total_cnt);
        setTotalClothesCnt();

        // 전체 가격 설정
        mAdapter = new ReservationInfoRecyclerAdapter(this, clothes);
        recyclerView.setAdapter(mAdapter);

        reservationCost = findViewById(R.id.reservation_cost);
        setTotalCost();

        int acceptStatus = reserve.getAcceptStatus();

        if(Locale.getDefault().getLanguage()=="ko") {
            if (acceptStatus == 2) {
                LinearLayout cancelBtn = (LinearLayout) findViewById(R.id.reservation_btn_layout);
                cancelBtn.setBackgroundColor(Color.parseColor("#606060"));
                TextView cancelBtnText = (TextView) findViewById(R.id.reservation_btn_text);
                cancelBtnText.setText("대여 거절");
            } else {
                LinearLayout cancelBtn = (LinearLayout) findViewById(R.id.reservation_btn_layout);
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlert();
                    }
                });
                TextView cancelBtnText = (TextView) findViewById(R.id.reservation_btn_text);
                cancelBtnText.setText("대여 취소");
            }
        } else {
            if (acceptStatus == 2) {
                LinearLayout cancelBtn = (LinearLayout) findViewById(R.id.reservation_btn_layout);
                cancelBtn.setBackgroundColor(Color.parseColor("#606060"));
                TextView cancelBtnText = (TextView) findViewById(R.id.reservation_btn_text);
                cancelBtnText.setText("Rejected");
            } else {
                LinearLayout cancelBtn = (LinearLayout) findViewById(R.id.reservation_btn_layout);
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlert();
                    }
                });
                TextView cancelBtnText = (TextView) findViewById(R.id.reservation_btn_text);
                cancelBtnText.setText("Cancel");
            }
        }
    }

    public void setTotalClothesCnt()
    {
        int cnt = 0;
        for(BasketItem item : clothes)
            cnt += item.getCnt();
        totalClothesCnt.setText("" + cnt);
    }

    public void setTotalCost()
    {
        int price = 0;
        for(BasketItem item : clothes)
            price += item.getCnt() * item.getClothes().getPrice();
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###");
        String str;
        if(Locale.getDefault().getLanguage()=="ko")
            str = decimalFormat.format(price) + " 원";
        else
            str = decimalFormat.format(price) + " won";
        reservationCost.setText(str);
    }

    private void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(Locale.getDefault().getLanguage()=="ko") {
            builder.setMessage("대여를 취소하시겠습니까?");
            builder.setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONTask.getInstance().deleteOrder(reserve.getId());
                            finish();
                            MyReserveFragment.changeFlg = true;
                        }
                    });
            builder.setNegativeButton("아니요",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        } else {
            builder.setMessage("Are you sure you want to cancel the reservation?");
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONTask.getInstance().deleteOrder(reserve.getId());
                            finish();
                            MyReserveFragment.changeFlg = true;
                        }
                    });
            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }
        builder.show();
    }
}
