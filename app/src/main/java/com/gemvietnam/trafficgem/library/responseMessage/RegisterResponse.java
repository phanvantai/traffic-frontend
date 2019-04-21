package com.gemvietnam.trafficgem.library.responseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterResponse extends Response {
    private String responseMessage;
    private String message;
    private boolean success;
    private JSONObject jsonObject;
    private String token;
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
            this.message = (String) jsonObject.get(Constants.Message);
            this.success = (boolean) jsonObject.get(Constants.Success);
            if(success) this.token = (String) jsonObject.get(Constants.Token);
        } catch (JSONException e){
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

    public String getToken(){ return token; }
}
