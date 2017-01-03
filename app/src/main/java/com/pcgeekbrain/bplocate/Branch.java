package com.pcgeekbrain.bplocate;

import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Mendel on 12/12/2016.
 * Branch Holding Class
 */

public class Branch implements Serializable{
    private static final String TAG = "Branch";
    private String name = "", address = "", number= "", closes_in = "";
    private String[] hours = new String[10];

    public Branch(){
        this.name = "";
        this.address = "";
        this.number = "";
        this.closes_in = "";
    }

    public String getClosesIn(){
        return closes_in;
    }
    public String getHours(int day){
        switch (day){
            case Calendar.MONDAY:
                return hours[0];
            case Calendar.TUESDAY:
                return hours[1];
            case Calendar.WEDNESDAY:
                return hours[2];
            case Calendar.THURSDAY:
                return hours[3];
            case Calendar.FRIDAY:
                return hours[4];
            case Calendar.SATURDAY:
                return hours[5];
            case Calendar.SUNDAY:
                return hours[6];
            default:
                return "N/A";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setHours(String[] hours){
        this.hours = hours;
    }
}
