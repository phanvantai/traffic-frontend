package com.gemvietnam.trafficgem.library.responseMessage;

import com.gemvietnam.trafficgem.library.Report;

import org.json.JSONException;
import org.json.JSONObject;

public class GetReportResponse extends Response {
    private String responseMessage;
    private String message;
    private boolean success;
    private JSONObject jsonObject, jsonData;
    private Report report = null;
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
                report = new Report();
                this.jsonData = (JSONObject) jsonObject.get(Constants.Data);
                report.setIDMsg((int) jsonObject.get(Constants.IDMsg));
                report.setLatitude((double) jsonObject.get(Constants.Latitude));
                report.setLongitude((double) jsonObject.get(Constants.Longitude));
                report.setDate((String) jsonObject.get(Constants.Time_Stamp));
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

    public Report getNotification(){
        return report;
    }
}