package com.gemvietnam.trafficgem.library;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import static com.gemvietnam.trafficgem.utils.Constants.ADDRESS;
import static com.gemvietnam.trafficgem.utils.Constants.EMAIL;
import static com.gemvietnam.trafficgem.utils.Constants.NAME;
import static com.gemvietnam.trafficgem.utils.Constants.PASSWORD;
import static com.gemvietnam.trafficgem.utils.Constants.PHONE;
import static com.gemvietnam.trafficgem.utils.Constants.VEHICLE;

public class User {
    private String Email;
    private String Name;
    private String Password;
    private String Vehicle;
    private String Phone;
    private String Address;
        private String Avatar;
    public User(){}
    public User(String _email, String _name, String _vehicle, String _phone, String _address, String _avatar) {
        Email = _email;
        Name = _name;
        //Password = _password;
        Vehicle = _vehicle;
        Phone = _phone;
        Address = _address;
        Avatar = _avatar;
    }

    public void setEmail(String _email){ Email = _email; }

    public String getEmail(){ return Email; }

    public void setName(String _name){ Name = _name; }

    public String getName(){ return Name; }

    public void setPassword(String _pass){ this.Password = _pass;}

    public String getPassword(){ return Password; }

    public void setVehicle(String _vehicle){ Vehicle = _vehicle; }

    public String getVehicle(){ return Vehicle; }

    public void setPhone(String _phone){ Phone = _phone; }

    public String getPhone(){ return Phone; }

    public void setAddress(String _address){ Address = _address; }

    public String getAddress(){ return Address; }
    public void setAvatar(String _path){ Avatar = _path; }

    public String getPathAvatar(){ return Avatar; }

    public String exportStringFormatJson(){
        JSONObject entry = new JSONObject();
        try {
            entry.put(EMAIL, Email);
            entry.put(NAME, Name);
            entry.put(PASSWORD, Password);
            entry.put(VEHICLE,Vehicle);
            entry.put(PHONE, Phone);
            entry.put(ADDRESS, Address);
        } catch (JSONException e){
            e.printStackTrace();
        }
//        entry.put("avatar",Avatar);
        return entry.toString();
    }
}
