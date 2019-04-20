package com.gemvietnam.trafficgem.library.responseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordResponse extends Response{
    private String responseMessage;
    private String message;
    private boolean success;
    private JSONObject jsonObject ;

    public ChangePasswordResponse(String responseMessage){ this.responseMessage = responseMessage;}


    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    public void analysis(){
        try {
            this.jsonObject = new JSONObject(responseMessage);
            this.message = (String) jsonObject.get("message");
            this.success = (boolean) jsonObject.get("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
