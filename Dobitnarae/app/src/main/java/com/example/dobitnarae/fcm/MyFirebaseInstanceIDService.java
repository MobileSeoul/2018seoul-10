package com.example.dobitnarae.fcm;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.dobitnarae.Account;
import com.example.dobitnarae.JSONTask;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    private Account account;

    @Override
    public void onTokenRefresh() {
       // super.onTokenRefresh();
        // 푸시알림을 보낼때 토큰이 필요
        // 앱 설치, 삭제 또는 유효기간 만료 등의 다양한 이유로 토큰은 계속해서 바뀐다.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    }
}
