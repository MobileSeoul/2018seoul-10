package com.example.dobitnarae;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.dobitnarae.QRCode.CustomQRActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.ArrayList;

public class QRScanActivity extends CaptureActivity {
    /* QR code scanner 객체 */
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* QR code Scanner Setting */
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.setCaptureActivity(CustomQRActivity.class);
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Log.v("qrcode", "no contents");
            } else { //QR코드, 내용 존재
                try {
                    /* QR 코드 내용*/
                    int reserveID = Integer.parseInt(result.getContents());

                    Order reserved = JSONTask.getInstance().getOrderAllByRID(reserveID).get(0);
                    ArrayList<BasketItem> clothes = JSONTask.getInstance().getBascketCustomerAll(reserveID);

                    Store store = JSONTask.getInstance().getAdminStoreAll(reserved.getAdmin_id()).get(0);

                    reserved.setBasket(clothes);

                    Intent intent = new Intent(this, OrderSpecificActivity.class);
                    intent.putExtra("order", reserved);
                    intent.putExtra("store", store);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        finishAffinity();
    }
}
