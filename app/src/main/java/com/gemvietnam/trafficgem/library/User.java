package com.gemvietnam.trafficgem.library;

import com.gemvietnam.trafficgem.library.responseMessage.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import static com.gemvietnam.trafficgem.utils.Constants.SESSION_TIME;

public class User {
    private String mEmail;
    private String mName;
    private String mPassword = null;
    private String mVehicle;
    private String mPhone;
    private String mAddress;
    private String mAvatar = null;
    private long mLastLogin;
    private String mToken;

    public User(){}
    public User(String _email, String _name, String _vehicle, String _phone, String _address){
        mEmail = _email;
        mName = _name;
        mVehicle = _vehicle;
        mPhone = _phone;
        mAddress = _address;
    }

    public void setEmail(String _email){ mEmail = _email; }

    public String getEmail(){ return mEmail; }

    public void setName(String _name){ mName = _name; }

    public String getName(){ return mName; }

    public void setPassword(String _pass){ this.mPassword = _pass;}

    public String getPassword(){ return mPassword; }

    public void setVehicle(String _vehicle){ mVehicle = _vehicle; }

    public String getVehicle(){ return mVehicle; }

    public void setPhone(String _phone){ mPhone = _phone; }

    public String getPhone(){ return mPhone; }

    public void setAddress(String _address){ mAddress = _address; }

    public String getAddress(){ return mAddress; }

    public void setAvatar(String _path){ mAvatar = _path; }

    public String getPathAvatar(){ return mAvatar; }

    public void setLastLogin(long mLastLogin) {
        this.mLastLogin = mLastLogin;
    }

    public long getLastLogin() {
        return mLastLogin;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }

    public String getToken() {
        return mToken;
    }

    public boolean isExpired() {
        if (mToken == null) {
            return true;
        }
        long size = System.currentTimeMillis() - mLastLogin;
        return (size >= SESSION_TIME);
    }

    public String exportStringFormatJson(){
        JSONObject entry = new JSONObject();
        try {
            entry.put(Constants.Email, mEmail);
            entry.put(Constants.Name, mName);
            //if(mPassword == null)   mPassword = "NULL";
            entry.put(Constants.Password, mPassword);
            entry.put(Constants.Vehicle, mVehicle);
            entry.put(Constants.Phone, mPhone);
            entry.put(Constants.Address, mAddress);
            //if(mAvatar == null)  mAvatar = "NULL";
            //entry.put(Constants.pathImage, mAvatar);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return entry.toString();
    }
}

