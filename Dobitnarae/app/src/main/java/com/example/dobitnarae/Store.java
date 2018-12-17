package com.example.dobitnarae;

import java.io.Serializable;
import java.sql.Time;

public class Store implements Serializable{
    private int id;
    private String name;
    private String admin_id;
    private String address;
    private String tel;
    private String intro;
    private String inform;
    private String startTime;
    private String endTime;
    private int sector;
    private double longitude;
    private double latitude;
    private String TransIntro;
    private String TransAddress;// 번역을위한필드

    public Store(String admin_id){
        this.id=id;
        this.name = "";
        this.admin_id= admin_id;
        this.address = "";
        this.tel = "";
        this.intro = "";
        this.inform = "";
        this.sector = 1;
        this.longitude = 37.579617;
        this.latitude = 126.9748523;
        this.startTime = "09:00:00";
        this.endTime = "20:00:00";
        this.TransIntro = "No Inform";
        this.TransAddress = "No Inform";
    }


    public Store(int id, String name, String admin_id, String tel, String intro, String inform,
                 String address, int sector, double latitude, double longitude, String startTime, String endTime){
        this.id=id;
        this.name = name;
        this.admin_id= admin_id;
        this.address = address;
        this.tel = tel;
        this.intro = intro;
        this.inform = inform;
        this.sector = sector;
        this.longitude = longitude;
        this.latitude = latitude;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getAdmin_id() {
        return admin_id;
    }
    public String getAddress() {
        return address;
    }
    public String getTel(){
        return tel;
    }
    public String getIntro() {
        return intro;
    }
    public String getInform() {
        return inform;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getStartTime() {
        return startTime;
    }
    public int getSector(){
        return sector;
    }
    public double getLongitude(){
        return longitude;
    }
    public double getLatitude(){
        return latitude;
    }
    public String getTransIntro() {
        return TransIntro;
    }
    public String getTransAddress() {
        return TransAddress;
    }


    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setTel(String tel){
        this.tel = tel;
    }
    public void setIntro(String intro) {
        this.intro = intro;
    }
    public void setInform(String inform) {
        this.inform = inform;
    }
    public void setSector(int sector){
        this.sector = sector;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public void setTransIntro(String TransIntro){this.TransIntro = TransIntro;}
    public void setTransAddress(String TransAddress){this.TransAddress = TransAddress;}

}