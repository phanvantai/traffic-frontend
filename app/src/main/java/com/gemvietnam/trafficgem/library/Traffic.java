package com.gemvietnam.trafficgem.library;

import android.location.Location;

import java.util.Date;

public class Traffic {
    private Location latLon;
    private String timeStamp;
    private String date;
    private String transport;
    private double speed;
    public void setLocation(Location location){
        latLon = location;
    }

    public Location getLocation(){
        return latLon;
    }

    public double getLat(){ return latLon.getLatitude(); }

    public double getLon(){ return latLon.getLongitude(); }

    public void setTimeStamp(String timeStamp){
        this.timeStamp = timeStamp;
    }

    public String getTimeStamp(){
        return timeStamp;
    }

    public void setDate(String date) { this.date = date;}

    public String getDate(){ return date; }

    public void setTransport(String transport){
        this.transport = transport;
    }

    public String getTransport(){
        return transport;
    }

    public void setSpeed(double speed){
        this.speed = speed;
    }

    public double getSpeed(){
        return  speed;
    }
}
