package com.gemvietnam.trafficgem.library;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonObject {
    private int id;
    private JSONObject jsonObject ;
    private JSONArray coordinates = new JSONArray();

    public JsonObject(JSONObject jsonObject,int key){ this.jsonObject = jsonObject; this.id = key; }

    public void setJsonObject(JSONObject jsonObject){ this.jsonObject = jsonObject;}

    public JSONObject getJsonObject(){ return jsonObject; }

    public void setKey(int key){ this.id = key; }

    public int getKey(){ return id; }

    public void init(){
        try {
            jsonObject.put("id", id);
            jsonObject.put("coordinates", coordinates);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Traffic DataTraffic(Location location, String date, String transport, double speed){
        Traffic traffic = new Traffic();
        traffic.setLocation(location);
        traffic.setTimeStamp(date);
        traffic.setTransport(transport);
        traffic.setSpeed(speed);
        return traffic;
    }

    public void pushData(Traffic traffic){
        JSONArray entry = new JSONArray();
        entry.put(traffic);
        coordinates.put(entry);
    }

    public String exportString(){
        return jsonObject.toString();
    }
}
