package com.example.dobitnarae.QRCode;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dobitnarae.R;
import com.journeyapps.barcodescanner.CaptureActivity;

public class CustomQRActivity extends CaptureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView title_view = new TextView(this);
        title_view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
        title_view.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        title_view.setPadding(150, 100, 100, 100);
        title_view.setTextColor(Color.parseColor("#FFFFFF"));

        this.addContentView(title_view, layoutParams);
    }
}
