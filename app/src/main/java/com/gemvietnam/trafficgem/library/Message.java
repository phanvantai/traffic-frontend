package com.gemvietnam.trafficgem.library;

import android.location.Location;

import com.gemvietnam.trafficgem.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private int IDMsg;
    private Location location;
    private String picture;
    private String date;

    public Message(){}

    public Message(int _IDMsg, Location _location, String _picture, String _date){
        IDMsg = _IDMsg;
        location = _location;
        picture = _picture;
        date = _date;
    }

    public void setIDMsg(int _IDMsg){ IDMsg = _IDMsg;}

    public int getIDMsg(){ return IDMsg;}

    public void setLatitude(double lat){ this.location.setLatitude(lat);}

    public double getLatitude(){ return location.getLatitude();}

    public void setLongitude(double lng){ this.location.setLongitude(lng);}

    public double getLongitude(){ return location.getLongitude(); }

    public void setPicture(String _picture){ picture = _picture;}

    public String getPathPicture(){ return picture;}

    public void setDate(String _date){ date = _date;}

    public String getDate(){ return date;}

    public String exportStringFormatJson(){
        JSONObject entry = new JSONObject();
        try {
            entry.put("IDMsg", IDMsg);
            entry.put("lat", location.getLatitude());
            entry.put("lng", location.getLongitude());
            entry.put("date", Constants.RECORD_TIME_FORMAT.format(date));
            entry.put("picture", picture);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return entry.toString();
    }

}