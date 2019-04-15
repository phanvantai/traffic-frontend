package com.gemvietnam.trafficgem.library;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private String Email;
    private String Name;
    private String Password;
    private String Vehicle;
    private String Avatar;
    private Date sessionExpiryDate;
    public User(){}
    public User(String _email, String _name, String _vehicle, String _avatar){
        Email = _email;
        Name = _name;
        Vehicle = _vehicle;
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

    public void setAvatar(String _path){ Avatar = _path; }

    public String getPathAvatar(){ return Avatar; }

    public void setSessionExpiryData(Date _sessionExpiryDate){ sessionExpiryDate = _sessionExpiryDate;}

    public Date getSessionExpiryDate(){ return sessionExpiryDate;}

    public JSONObject exportJson() throws JSONException {
        JSONObject entry = new JSONObject();
        entry.put("Email", Email);
        entry.put("Name", Name);
        entry.put("Vehicle",Vehicle);
        entry.put("Avatar",Avatar);
        return entry;
    }

}
