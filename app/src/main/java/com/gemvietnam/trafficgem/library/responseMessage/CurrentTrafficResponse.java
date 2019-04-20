package com.gemvietnam.trafficgem.library.responseMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class CurrentTrafficResponse extends Response{
    private int width = 0, height = 0;
    private String responseMessage;
    private String message;
    private boolean success;
    private JSONObject jsonObject, jsonData, jsonGrid;
    private JSONArray jsonCells;
    public CurrentTrafficResponse(String responseMessage){ this.responseMessage = responseMessage;}


    @Override
    public String getResponseMessage() {
        return null;
    }

    public void analysis(){
        try {
            this.jsonObject = new JSONObject(responseMessage);
            this.jsonData = (JSONObject) jsonObject.get("data");
            this.message = (String) jsonObject.get("message");
            this.success = (boolean) jsonObject.get("success");
            getGridInfo();
            getCellInfo();
        } catch (JSONException e){
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

    public void getGridInfo(){
        try {
            this.jsonGrid = (JSONObject) jsonData.get("grid");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getCellInfo(){
        try {
            this.jsonCells = (JSONArray) jsonData.get("cells");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public int getHeight(){
        try {
            this.height = (int) this.jsonGrid.get("height");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return height;
    }

    public int getWidth(){
        try {
            this.width = (int) this.jsonGrid.get("width");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return width;
    }

    public double getEast(){
        double east = 0;
        try {
            east = (double) this.jsonGrid.get("east");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return east;
    }

    public double getWest(){
        double west = 0;
        try {
            west = (double) this.jsonGrid.get("width");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return west;
    }

    public double getSouth(){
        double South = 0;
        try {
            South = (double) this.jsonGrid.get("width");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return South;
    }

    public double getNorth(){
        double North = 0;
        try {
            North = (double) this.jsonGrid.get("width");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return North;
    }

//    public JSONArray getColorArray(){
//        return jsonCells;
//    }

    public String[][] getColorArray(){
        String[][] temp = new String[height+1][width+1];
        try {
            for(int i=0; i<jsonCells.length(); i++){
                JSONObject jb = jsonCells.getJSONObject(i);
                temp[(int)jb.get("height")][(int)jb.get("width")] = (String)jb.get("color");
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return temp;
    }
}

/*
    huong dan
    Truyen ResponseMessage vao class.
    Sau do goi ham analysis de phan tich cu phap cua response
    Sau do goi cac ham get de nhan cac gia tri can thiet
    GET:
        height, width: kich thuoc cua marker
        W,S,N,E: toa do cua diem do
        colorArray: tra ve mang 2 chieu chua color tuong ung
 */