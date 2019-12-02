package com.example.firebasetest01;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UserData {
    private String userEmail;
    private String name;
    private String phoneNum;
    private String carNum;
    private String carType;

    private String motion;
    private String temperature;
    private String gps;
    private String lat;         //위도
    private String lon;         //경도

    public UserData() {

    }

    public UserData(String email, String name, String phoneNum, String carNum, String carType) {
        this.userEmail = email;
        this.name = name;
        this.phoneNum = phoneNum;
        this.carNum = carNum;
        this.carType = carType;
        this.motion = "Not Connected";
        this.temperature = "Not Connected";
        this.gps = "";
        this.lat = "";
        this.lon = "";
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public void setMotion(String motion) {
        this.motion = motion;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getCarNum() {
        return carNum;
    }

    public String getCarType() {
        return carType;
    }

    public String getMotion() {
        return motion;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getGps() {
        return gps;
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
