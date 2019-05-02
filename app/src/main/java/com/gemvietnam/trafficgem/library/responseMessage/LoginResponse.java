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
    private JSONObject jsonObject;
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
            Log.d("Test-success-1", String.valueOf(success));
            if(success){
                this.token = (String) jsonObject.get(Constants.Token);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getToken(){
        return token;
    }

    @Override
    public String getMessage(){
        return message;
    }

    @Override
    public boolean getSuccess(){
        return success;
    }

}
