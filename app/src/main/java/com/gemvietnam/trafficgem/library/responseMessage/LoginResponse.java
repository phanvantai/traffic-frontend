package com.gemvietnam.trafficgem.library.responseMessage;

import android.util.Log;

import com.gemvietnam.trafficgem.library.User;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginResponse extends Response{
    private String responseMessage;
    private String message;
    private boolean success;
    private String token;
    private String phone;
    private String address;
    private String name;
    private String pathImage;
    private String email;
    private String vehicle;
    private JSONObject jsonObject;
    private User user;
    public LoginResponse(String responseMessage){
        this.responseMessage = responseMessage;
    }


    @Override
    public String getResponseMessage(){
        return responseMessage;
    }

    public void analysis(){
        try {
            this.jsonObject = new JSONObject(responseMessage);
            this.message = (String) jsonObject.get(Constants.Message);
            this.success = (boolean) jsonObject.get(Constants.Success);

            if(success){
                this.token = (String) jsonObject.get(Constants.Token);
                this.name = (String) jsonObject.get(Constants.Name);
                this.pathImage = (String) jsonObject.get(Constants.pathImage);
                this.email = (String) jsonObject.get(Constants.Email);
                this.phone = (String) jsonObject.get(Constants.Phone);
                this.address = (String) jsonObject.get(Constants.Address);
                this.vehicle = (String) jsonObject.get(Constants.Vehicle);
                user = new User(email, name, "null", vehicle, phone, address);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getToken(){
        return token;
    }

    public String getName(){
        return name;
    }

    public String getPathImage(){
        return pathImage;
    }

    public String getEmail(){
        return email;
    }

    public String getVehicle(){
        return vehicle;
    }
    @Override
    public String getMessage(){
        return message;
    }

    @Override
    public boolean getSuccess(){
        return success;
    }

    public User getUser(){ return user;}

}
