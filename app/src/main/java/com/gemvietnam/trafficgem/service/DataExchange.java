package com.gemvietnam.trafficgem.service;

import com.gemvietnam.trafficgem.library.Credential;
import com.gemvietnam.trafficgem.library.Message;
import com.gemvietnam.trafficgem.library.Traffic;
import com.gemvietnam.trafficgem.library.UpdateProfile;
import com.gemvietnam.trafficgem.library.User;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.net.ssl.HttpsURLConnection;

public class DataExchange implements IDataExchange {
    private String _url;
    private HttpsURLConnection conn = null;
    private FileInputStream fileInputStream = null;
    private DataOutputStream dos = null;
    public DataExchange(String url){ this._url = url;}

    public void init(){
        try {
            URL url = new URL(_url);
            // open a Https connect to the url
            conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCredential(Credential credential) {
        init();
        try {
            conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            conn.setRequestMethod("POST");
            conn.connect();
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(credential.exportStringFormatJson());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendRegistrationInfo(User user) {
        init();
        try {
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestMethod("POST");
            conn.connect();

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(user.exportStringFormatJson());
//            dos.writeBytes(user.getName());
//            dos.writeBytes(user.getEmail());
//            dos.writeBytes(user.getPassword());
//            dos.writeBytes(user.getVehicle());
//            DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss dd-mm-yyyy");
//            dos.writeBytes(dateFormat.format(user.getSessionExpiryDate()));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateProfile(String token, UpdateProfile profile) {
        init();
        try {
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("PUT");
            conn.connect();

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(profile.exportStringFormatJson());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void changePassword(String token, String oldPassword, String newPassword) {
        init();
        try {
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("PUT");
            conn.connect();

            dos = new DataOutputStream(conn.getOutputStream());
            JSONObject entry = new JSONObject();
            try {
                entry.put("oldPassword", oldPassword);
                entry.put("newPassword", newPassword);
            } catch (JSONException e){
                e.printStackTrace();
            }
            dos.writeBytes(entry.toString());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void getFuture(String token, int layer) {
        init();
        try {
            conn.setRequestProperty("Authorization", token);
            conn.setRequestProperty("Layer", String.valueOf(layer));
            conn.setRequestMethod("GET");
            conn.connect();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void getCurrent(String token, int layer) {
        init();
        try {
            conn.setRequestProperty("Authorization", token);
            conn.setRequestProperty("Layer", String.valueOf(layer));
            conn.setRequestMethod("GET");
            conn.connect();


        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendDataTraffic(String token, Traffic traffic) {
        init();
        try {
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("POST");
            conn.connect();

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(traffic.exportStringFormatJson());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendPicture(String token, String pathPicture) {
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024*1024;
        init();
        try {
            conn.setRequestProperty("Content-Type", "image/png");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("POST");
            conn.connect();

            dos = new DataOutputStream(conn.getOutputStream());
            File file = new File(pathPicture);
            if(file.isFile()){
                FileInputStream fileInputStream = new FileInputStream(file);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while(bytesRead > 0){
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void getUserProfile(String token) {
        init();
        try {
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("GET");
            conn.connect();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void report(String token, Message reportMessage){
        init();
        try {
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("POST");
            conn.connect();

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(reportMessage.exportStringFormatJson());
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public String getResponse(){
        String serverResponseMessage = "";
        int serverResponseCode;
        try {
            serverResponseCode = conn.getResponseCode();
            if(serverResponseCode == 200){
                serverResponseMessage = conn.getResponseMessage();
            }
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverResponseMessage;
    }
}
