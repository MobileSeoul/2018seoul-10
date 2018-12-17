package com.example.dobitnarae;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.dobitnarae.QRCode.CreateQRCode;
import com.google.zxing.qrcode.encoder.QRCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MyQRCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //뒤로가기
        ImageButton backButton = (ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        Intent intent = getIntent();
        int reserveID = intent.getIntExtra("reservationID", -1);

        // error
        if(reserveID == -1){
            finish();
            return;
        }

        CreateQRCode createQRCode = new CreateQRCode();
        Bitmap bitmap = createQRCode.createQRCode(reserveID);
        loadQRCode(bitmap);
    }

    public void loadQRCode(Bitmap bitmap){
        ImageView imageView = findViewById(R.id.qrcode);
        Glide.with(this).load(bitmap)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter())
                .into(imageView);
    }
}
