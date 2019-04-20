package com.gemvietnam.trafficgem.library.responseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterResponse extends Response {
    private String responseMessage;
    private String message;
    private JSONObject jsonObject;
    public RegisterResponse(String responseMessage){
        this.responseMessage = responseMessage;
    }

    @Override
    public String getResponseMessage(){
        return responseMessage;
    }

    public void analysist(){
        try {
            this.jsonObject = new JSONObject(responseMessage);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getMessage(){
        try {
            message = (String) jsonObject.get("message");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
