package com.gemvietnam.trafficgem.library.responseMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static com.gemvietnam.trafficgem.utils.Constants.MESSAGE;
import static com.gemvietnam.trafficgem.utils.Constants.SUCCESS;

public class UpdateProfileResponse extends Response{
    private String responseMessage;
    private String message;
    private boolean success;
    private JSONObject jsonObject ;

    public UpdateProfileResponse(String responseMessage){ this.responseMessage = responseMessage;}


    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    public void analysis(){
        try {
            this.jsonObject = new JSONObject(responseMessage);
            this.message = (String) jsonObject.get(MESSAGE);
            this.success = (Boolean) jsonObject.get(SUCCESS);
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
