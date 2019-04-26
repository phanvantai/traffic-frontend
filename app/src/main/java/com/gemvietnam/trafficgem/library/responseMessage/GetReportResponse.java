package com.gemvietnam.trafficgem.library.responseMessage;

import com.gemvietnam.trafficgem.library.Message;

import org.json.JSONException;
import org.json.JSONObject;

public class GetReportResponse extends Response {
    private String responseMessage;
    private String message;
    private boolean success;
    private JSONObject jsonObject, jsonData;
    private Message notification = null;
    public GetReportResponse(String responseMessage){ this.responseMessage = responseMessage;}


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
                notification = new Message();
                this.jsonData = (JSONObject) jsonObject.get(Constants.Data);
                notification.setIDMsg((int) jsonObject.get(Constants.IDMsg));
                notification.setLatitude((double) jsonObject.get(Constants.Latitude));
                notification.setLongitude((double) jsonObject.get(Constants.Longitude));
                notification.setDate((String) jsonObject.get(Constants.Date));
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
    public boolean getSuccess(){
        return success;
    }

    public Message getNotification(){
        return notification;
    }
}