package com.gemvietnam.trafficgem.library;

import android.location.Location;

import java.util.Date;

public class Message {
    private int IDMsg;
    private Location location;
    private String picture;
    private Date date;

    public Message(int _IDMsg, Location _location, String _picture, Date _date){
        IDMsg = _IDMsg;
        location = _location;
        picture = _picture;
        date = _date;
    }

    public void setIDMsg(int _IDMsg){ IDMsg = _IDMsg;}

    public int getIDMsg(){ return IDMsg;}

    public void setLocation(Location _location){ location = _location;}

    public Location getLocation(){ return location;}

    public void setPicture(String _picture){ picture = _picture;}

    public String getPathPicture(){ return picture;}

    public void setDate(Date _date){ date = _date;}

    public Date getDate(){ return date;}

}
