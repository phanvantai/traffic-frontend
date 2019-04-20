package com.gemvietnam.trafficgem.library.responseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateProfile {
    private String name;
    private String phone;
    private String address;

    public UpdateProfile(String _name, String _phone, String _address){
        name = _name;
        phone = _phone;
        address = _address;
    }

    public String exportStringFormatJson(){
        JSONObject entry = new JSONObject();
        try {
            entry.put("name", name);
            entry.put("phone", phone);
            entry.put("address", address);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return entry.toString();
    }
}
