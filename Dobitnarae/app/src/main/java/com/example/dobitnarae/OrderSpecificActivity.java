package com.example.dobitnarae;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class OrderSpecificActivity extends AppCompatActivity {
    private Order item;
    private LinearLayout layout, layout1, btnRegister, btnReject, btnDelete;
    private ArrayList<BasketItem> basket;

    private Intent intent;
    private Store store;

    private OrderSpecificRecyclerAdapter mAdapter;
    private TextView totalClothesCnt, reservationCost;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_order);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //뒤로가기
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        context = this;

        intent = getIntent();
        item = (Order) intent.getSerializableExtra("order");
        store = (Store) intent.getSerializableExtra("store");
        basket = JSONTask.getInstance().getBascketCustomerAll(item.getId());

        ((TextView) findViewById(R.id.toolbar_title)).setText(store.getName());

        layout = (LinearLayout) findViewById(R.id.layout_confirmornot);
        layout1 = (LinearLayout) findViewById(R.id.layout_delete);

        // 미승인 목록
        if (item.getAcceptStatus() == 0) {
            // 승인 버튼 클릭 시
            btnRegister = (LinearLayout) findViewById(R.id.order_clothes_register);
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), item.getUser_id() + "님의 주문이 승인되었습니다.", Toast.LENGTH_SHORT).show();
                    item.setAcceptStatus(1); // 승인
                    JSONTask.getInstance().updateOrderAccept(item.getId(), 1);
                    btnRegister.setEnabled(false);
                    btnReject.setEnabled(false);
                    btnRegister.setBackgroundResource(R.color.darkergrey);
                    btnReject.setBackgroundResource(R.color.darkergrey);
                    OrderFragmentManagementFragment.changeFlg = true;
                    JSONTask.getInstance().sendMsgByFCM(item.getUser_id(), store.getName(), item.getUser_id() + "님의 주문이 승인되었습니다.");
                }
            });

            // 거절 버튼 클릭 시
            btnReject = (LinearLayout) findViewById(R.id.order_clothes_reject);
            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), item.getUser_id() + "님의 주문이 거절되었습니다.", Toast.LENGTH_SHORT).show();
                    item.setAcceptStatus(2); // 거절
                    JSONTask.getInstance().updateOrderAccept(item.getId(), 2);
                    btnRegister.setEnabled(false);
                    btnReject.setEnabled(false);
                    btnRegister.setBackgroundResource(R.color.darkergrey);
                    btnReject.setBackgroundResource(R.color.darkergrey);
                    OrderFragmentManagementFragment.changeFlg = true;

                    // 거절시 물품개수 반환
                    for (BasketItem item : basket) {
                        Clothes temp = item.getClothes();
                        int tmpCnt = item.getClothes().getCount();
                        temp.setCount(tmpCnt + item.getCnt());
                        JSONTask.getInstance().updateCloth(temp);
                    }
                    // 푸시알림
                    JSONTask.getInstance().sendMsgByFCM(item.getUser_id(), store.getName(), item.getUser_id() + "님의 주문이 거절되었습니다.");
                }
            });
        } else {
            layout.setVisibility(View.GONE);
            layout1.setVisibility(View.VISIBLE);

            btnDelete = (LinearLayout) findViewById(R.id.order_delete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlert(context, item);
                }
            });
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reservation_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new OrderSpecificRecyclerAdapter(this, basket);
        recyclerView.setAdapter(mAdapter);

        if (basket.size() == 0) {
            Toast.makeText(getApplicationContext(), "장바구니가 비었습니다.", Toast.LENGTH_SHORT).show();
        }

        reservationCost = findViewById(R.id.reservation_cost);
        setTotalCost();

        totalClothesCnt = findViewById(R.id.reservation_clothes_total_cnt);
        setTotalClothesCnt();


        final Drawable successLayoutDrawable;
        int successStatus = item.getAcceptStatus();
        LinearLayout statusLayout = (LinearLayout)findViewById(R.id.order_accept_layout);
        TextView statusText = (TextView)findViewById(R.id.order_accept_txt);

        Drawable acceptFlg = context.getResources().getDrawable(R.drawable.border_all_layout_item_green);
        Drawable pendingFlg = context.getResources().getDrawable(R.drawable.border_all_layout_item_gray);
        Drawable rejectFlg = context.getResources().getDrawable(R.drawable.border_all_layout_item_red);

        if (successStatus == 0) {
            statusText.setText("대기");
            statusText.setTextColor(Color.parseColor("#8f8f8f"));
            successLayoutDrawable = pendingFlg;
        } else if (successStatus == 1) {
            statusText.setText("승인");
            statusText.setTextColor(Color.parseColor("#339738"));
            successLayoutDrawable = acceptFlg;
        } else {
            statusText.setText("거절");
            statusText.setTextColor(Color.parseColor("#f94c4c"));
            successLayoutDrawable = rejectFlg;
        }

        statusLayout.setBackground(successLayoutDrawable);

        // 대여자 정보
        final Account account = JSONTask.getInstance().getAccountAll(item.getUser_id()).get(0);
        TextView id = findViewById(R.id.reserve_client_id);
        TextView name = findViewById(R.id.reserve_client_name);
        TextView phone = findViewById(R.id.reserve_client_phone);
        TextView rent = findViewById(R.id.reserve_client_rentalDate);
        id.setText(account.getId());
        name.setText(account.getName());
        phone.setText(account.getPhone());
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = "tel:" + account.getPhone();
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
            }
        });
        String[] date = item.getRentalDate().split(":");
        rent.setText(String.format("%s:%s", date[0], date[1]));
    }
    public void setTotalClothesCnt()
    {
        int cnt = 0;
        for(BasketItem item : basket)
            cnt += item.getCnt();
        totalClothesCnt.setText("" + cnt);
    }

    public void setTotalCost()
    {
        int price = 0;
        for(BasketItem item : basket)
            price += item.getCnt() * item.getClothes().getPrice();
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###");
        String str = decimalFormat.format(price) + " 원";
        reservationCost.setText(str);
    }

    private void showAlert(final Context context, final Order order){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String name = JSONTask.getInstance().getAccountAll(order.getUser_id()).get(0).getName();
        builder.setMessage(name+ "님의 주문정보를 삭제하시겠습니까?\n주문내역에서 사라집니다.");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONTask.getInstance().deleteOrder(order.getId());
                        Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        OrderFragmentManagementFragment2.changeFlg = true;
                        finish();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }
}
