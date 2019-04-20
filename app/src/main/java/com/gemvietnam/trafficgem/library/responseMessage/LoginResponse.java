package com.gemvietnam.trafficgem.library.responseMessage;

import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginResponse extends Response{
    private String responseMessage;
    private String message;
    private boolean success;
    private String token;
    private String name;
    private String pathImage;
    private JSONObject jsonObject;
    public LoginResponse(String responseMessage){
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
            this.jsonObject = new JSONObject(responseMessage);
            this.message = (String) jsonObject.get("message");
            this.success = (boolean) jsonObject.get("success");
            this.token = (String) jsonObject.get("token");
            this.name = (String) jsonObject.get("name");
            this.pathImage = (String) jsonObject.get("image");
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
    @Override
    public String getMessage(){
        return message;
    }

    @Override
    public boolean getSuccess(){
        return success;
    }

}
