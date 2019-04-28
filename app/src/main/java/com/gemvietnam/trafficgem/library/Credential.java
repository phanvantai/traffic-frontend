package com.gemvietnam.trafficgem.library;

import org.json.JSONException;
import org.json.JSONObject;

import static com.gemvietnam.trafficgem.utils.Constants.EMAIL;
import static com.gemvietnam.trafficgem.utils.Constants.PASSWORD;
import static com.gemvietnam.trafficgem.utils.Constants.TIME;

public class Credential {
    private String Email;
    private String Password;
    private String Time;

    public Credential(){}

    public Credential(String userName, String password, String time){
        Email = userName;
        Password = password;
        Time = time;
    }
    public void setUserName(String userName){ Email = userName;}

    public String getUserName() { return Email; }

    public void setPassword(String password){ Password = password;}

    public String getPassword(){ return Password;}

    public void setTime(String time){ Time = time;}

    public String getTime(){ return Time;}

    public String exportStringFormatJson(){
        JSONObject entry = new JSONObject();
        try {
            entry.put(EMAIL, Email);
            entry.put(PASSWORD, Password);
            entry.put(TIME, Time);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return entry.toString();
    }
}
