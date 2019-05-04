package com.gemvietnam.trafficgem.library;

import android.location.Location;

import com.gemvietnam.trafficgem.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class Report {
    private int IDMsg;
    private Location location;
    //    private String picture;
    private String time_stamp;

    public Report(){}

    public Report(int _IDMsg, String _time_stamp, Location _location){
        IDMsg = _IDMsg;
        location = _location;
//        picture = _picture;
        time_stamp = _time_stamp;
    }

    public void setIDMsg(int _IDMsg){ IDMsg = _IDMsg;}

    public int getIDMsg(){ return IDMsg;}

    public void setLatitude(double lat){ this.location.setLatitude(lat);}

    public double getLatitude(){ return location.getLatitude();}

    public void setLongitude(double lng){ this.location.setLongitude(lng);}

    public double getLongitude(){ return location.getLongitude(); }

//    public void setPicture(String _picture){ picture = _picture;}
//
//    public String getPathPicture(){ return picture;}

    public void setDate(String _time_stamp){ time_stamp = _time_stamp;}

    public String getDate(){ return time_stamp;}

    public String exportStringFormatJson(){
        JSONObject entry = new JSONObject();
        try {
            entry.put(Constants.IDMsg, IDMsg);
            entry.put(Constants.Time_Stamp, time_stamp);
            entry.put(Constants.Latitude, location.getLatitude());
            entry.put(Constants.Longitude, location.getLongitude());
//            entry.put("picture", picture);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return entry.toString();
    }
}
