package com.gemvietnam.trafficgem.library;

import org.json.JSONException;
import org.json.JSONObject;

public class Credential {
    private String Email;
    private String Password;

    public Credential(){}

    public Credential(String userName, String password){
        Email = userName;
        Password = password;
    }
    public void setUserName(String userName){ Email = userName;}

    public String getUserName() { return Email; }

    public void setPassword(String password){ Password = password;}

    public String getPassword(){ return Password;}

    public String exportStringFormatJson(){
        JSONObject entry = new JSONObject();
        try {
            entry.put("email", Email);
            entry.put("password", Password);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return entry.toString();
    }
}
