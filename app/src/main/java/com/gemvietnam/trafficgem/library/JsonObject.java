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
            mObject.put("mCoordinates", mCoordinates);
        }   catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void pushDataTraffic(Traffic traffic) throws JSONException {
        JSONObject entry = new JSONObject();
        entry.put("latitude", traffic.getLat());
        entry.put("longitude", traffic.getLon());
        entry.put("timeStamp", traffic.getTimeStamp());
        entry.put("date", traffic.getDate());
        entry.put("transport", traffic.getTransport());
        entry.put("speed", traffic.getSpeed());
        mCoordinates.put(entry);
    }

    public String exportString() { return mObject.toString();}
}
