package com.gemvietnam.trafficgem.library;

import com.gemvietnam.trafficgem.library.responseMessage.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class User {
    private String Email;
    private String Name;
    private String Password = null;
    private String Vehicle;
    private String Phone;
    private String Address;
    private String Avatar = null;

    public User(){}
    public User(String _email, String _name, String _vehicle, String _phone, String _address){
        Email = _email;
        Name = _name;
        Vehicle = _vehicle;
        Phone = _phone;
        Address = _address;
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
            entry.put(Constants.Email, Email);
            entry.put(Constants.Name, Name);
            if(Password == null)   Password = "NULL";
            entry.put(Constants.Password, Password);
            entry.put(Constants.Vehicle,Vehicle);
            entry.put(Constants.Phone, Phone);
            entry.put(Constants.Address, Address);
            if(Avatar == null)  Avatar = "NULL";
            entry.put(Constants.pathImage, Avatar);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return entry.toString();
    }
}

