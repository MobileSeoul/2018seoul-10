package com.example.dobitnarae;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dobitnarae.fcm.MyFirebaseInstanceIDService;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private boolean saveLoginData;
    private String id;
    private String pwd;

    private EditText IDTxt;
    private EditText PasswordTxt;
    private LinearLayout LoginBtn, signUp;
    private CheckBox checkBox;
    private ArrayList<Account> accountList = new ArrayList<>();
    public static SharedPreferences appData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //설정값 불러오기
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        IDTxt = (EditText) findViewById(R.id.login_id);
        PasswordTxt = (EditText) findViewById(R.id.login_password);
        LoginBtn = (LinearLayout) findViewById(R.id.login_btn);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        signUp = (LinearLayout)findViewById(R.id.login_signup);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        //로그인정보가 있을시
        if (saveLoginData) {
            IDTxt.setText(id);
            PasswordTxt.setText(pwd);
            checkBox.setChecked(saveLoginData);

            String ID = IDTxt.getText().toString();
            String Password = PasswordTxt.getText().toString();
            JSONTask.getInstance().getLoginResult(ID, Password);
            save();

            // 계정 설정
            Account.getInstance().setAccount(JSONTask.getInstance().getAccountAll(id).get(0));

            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ID = IDTxt.getText().toString();
                String Password = PasswordTxt.getText().toString();
                if(ID.length() * Password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }

                else if(!Pattern.matches("^[a-zA-Z0-9]*$", ID) || !Pattern.matches("^[a-zA-Z0-9]*$", Password))
                {
                    Toast.makeText(getApplicationContext(),"올바른 내용을 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                accountList = JSONTask.getInstance().getAccountAll(ID);//ID존재 확인

                if (accountList.size() != 0) {


                    if (JSONTask.getInstance().getLoginResult(ID, Password) == 1) {
                        checkBox.setChecked(true);
                        save();

                        // 계정 설정
                        Account.getInstance().setAccount(accountList.get(0));

                        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                        finish();
                        startActivity(intent);
                        overridePendingTransition(0, 0);

                    } else {
                        Toast.makeText(getApplicationContext(), "비밀번호를 다시 입력하세요", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
        private void save() {
            // SharedPreferences 객체만으론 저장 불가능 Editor 사용
            SharedPreferences.Editor editor = appData.edit();

            // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
            // 저장시킬 이름이 이미 존재하면 덮어씌움
            editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
            editor.putString("ID", IDTxt.getText().toString().trim());
            editor.putString("PWD", PasswordTxt.getText().toString().trim());

            // apply, commit 을 안하면 변경된 내용이 저장되지 않음
            editor.apply();
        }

        // 설정값을 불러오는 함수
        private void load() {
            // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
            // 저장된 이름이 존재하지 않을 시 기본값
            saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
            id = appData.getString("ID", "");
            pwd = appData.getString("PWD", "");

         }

         public static void setLogOut(){
            SharedPreferences.Editor editor = appData.edit();
            editor.clear();
            editor.commit();
         }
}
