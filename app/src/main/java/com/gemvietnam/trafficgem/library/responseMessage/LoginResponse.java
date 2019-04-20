package com.gemvietnam.trafficgem.library.responseMessage;

import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginResponse extends Response{
    private String responseMessage;
    private String message;
    private String token;
    private String name;
    private String pathImage;
    private JSONObject jsonObject;
    public LoginResponse(int responseCode, String responseMessage){
        this.responseMessage = responseMessage;
    }


    @Override
    public String getResponseMessage(){
        return responseMessage;
    }

    public void analysis(){
//        JsonParser parser = new JsonParser();
//        JSONObject json = (JSONObject) parser.parse(responseCode);
        try {
            this.jsonObject = new JSONObject(responseMessage.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getToken(){
        try {
            token = (String) jsonObject.get("token");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return token;
    }

    public String getName(){
        try {
            name = (String) jsonObject.get("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.name;
    }

    public String getPathImage(){
        try {
            pathImage = (String) jsonObject.get("pathImage");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return this.pathImage;
    }

    public String getMessage(){
        try {
            message = (String) jsonObject.get("message");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return this.message;
    }
}
