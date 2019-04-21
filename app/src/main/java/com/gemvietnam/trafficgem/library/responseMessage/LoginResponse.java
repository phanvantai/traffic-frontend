package com.gemvietnam.trafficgem.library.responseMessage;

import com.gemvietnam.trafficgem.library.User;

import org.json.JSONException;
import org.json.JSONObject;

import static com.gemvietnam.trafficgem.utils.Constants.ADDRESS;
import static com.gemvietnam.trafficgem.utils.Constants.EMAIL;
import static com.gemvietnam.trafficgem.utils.Constants.IMAGE;
import static com.gemvietnam.trafficgem.utils.Constants.MESSAGE;
import static com.gemvietnam.trafficgem.utils.Constants.NAME;
import static com.gemvietnam.trafficgem.utils.Constants.PHONE;
import static com.gemvietnam.trafficgem.utils.Constants.SUCCESS;
import static com.gemvietnam.trafficgem.utils.Constants.TOKEN;
import static com.gemvietnam.trafficgem.utils.Constants.VEHICLE;

public class LoginResponse extends Response{
    private String responseMessage;
    private String message;
    private boolean success;
    private String token;
    private User mUser;

    public LoginResponse(String responseMessage){
        this.responseMessage = responseMessage;
    }


    @Override
    public String getResponseMessage(){
        return responseMessage;
    }

    public void analysis(){
        try {
            JSONObject jsonObject = new JSONObject(responseMessage);
            this.message = (String) jsonObject.get(MESSAGE);
            this.success = (boolean) jsonObject.get(SUCCESS);
            if (success) {
                this.token = (String) jsonObject.get(TOKEN);
                String email = (String) jsonObject.get(EMAIL);
                String name = (String) jsonObject.get(NAME);
                String pathImage = (String) jsonObject.get(IMAGE);
                String vehicle = (String) jsonObject.get(VEHICLE);
                String phone = (String) jsonObject.get(PHONE);
                String address = (String) jsonObject.get(ADDRESS);
                mUser = new User(email, name, vehicle, phone, address, pathImage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getToken(){
        return token;
    }

    public User getUser() {
        return mUser;
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
