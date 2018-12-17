package com.example.dobitnarae;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Reserve implements Serializable{
    private int id;
    private String user_id;
    private String admin_id;
    private int acceptStatus;
    private String rentalDate;  // 형태  yyyy-mm-dd hh:mm:ss

    public Reserve(int id, String user_id, String admin_id, int acceptStatus, String rentalDate){
        this.id = id;
        this.user_id = user_id;
        this.admin_id = admin_id;
        this.acceptStatus = acceptStatus;
        this.rentalDate = rentalDate;
    }

    public int getId() {
        return id;
    }

    public int getAcceptStatus() {
        return acceptStatus;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(String rentalDate) {
        this.rentalDate = rentalDate;
    }

    public void setAcceptStatus(int acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void addClothes(BasketItem item){
    }

    public int compareTo(Reserve reserve){
        if(this.id > reserve.id)
            return 1;
        else if(this.id == reserve.id)
            return 0;
        return -1;
    }
}
