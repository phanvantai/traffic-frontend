package com.gemvietnam.trafficgem.library.responseMessage;

import android.util.Log;

import com.gemvietnam.trafficgem.library.Report;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetReportResponse extends Response {
    private String responseMessage;
    private String message;
    private boolean success;
    private JSONObject jsonObject;
    private JSONArray jsonData;
    public GetReportResponse(String responseMessage){
        this.responseMessage = responseMessage;
    }


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
                this.jsonData = (JSONArray) jsonObject.get(Constants.Data);
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

    public Report[] getReport(){
        int length = jsonData.length();
        Report[] reports = new Report[length];
        for(int i=0; i<length; i++){
            reports[i] = new Report();
            JSONObject reportJson = new JSONObject();
            try {
                reportJson = jsonData.getJSONObject(i);
                Log.d("reportjson", reportJson.toString());
                reports[i].setIDMsg((int)reportJson.get("idmsg"));
                reports[i].setLatitude((double)(reportJson.get(Constants.Latitude)));
                reports[i].setLongitude((double)(reportJson.get(Constants.Longitude)));
//                reports[i].setDate((String)jsonData.getJSONObject(i).get(Constants.Time_Stamp));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return reports;
    }
}