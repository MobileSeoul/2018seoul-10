package com.example.dobitnarae;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText idTxt;
    private EditText passwordConfirmTxt;
    private EditText passwordTxt;
    private EditText nameTxt;
    private EditText hpTxt;
    private CheckBox privBox;
    private LinearLayout registerBtn;
    private int flag = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        registerBtn = (LinearLayout) findViewById(R.id.login_btn);
        idTxt = (EditText) findViewById(R.id.register_id);
        passwordTxt = (EditText) findViewById(R.id.register_password);
        passwordConfirmTxt =  (EditText) findViewById(R.id.register_password_confirm);
        nameTxt = (EditText) findViewById(R.id.register_name);
        hpTxt = (EditText) findViewById(R.id.register_phone);
        privBox = (CheckBox) findViewById(R.id.register_check);

/*
        idTxt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if(keyCode == event.KEYCODE_ENTER)
                {
                    return true;
                }
                return false;
            }
        });
*/





        registerBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                if (idTxt.getText().toString().length() * passwordTxt.getText().toString().length() * nameTxt.getText().toString().length() * hpTxt.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "공란이 없어야 합니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                //핸드폰번호 유효성
                else if(!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",hpTxt.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"올바른 핸드폰 번호가 아닙니다. 010-0000-000",Toast.LENGTH_SHORT).show();
                    return;
                }

                //비밀번호 유효성
                else if(!Pattern.matches("^[a-zA-Z0-9].{7,20}$", passwordTxt.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"비밀번호 형식을 지켜주세요.",Toast.LENGTH_SHORT).show();

                    if(!(passwordTxt.getText().toString().equals(passwordConfirmTxt.getText().toString()))) {
                        Toast.makeText(getApplicationContext(), "비밀번호 형식을 지켜주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    return;
                }
                else if(!Pattern.matches("^[a-zA-Z0-9]*$", idTxt.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"아이디는 영문만 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }



                else{
                    ArrayList<Account> tmp = JSONTask.getInstance().getAccountAll(idTxt.getText().toString());
                    if(tmp.size() == 0) {

                        if (privBox.isChecked())
                            flag = 0;

                        Toast.makeText(getApplicationContext(), nameTxt.getText().toString() + "님 회원가입에 성공하였습니다. 로그인해주세요", Toast.LENGTH_LONG).show();
                        Account account = new Account(idTxt.getText().toString(), passwordTxt.getText().toString(), nameTxt.getText().toString(), hpTxt.getText().toString(), flag);
                        JSONTask.getInstance().insertAccount(account);
                        if(flag==0) {
                            Store store = new Store(account.getId());
                            JSONTask.getInstance().insertStore(store);
                        }
                        LoginActivity.setLogOut();
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "아이디가 중복되었습니다", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
}
