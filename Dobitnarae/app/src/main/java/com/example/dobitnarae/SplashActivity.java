package com.example.dobitnarae;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

public class SplashActivity extends Activity {
    private String token;
    private final int sleepTime = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MainActivity.class 자리에 다음에 넘어갈 액티비티를 넣어주기

        // 앱이 실행될때 토큰정보
        token = FirebaseInstanceId.getInstance().getToken();
        JSONTask.getInstance().updateFcmToken(Account.getInstance(), token);

        int priv = Account.getInstance().getPrivilege();
        Intent intent = null;

        try {
            Thread.sleep(sleepTime);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        switch (priv){
            case Constant.CLIENT:
                intent = new Intent(this, MainActivity.class);
                break;
            case Constant.ADMIN:
                intent = new Intent(this, AdminActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }
}
