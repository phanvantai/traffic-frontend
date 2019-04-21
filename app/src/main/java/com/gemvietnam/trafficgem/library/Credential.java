package com.gemvietnam.trafficgem.library;

import org.json.JSONException;
import org.json.JSONObject;

import static com.gemvietnam.trafficgem.utils.Constants.EMAIL;
import static com.gemvietnam.trafficgem.utils.Constants.PASSWORD;

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
            entry.put(EMAIL, Email);
            entry.put(PASSWORD, Password);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return entry.toString();
    }
}
