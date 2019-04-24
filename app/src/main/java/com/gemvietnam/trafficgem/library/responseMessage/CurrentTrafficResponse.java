package com.gemvietnam.trafficgem.library.responseMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import com.gemvietnam.trafficgem.library.responseMessage.Constants;

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
            this.success = (boolean) jsonObject.get(Constants.Success);
            this.message = (String) jsonObject.get(Constants.Message);
            if(success){
                this.jsonData = (JSONObject) jsonObject.get(Constants.Data);
                getGridInfo();
                getCellInfo();
            }
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
            this.jsonGrid = (JSONObject) jsonData.get(Constants.Grid);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getCellInfo(){
        try {
            this.jsonCells = (JSONArray) jsonData.get(Constants.Cells);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public int getHeight(){
        try {
            this.height = (int) this.jsonGrid.get(Constants.Height);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return height;
    }

    public int getWidth(){
        try {
            this.width = (int) this.jsonGrid.get(Constants.Width);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return width;
    }

    public double getEast(){
        double east = 0;
        try {
            east = (double) this.jsonGrid.get(Constants.East);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return east;
    }

    public double getWest(){
        double west = 0;
        try {
            west = (double) this.jsonGrid.get(Constants.West);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return west;
    }

    public double getSouth(){
        double South = 0d;
        try {
            South = (double) this.jsonGrid.get(Constants.South);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return South;
    }

    public double getNorth(){
        double North = 0;
        try {
            North = (double) this.jsonGrid.get(Constants.North);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return North;
    }

    public String[][] getColorArray(){
        String[][] temp = new String[height][width];
        try {
            for(int i=0; i<jsonCells.length(); i++){
                JSONObject jb = jsonCells.getJSONObject(i);
                temp[(int)jb.get(Constants.Height)][(int)jb.get(Constants.Width)] = (String)jb.get(Constants.Color);
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