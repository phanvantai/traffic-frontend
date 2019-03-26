package com.gemvietnam.trafficgem.library;

import android.location.Location;

public class Traffic {
    private Location mLocation;
    private String mTimeStamp;
    private String mDate;
    private String mTransport;
    private double mSpeed;

    public Traffic(Location location, String timeStamp, String date, String transport, double speed) {
        mLocation = location;
        mTimeStamp = timeStamp;
        mDate = date;
        mTransport = transport;
        mSpeed = speed;
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
        this.mTransport = mTransport;
    }

    public String getTransport(){
        return mTransport;
    }

    public void setSpeed(double mSpeed){
        this.mSpeed = mSpeed;
    }

    public double getSpeed(){
        return mSpeed;
    }
}
