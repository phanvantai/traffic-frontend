package com.gemvietnam.trafficgem.service;

import com.gemvietnam.trafficgem.library.Credential;
import com.gemvietnam.trafficgem.library.JsonObject;
import com.gemvietnam.trafficgem.library.Message;
import com.gemvietnam.trafficgem.library.User;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.net.ssl.HttpsURLConnection;

public class SendMode implements ISendMode {
    private final String _url ;
    private HttpsURLConnection conn = null;
    private FileInputStream fileInputStream = null;
    private DataOutputStream dos = null;
    private InputStream is = null;
    public SendMode(String _url){
        this._url = _url;
    }

    public void init(){ // init connect
        try {
            URL url = new URL(_url);
            // open a Https connect to the url
            conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
//            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCredential(Credential credential){
        init();
        try {
            conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            conn.setRequestMethod("POST");
            conn.connect();
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(credential.getUserName());   // send
            dos.writeBytes(credential.getPassword());
        }   catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String token, Message message) {    // send report from user.
        init();
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024*1024;
        try {
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("POST");
            conn.connect();
            dos = new DataOutputStream(conn.getOutputStream());

            // send multipart form data
            dos.writeBytes(Integer.toString(message.getIDMsg()));
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            dos.writeBytes(dateFormat.format(message.getDate()));
            dos.writeBytes(Double.toString(message.getLocation().getAltitude()));
            dos.writeBytes(Double.toString(message.getLocation().getLongitude()));

            // send picutre
            File file = new File(message.getPathPicture());
            if(file.isFile()){
                fileInputStream = new FileInputStream(file);
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendRegistrationInfo(User user) {
        init();
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024*1024;
        try {
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            conn.setRequestMethod("POST");      //
            conn.connect();     // start connect

            dos = new DataOutputStream(conn.getOutputStream());

            // send multipart form data
            dos.writeBytes(user.getEmail());
            dos.writeBytes(user.getName());
            dos.writeBytes(user.getPassword());
            dos.writeBytes(user.getVehicle());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            dos.writeBytes(dateFormat.format(user.getSessionExpiryDate()));
            File file = new File(user.getPathAvatar());
            if(file.isFile()){
                FileInputStream fileInputStream = new FileInputStream(file);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0){
                    dos.write(buffer, 0, bufferSize);       // send
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendDataTraffic(String token, String data) {    // data: convert from json object to string format json
        init();
        try {
            conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("POST");
            conn.connect();

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(data);           // send data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateProfile(String token, User user){
        init();
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024*1024;
        try {
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("PUT");   // set method
            conn.connect();
            dos = new DataOutputStream(conn.getOutputStream());
            dos = new DataOutputStream(conn.getOutputStream());

            // send multipart form data
            dos.writeBytes(user.getEmail());
            dos.writeBytes(user.getName());
            dos.writeBytes(user.getPassword());
            dos.writeBytes(user.getVehicle());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");    // convert date into string
            dos.writeBytes(dateFormat.format(user.getSessionExpiryDate()));
            File file = new File(user.getPathAvatar());
            if(file.isFile()){      // check file
                FileInputStream fileInputStream = new FileInputStream(file);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);   // set buffer size suitable
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0){
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
    public void changePassword(String token,String oldPassword, String newpassword){
        init();
        try {
            conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("PUT");
            conn.connect();

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(oldPassword);        // send old pass
            dos.writeBytes(newpassword);        // send new pass

        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public String getResponse(){        // get response from server format str
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
