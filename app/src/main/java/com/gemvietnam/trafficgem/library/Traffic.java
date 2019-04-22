package com.gemvietnam.trafficgem.library;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Traffic {
    private Location mLocation;
    private double mSpeed;
    private String mVehicle;
    private String mRecord_time;
    private String mDirection;

    public Traffic(Location location, String record_time, String vehicle, double speed, String direction) {
        mLocation = location;
        mRecord_time = record_time;
        mVehicle = vehicle;
        mSpeed = speed;
        mDirection = direction;
    }
    public void setLocation(Location location){
        mLocation = location;
    }

    public Location getLocation(){
        return mLocation;
    }

    public double getLat(){ return mLocation.getLatitude(); }

    public double getLon(){ return mLocation.getLongitude(); }

    public void setRecordTime(String time){ mRecord_time = time;}

    public String getRecordTime(){ return mRecord_time;}

    public void setVehicle(String mVehicle){
        this.mVehicle = mVehicle;
    }

    public String getVehicle(){
        return mVehicle;
    }

    public void setAvgSpeed(double mSpeed){
        this.mSpeed = mSpeed;
    }

    public double getAvgSpeed(){
        return mSpeed;
    }

    public void setDirection(String direction){ mDirection = direction; }

    public String getDirection(){ return mDirection; }

//    public String exportStringFormatJson(){
//        JSONObject entry = new JSONObject();
//        try {
//            entry.put("lat", mLocation.getLatitude());
//            entry.put("lng", mLocation.getLongitude());
//            entry.put("avg_speed", mSpeed);
//            entry.put("vehicle", mVehicle);
//            entry.put("record_time", mRecord_time);
//            entry.put("direction", mDirection);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return entry.toString();
//    }
    public JSONObject exportJson(){
        JSONObject entry = new JSONObject();
        try {
            entry.put("lat", mLocation.getLatitude());
            entry.put("lng", mLocation.getLongitude());
            entry.put("avg_speed", mSpeed);
            entry.put("vehicle", mVehicle);
            entry.put("record_time", mRecord_time);
            entry.put("direction", mDirection);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entry;
    }

    public String exportStringFormatJson(){
        return exportJson().toString();
    }
}