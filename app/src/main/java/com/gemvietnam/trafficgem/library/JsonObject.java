package com.gemvietnam.trafficgem.library;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonObject {
    private JSONObject jsonObject;
    private JSONArray coordinates = new JSONArray();
    //    public JsonObject(JSONObject jsonObject){ this.jsonObject = jsonObject;}
    public void setJsonObject(JSONObject jsonObject){ this.jsonObject = jsonObject;}

    public void init(){
        try {
            jsonObject.put("coordinates", coordinates);
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
        coordinates.put(entry);
    }

    public String exportString() {  return jsonObject.toString();}
}
