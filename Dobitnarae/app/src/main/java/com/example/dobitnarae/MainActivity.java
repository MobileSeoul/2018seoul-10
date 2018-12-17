package com.example.dobitnarae;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.dobitnarae.RecyclerViewAdapter.ClothesRecommendationListRecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private Account account;
    private ClothesRecommendationListRecyclerAdapter cAdapter;
    private ArrayList<Clothes> clothes;
    private final int RANDOM_CLOTHES_CNT = 6;

    // 날씨
    private final String[] skyStatus = {"맑음", "구름조금", "구름많음", "흐림"};
    private final String[] precipitationType = {"", "비", "비/눈", "눈"};

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        LinearLayout myPageBtn = (LinearLayout)findViewById(R.id.myPage);
        myPageBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });

        View.OnClickListener gotoStoreList = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StoreListActivity.class);
                int sector = Integer.parseInt((String) v.getTag());
                intent.putExtra("sector", sector);
                startActivity(intent);
            }
        };

        LinearLayout basket = findViewById(R.id.main_basket);
        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BasketActivity.class);
                startActivity(intent);
            }
        });

        CoordinatorLayout sector1 = (CoordinatorLayout) findViewById(R.id.store_sector_1);
        sector1.setOnClickListener(gotoStoreList);

        ImageView seochonImgV = findViewById(R.id.sector_seochon);
        setImageViewGlide(seochonImgV, R.drawable.seochon);

        CoordinatorLayout sector2 = (CoordinatorLayout)findViewById(R.id.store_sector_2);
        sector2.setOnClickListener(gotoStoreList);

        ImageView bukchonImgV = findViewById(R.id.sector_bukchon);
        setImageViewGlide(bukchonImgV, R.drawable.bukchon);

        // 옷 추천 리스트
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.clothes_recommendation_recycler_view);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setNestedScrollingEnabled(false);

        // 랜덤으로 옷 추출
        clothes = JSONTask.getInstance().getRandomClothesAll(RANDOM_CLOTHES_CNT);
        cAdapter = new ClothesRecommendationListRecyclerAdapter(this, clothes);
        recyclerView.setAdapter(cAdapter);

        // 고궁 알아보기
        setPalaceRedirection();

        LinearLayout hanbokRule = findViewById(R.id.hanbok_guideline);
        hanbokRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://cgg.cha.go.kr/agapp/public/html/HtmlPage.do?pg=/cgg/02/information_05.jsp&pageNo=78010200&siteCd=CGG";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        // 날씨 설정
        try {
            TextView temperature = (TextView) findViewById(R.id.weather_temperature);
            TextView weatherStatusMsg = (TextView)findViewById(R.id.weather_status_text);
            ImageView weatherImg = (ImageView)findViewById(R.id.weather_status_img);
            TextView weatherContext = (TextView)findViewById(R.id.weather_context);

            JSONObject weatherInfo = new WeatherTask().execute().get();

            temperature.setText(weatherInfo.getString("기온"));
            int sky = weatherInfo.getInt("하늘상태");
            int pty = weatherInfo.getInt("강수상태");

            if(pty == 0)
                weatherStatusMsg.setText(skyStatus[sky]);
            else
                weatherStatusMsg.setText(precipitationType[pty]);

            // 추천 문구
            weatherContext.setText(getWeatherMessage(sky, pty));

            // 날씨 이미지
            Glide.with(getApplicationContext()).load(getWeatherImg(sky, pty))
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .fitCenter())
                    .into(weatherImg);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout logout = (LinearLayout)findViewById(R.id.footer_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.setLogOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout openSourceInfo = (LinearLayout)findViewById(R.id.footer_opensource);
        openSourceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OpenSourceInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
            finish();
        }
        else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setPalaceRedirection(){
        // 경복궁
        ImageView gyeongbok = (ImageView)findViewById(R.id.gyeongbokgung);
        gyeongbok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.royalpalace.go.kr/"));
                startActivity(intent);
            }
        });

        // 창경궁
        ImageView changgyeong = (ImageView)findViewById(R.id.changgyeonggung);
        changgyeong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cgg.cha.go.kr/"));
                startActivity(intent);
            }
        });

        // 창덕궁
        ImageView changdeok = (ImageView)findViewById(R.id.changdeokgung);
        changdeok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cdg.go.kr/"));
                startActivity(intent);
            }
        });

        // 종묘
        ImageView jongmyo = (ImageView)findViewById(R.id.jongmyo);
        jongmyo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://jm.cha.go.kr/"));
                startActivity(intent);
            }
        });

        // 덕수궁
        ImageView deoksu = (ImageView)findViewById(R.id.deoksugung);
        deoksu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.deoksugung.go.kr/"));
                startActivity(intent);
            }
        });
    }

    private int getWeatherImg(int skyStatus, int precipitationType){
        if(precipitationType == 0){ // 비 or 눈 안올때
            switch (skyStatus){
                case 0: // 맑음
                    return R.drawable.sunny_day_weather_symbol;
                case 1: // 구름 조금
                    return R.drawable.cloudy_day;
                default:    // 구름많음, 흐림
                    return R.drawable.cloud_outline;
            }
        }
        else {  // 비 or 눈 올때
            if(skyStatus == 0){ // 맑음
                switch (precipitationType){
                    case 1: //비
                        return R.drawable.rainy_day;
                    case 2: //눈비
                        return R.drawable.hail;
                    case 3: //눈
                        return R.drawable.snowy_weather_symbol;
                }
            }
            else{
                switch (precipitationType){
                    case 1: //비
                        return R.drawable.cloud_with_rain_drops;
                    case 2: //눈비
                        return R.drawable.hail_crystals_falling_of_a_cloud;
                    case 3: //눈
                        return R.drawable.snow_weather_symbol;
                }
            }
        }
        return R.drawable.sunny_day_weather_symbol;
    }

    private String getWeatherMessage(int skyStatus, int precipitationType){
        if(precipitationType == 0){
            if(skyStatus == 0 || skyStatus == 1) { // 맑음, 구름 조금
                return "화창한 오늘, 한복입고 고궁을 거닐어 보세요";
            }
            else{   // 구름 많음, 흐림
                return "당신의 나래가 되어줄 한복 구경 어떠세요?";
            }
        }
        else if(precipitationType == 3){ // 눈
            return "다채로운 한복으로\n 하얀 고궁을 채워보는건 어떨까요?";
        }
        else { // 비, 눈비
            return "눈부신 날을 위해\n 아름다운 한복을 구경해보세요";
        }
    }

    private void setImageViewGlide(ImageView imageView, int id){
        Glide.with(getApplicationContext()).load(id)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter())
                .into(imageView);
    }
}