package com.gemvietnam.trafficgem.library.responseMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class CurrentTrafficResponse extends Response{
    private int column = 56, row = 23;
    private String responseMessage;
    private String message;
    private String color[][] = new String[row][column];
    private JSONObject jsonObject;

    public CurrentTrafficResponse(String responseMessage){ this.responseMessage = responseMessage;}


    @Override
    public String getResponseMessage() {
        return null;
    }

    public void analysis(){
        try {
            this.jsonObject = new JSONObject(responseMessage);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void getMessage(){
        try {
            message = (String) jsonObject.get("message");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
                    // DOING
//    public void getColorArray(){
//        try {
//            color = jsonObject.get("color_array");
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//    }
}
