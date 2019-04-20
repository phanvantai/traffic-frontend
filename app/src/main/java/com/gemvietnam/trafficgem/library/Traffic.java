package com.gemvietnam.trafficgem.library;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

public class Traffic {
    private Location mLocation;
    private String mTimeStamp;
    private String mDate;
    private String mVehicle;
    private double mSpeed;
    private String mDirection;

    public Traffic(Location location, String timeStamp, String date, String transport, double speed, String direction) {
        mLocation = location;
        mTimeStamp = timeStamp;
        mDate = date;
        mVehicle = transport;
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

    public void setTimeStamp(String mTimeStamp){
        this.mTimeStamp = mTimeStamp;
    }

    public String getTimeStamp(){
        return mTimeStamp;
    }

    public void setDate(String date) { this.mDate = date;}

    public String getDate(){ return mDate; }

    public void setTransport(String mTransport){
        this.mVehicle = mTransport;
    }

    public String getTransport(){
        return mVehicle;
    }

    public void setSpeed(double mSpeed){
        this.mSpeed = mSpeed;
    }

    public double getSpeed(){
        return mSpeed;
    }

    public void setDirection(String direction){ mDirection = direction; }

    public String getDirection(){ return mDirection; }

    public JSONObject exportJson(){
        JSONObject entry = new JSONObject();
        try {
            entry.put("latitude", mLocation.getLatitude());
            entry.put("longitude", mLocation.getLongitude());
            entry.put("avg_speed", mSpeed);
            entry.put("vehicle", mVehicle);
            entry.put("record_time", mTimeStamp);
            entry.put("direction", mDirection);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entry;
    }
}
