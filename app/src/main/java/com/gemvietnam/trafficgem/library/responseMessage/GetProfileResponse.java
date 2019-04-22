package com.gemvietnam.trafficgem.library.responseMessage;

import com.gemvietnam.trafficgem.library.User;

import org.json.JSONException;
import org.json.JSONObject;

public class GetProfileResponse extends Response {
    private String responseMessage;
    private String message;
    private boolean success;
    private JSONObject jsonObject, jsonMobileUser ;

    public GetProfileResponse(String responseMessage){ this.responseMessage = responseMessage;}


    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    public void analysis(){
        try {
            this.jsonObject = new JSONObject(responseMessage);
            this.message = (String) jsonObject.get(Constants.Message);
            this.success = (boolean) jsonObject.get(Constants.Success);
            this.jsonMobileUser = (JSONObject) jsonObject.get(Constants.MobileUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getMessage(){
        return message;
    }

    @Override
    public boolean getSuccess(){ return success; }

    public User getMobileUser(){
        User user = new User();
        try {
            user.setEmail((String) jsonMobileUser.get(Constants.Email));
            user.setName((String) jsonMobileUser.get(Constants.Name));
            user.setPhone((String) jsonMobileUser.get(Constants.Phone));
            user.setAddress((String) jsonMobileUser.get(Constants.Address));
        } catch (JSONException e){
            e.printStackTrace();
        }
        return user;
    }
}
