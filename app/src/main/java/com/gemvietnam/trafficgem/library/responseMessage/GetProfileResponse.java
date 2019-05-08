package com.gemvietnam.trafficgem.library.responseMessage;

import com.gemvietnam.trafficgem.library.User;

import org.json.JSONException;
import org.json.JSONObject;

public class GetProfileResponse extends Response {
    private String responseMessage;
    private String message;
    private boolean success;
    private JSONObject jsonObject, jsonUserProfile ;
    private User user = new User();
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
            if(success){
                this.jsonUserProfile = (JSONObject) jsonObject.get(Constants.Mobile_User);
            }
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
            String email = jsonUserProfile.getString(Constants.Email);
            user.setEmail(email);
            String phone = jsonUserProfile.getString(Constants.Phone);
            user.setName((String) jsonUserProfile.get(Constants.Name));
            user.setPhone(phone);
            user.setAddress((String) jsonUserProfile.get(Constants.Address));
            user.setVehicle((String) jsonUserProfile.get(Constants.Vehicle));
            String pathAvatar = "";
            if(jsonUserProfile.get(Constants.pathImage) == null){
                pathAvatar = "null";
            }
            user.setAvatar(pathAvatar);
//            user.setAvatar((String) jsonUserProfile.get(Constants.pathImage));
        } catch (JSONException e){
            e.printStackTrace();
        }
        return user;
    }
}
