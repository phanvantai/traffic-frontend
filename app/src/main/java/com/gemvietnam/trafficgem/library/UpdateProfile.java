package com.gemvietnam.trafficgem.library;

import com.gemvietnam.trafficgem.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateProfile {        // user can only change name, phone, address
    private String name;
    private String phone;
    private String address;
    private String vehicle;

    public UpdateProfile(String _name, String _phone, String _address, String _vehicle){
        name = _name;
        phone = _phone;
        address = _address;
        vehicle = _vehicle;
    }

    public String exportStringFormatJson(){
        JSONObject entry = new JSONObject();
        try {
            entry.put(Constants.NAME, name);
            entry.put(Constants.PHONE, phone);
            entry.put(Constants.ADDRESS, address);
            entry.put(Constants.VEHICLE, vehicle);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return entry.toString();
    }
}
