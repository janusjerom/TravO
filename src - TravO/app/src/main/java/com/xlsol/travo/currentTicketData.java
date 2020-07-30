package com.xlsol.travo;

import androidx.annotation.NonNull;

import java.util.List;

public class currentTicketData {
    String busname,bustime,status;

    public currentTicketData(String busname,String bustime,String status){
        this.busname=busname;
        this.bustime=bustime;
        this.status=status;
    }

    public String getBusname() {
        return busname;
    }

    public void setBusname(String name) {
        this.busname = name;
    }
    public String getBustime() {
        return bustime;
    }

    public void setBustime(String bustime) {
        this.bustime = bustime;

    }public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "null";
    }
}
