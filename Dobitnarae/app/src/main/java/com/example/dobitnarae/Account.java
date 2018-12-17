package com.example.dobitnarae;

import java.io.Serializable;

public class Account {
    private String id;
    private String name;
    private String pw;  // 삭제
    private String phone;
    private int privilege;

    private Account() {
    }

    public Account(String id,String pw, String name, String tel, int privilege) {
        this.id = id;
        this.name = name;
        this.pw = pw;
        this.phone = tel;
        this.privilege = privilege;
    }

    public static Account getInstance(){
        return Singleton.instance;
    }

    private static class Singleton{
        private static final Account instance = new Account();
    }

    public void setAccount(Account account){
        this.id = account.id;
        this.pw = account.pw;
        this.name = account.name;
        this.phone = account.phone;
        this.privilege = account.privilege;
    }

    public String getName() {
        return name;
    }

    public String getPw() {
        return pw;
    }

    public int getPrivilege() {
        return privilege;
    }

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPw(String pw){
        this.pw = pw;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
