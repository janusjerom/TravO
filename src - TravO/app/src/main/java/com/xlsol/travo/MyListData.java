package com.xlsol.travo;
import java.util.List;

public class MyListData{
    private String uid;
    private String time;
    private String busName;
    private String seats;
    public MyListData(String uid, String time,String busName,String seats) {
        this.uid = uid;
        this.time = time;
        this.busName = busName;
        this.seats = seats;

    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getBusName() {
        return busName;
    }
    public void setBusName(String busName) {
        this.busName = busName;
    }
    public String getSeats() {
        return seats;
    }
    public void setSeats(String seats) {
        this.seats = seats;
    }

}