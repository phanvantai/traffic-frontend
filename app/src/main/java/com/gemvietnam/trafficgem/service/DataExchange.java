package com.gemvietnam.trafficgem.service;
//
//import android.util.Log;
//
//import com.gemvietnam.trafficgem.library.Credential;
//import com.gemvietnam.trafficgem.library.Report;
//import com.gemvietnam.trafficgem.library.UpdateProfile;
//import com.gemvietnam.trafficgem.library.User;
//import com.gemvietnam.trafficgem.utils.Constants;
//
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//
////import javax.net.ssl.HttpURLConnection;
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLContext;
//
//public class DataExchange implements IDataExchange {
//    private String _url;
//    //    private HttpsURLConnection conn = null;
//    private HttpURLConnection conn = null;
//    private FileInputStream fileInputStream = null;
//    private DataOutputStream dos = null;
//    public DataExchange(String url){ this._url = url;}
//
//    public void init(){
//        try {
//            URL url = new URL(_url);
//            // open a Https connect to the url
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setRequestProperty("Content-Type", "application/json");
////            conn.setRequestProperty("Content-Language", "en-US");
//            conn.setUseCaches(false);
//            conn.setDoInput(true);
//            conn.setConnectTimeout(30000);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void sendCredential(Credential credential) {
//        init();
//        try {
//            conn.setRequestMethod("POST");
//            conn.connect();
//            dos = new DataOutputStream(conn.getOutputStream());
//            dos.writeBytes(credential.exportStringFormatJson());
//        } catch (IOException e){
//            e.printStackTrace();
//        } catch (NullPointerException e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public String sendRegistrationInfo(String user) {
//        init();
//        try {
////            conn.setFixedLengthStreamingMode(user.exportStringFormatJson().getBytes().length);
//            conn.setRequestMethod("POST");
//            conn.connect();
//            dos = new DataOutputStream(conn.getOutputStream());
//            dos.writeBytes(user.exportStringFormatJson());
//            Log.d("test-json-register", user.exportStringFormatJson());
//
//        } catch (IOException e){
//            e.printStackTrace();
//        } catch (NullPointerException e){
//            e.printStackTrace();
//        }
//
//
//    }
//
//    @Override
//    public void updateProfile(String token, UpdateProfile profile) {        // send user's profile changed
//        init();
//        try {
//            conn.setRequestProperty(Constants.TOKEN, token);
//            conn.setRequestMethod("PUT");
//
//            dos = new DataOutputStream(conn.getOutputStream());
//            dos.writeBytes(profile.exportStringFormatJson());
//        } catch (IOException e){
//            e.printStackTrace();
//        } catch (NullPointerException e){
//            e.printStackTrace();
//        } catch (Exception e){
//
//        }
//    }
//
//    @Override
//    public void changePassword(String token, String oldPassword, String newPassword) {      //
//        init();
//        try {
//            conn.setRequestProperty(Constants.TOKEN, token);
//            conn.setRequestMethod("PUT");
//
//            dos = new DataOutputStream(conn.getOutputStream());
//            JSONObject entry = new JSONObject();
//            try {
//                entry.put("oldPassword", oldPassword);
//                entry.put("newPassword", newPassword);
//            } catch (JSONException e){
//                e.printStackTrace();
//            }
//            dos.writeBytes(entry.toString());
//        } catch (IOException e){
//            e.printStackTrace();
//        } catch (NullPointerException e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void getFuture(String token, int layer) {        //  send request to server to receive future traffic data
//        init();
//        try {
//            conn.setRequestProperty(Constants.TOKEN, token);
//            conn.setRequestProperty(Constants.LAYER, String.valueOf(layer));
//            conn.setRequestMethod("GET");
//            conn.connect();
//
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void getCurrent(String token, int layer) {       // send request to server to receive current traffic data
//        init();
//        try {
//            conn.setRequestProperty(Constants.TOKEN, token);
//            conn.setRequestProperty(Constants.LAYER, String.valueOf(layer));
//            conn.setRequestMethod("GET");
//            conn.connect();
//
//        } catch (IOException e){
//            e.printStackTrace();
//        } catch (NullPointerException e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void sendDataTraffic(String token, String dataTraffic) {     // send data traffic of user get from sensor .
//        init();
//        try {
//            conn.setRequestProperty("Content-Type", "text/plain");
//            conn.setRequestProperty(Constants.TOKEN, token);
//            conn.setRequestMethod("POST");
//            conn.connect();
//
//            dos = new DataOutputStream(conn.getOutputStream());
//            dos.writeBytes(dataTraffic);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void sendPicture(String token, String pathPicture) {         // send picture to server.
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 1024*1024;
//        init();
//        try {
//            conn.setRequestProperty("Content-Type", "image/png");
//            conn.setRequestProperty(Constants.TOKEN, token);
//            conn.setRequestMethod("POST");
//            conn.connect();
//
//            dos = new DataOutputStream(conn.getOutputStream());
//            File file = new File(pathPicture);
//            if(file.isFile()){
//                FileInputStream fileInputStream = new FileInputStream(file);
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                buffer = new byte[bufferSize];
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//                while(bytesRead > 0){
//                    dos.write(buffer, 0, bufferSize);
//                    bytesAvailable = fileInputStream.available();
//                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//                }
//            }
//        } catch (IOException e){
//            e.printStackTrace();
//        } catch (NullPointerException e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void getUserProfile(String token) {      // send request to server to receive profile
//        init();
//        try {
//            Log.d("test-token", token);
//            conn.setRequestProperty("remember_token", token);
//            conn.setRequestMethod("GET");
//            conn.connect();
//
////            dos = new DataOutputStream(conn.getOutputStream());
////            dos.writeBytes("");
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void report(String token, Report reportMessage){        // send report of user to server
//        init();
//        try {
//            conn.setRequestProperty(Constants.TOKEN, token);
//            conn.setRequestMethod("POST");
//            conn.connect();
//
//            dos = new DataOutputStream(conn.getOutputStream());
//            dos.writeBytes(reportMessage.exportStringFormatJson());
//        } catch (IOException e){
//            e.printStackTrace();
//        } catch (NullPointerException e){
//            e.printStackTrace();
//        }
//    }
//    @Override
//    public String getResponse(){        // receive response from server format string.
////        String serverResponseMessage = "";
//        StringBuffer serverResponseMessage = new StringBuffer();
//        int serverResponseCode;
//
//        try {
//            serverResponseCode = conn.getResponseCode();
//            Log.d("test-response-code", String.valueOf(serverResponseCode));
////          if(serverResponseCode == 200) {
////                serverResponseMessage = conn.getResponseMessage();
//
//            InputStream is = new BufferedInputStream(conn.getInputStream());
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//            String line;
//            while ((line = rd.readLine()) != null) {
//                serverResponseMessage.append(line);
//                serverResponseMessage.append("\n");
//            }
//            rd.close();
////          }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NullPointerException e){
//            e.printStackTrace();
//        }
//        Log.d("test--Response", serverResponseMessage.toString());
//        return serverResponseMessage.toString();
//    }
//}

import android.util.Log;

import com.gemvietnam.trafficgem.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.gemvietnam.trafficgem.utils.Constants.URL_AVATAR;
import static com.gemvietnam.trafficgem.utils.Constants.URL_CURRENT;
import static com.gemvietnam.trafficgem.utils.Constants.URL_EDIT_PROFILE;
import static com.gemvietnam.trafficgem.utils.Constants.URL_FUTURE;
import static com.gemvietnam.trafficgem.utils.Constants.URL_GET_PROFILE;
import static com.gemvietnam.trafficgem.utils.Constants.URL_LOGIN;
import static com.gemvietnam.trafficgem.utils.Constants.URL_MARKER;
import static com.gemvietnam.trafficgem.utils.Constants.URL_PASSWORD;
import static com.gemvietnam.trafficgem.utils.Constants.URL_REGISTER;
import static com.gemvietnam.trafficgem.utils.Constants.URL_REPORT;

public class DataExchange implements IDataExchange {

    private OkHttpClient client = new OkHttpClient();
    MediaType JSON = MediaType.parse("application/json; charset=utf-8;");


    @Override
    public String sendCredential(String credential) {
        RequestBody body = RequestBody.create(JSON, credential);
        Request request = new Request.Builder()
                .url(URL_LOGIN)
                .post(body)
                .build();

        Response response = null;
        String responseString = "";
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    public String sendRegistrationInfo(String user) {
        RequestBody body = RequestBody.create(JSON, user);
        Request request = new Request.Builder()
                .url(URL_REGISTER)
                .post(body)
                .build();

        Response response = null;
        String responseString = "";
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    public String updateProfile(String token, String profile){
        RequestBody body = RequestBody.create(JSON, profile);
        Request request = new Request.Builder()
                .url(URL_EDIT_PROFILE)
                .addHeader("Content-Type", "application/json charset=utf-8;")
                .addHeader(Constants.TOKEN, token)
                .addHeader("cache-control", "no-cache")
                .put(body)
                .build();

        Response response = null;
        String responseString = "";
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseString;
    }

    @Override
    public String changePassword(String token, String oldPassword, String newPassword){
        JSONObject jsonPass = new JSONObject();
        try {
            jsonPass.put("old_password", oldPassword);
            jsonPass.put("new_password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String passStringFormatJson = jsonPass.toString();

        RequestBody body = RequestBody.create(JSON, passStringFormatJson);

        Request request = new Request.Builder()
                .url(URL_PASSWORD)
                .addHeader("Content-Type", "application/json charset=utf-8;")
                .addHeader(Constants.TOKEN, token)
                .addHeader("cache-control", "no-cache")
                .put(body)
                .build();

        Response response = null;
        String responseString = "";
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    public String getFuture(String token, int layer) {
        Request request = new Request.Builder()
                .url(URL_FUTURE)
                .addHeader("Content-Type", "application/json charset=utf-8;")
                .addHeader(Constants.TOKEN, token)
                .addHeader(Constants.LAYER, String.valueOf(layer))
                .addHeader("cache-control", "no-cache")
                .get()
                .build();

        Response response = null;
        String responseString = "";
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (IOException e){
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    public String getCurrent(String token, int layer) {
        Request request = new Request.Builder()
                .url(URL_CURRENT)
                .addHeader("Content-Type", "application/json charset=utf-8;")
                .addHeader(Constants.TOKEN, token)
                .addHeader(Constants.LAYER, String.valueOf(layer))
                .addHeader("cache-control", "no-cache")
                .get()
                .build();

        Response response = null;
        String responseString = "";
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (IOException e){
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    public String sendDataTraffic(String token, String dataTraffic){
        RequestBody body = RequestBody.create(JSON, dataTraffic);

        Request request = new Request.Builder()
                .url(URL_MARKER)
                .addHeader("Content-Type", "application/json charset=utf-8;")
                .addHeader(Constants.TOKEN, token)
                .addHeader("cache-control", "no-cache")
                .post(body)
                .build();

        Response response = null;
        String responseString = "";
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    public String sendPicture(String token, String pathPicture) {
        //String fileName = pathPicture.substring(pathPicture.lastIndexOf("/")+1);
        File sourceFile = new File(pathPicture);
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(Constants.AVATAR, sourceFile.getName(), RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                .build();

        Request request = new Request.Builder()
                .addHeader(Constants.TOKEN, token)
                .addHeader("cache-control", "no-cache")
                .url(URL_AVATAR)
                .post(body)
                .build();

        Response response = null;
        String responseFormatString = "";
        try {
            response = client.newCall(request).execute();
            responseFormatString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseFormatString;
    }

    @Override
    public String getUserProfile(String token){
        Request request = new Request.Builder()
                .url(URL_GET_PROFILE)
                .addHeader("Content-Type", "application/json charset=utf-8;")
                .addHeader(Constants.TOKEN, token)
                .addHeader("cache-control", "no-cache")
                .get()
                .build();

        Response response = null;
        String responseString = "";
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    public String report(String token, String reportMessage) {
        RequestBody body = RequestBody.create(JSON, reportMessage);
        Request request = new Request.Builder()
                .url(URL_REPORT)
                .addHeader("Content-Type", "application/json charset=utf-8")
                .addHeader(Constants.TOKEN, token)
                .addHeader("cache-control", "no-cache")
                .post(body)
                .build();

        Response response = null;
        String responseFormatString = "";
        try {
            response = client.newCall(request).execute();
            responseFormatString = response.body().string();
        } catch (IOException e){
            e.printStackTrace();
        }

        return responseFormatString;
    }

}