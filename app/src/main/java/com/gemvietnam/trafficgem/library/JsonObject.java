package com.gemvietnam.trafficgem.library;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonObject {
    private JSONObject mObject;
    private JSONArray mCoordinates = new JSONArray();
    //    public JsonObject(JSONObject mObject){ this.mObject = mObject;}
    public void setJsonObject(JSONObject jsonObject){ this.mObject = jsonObject;}

    public void init(){
        try {
            mObject.put("markers", mCoordinates);
        }   catch (JSONException e){
            e.printStackTrace();
        }
    }

//    public void pushDataTraffic(Traffic traffic) throws JSONException {
//        JSONObject entry = new JSONObject();
//        entry.put("lat", traffic.getLat());
//        entry.put("lng", traffic.getLon());
//        entry.put("avg_speed", traffic.getAvgSpeed());
//        entry.put("vehicle", traffic.getVehicle());
//        entry.put("record_time", traffic.getRecordTime());
//        entry.put("direction", traffic.getDirection());
//        mCoordinates.put(entry);
//    }

    public void pushDataTraffic(Traffic traffic) throws JSONException{
        mCoordinates.put(traffic.exportJson());
    }

    public String exportStringFormatJson() { return mObject.toString();}
}
