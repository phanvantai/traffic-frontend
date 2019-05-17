package com.gemvietnam.trafficgem.service;

import android.graphics.Bitmap;
import android.util.Log;

import com.gemvietnam.trafficgem.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import static com.gemvietnam.trafficgem.utils.Constants.URL_GET_REPORT;
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
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        File sourceFile = new File(pathPicture);
        Log.d("test-name-image", sourceFile.getName());
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*jpg");

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(Constants.AVATAR, sourceFile.getName(), RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                .build();

        Request request = new Request.Builder()
                .addHeader(Constants.TOKEN, token)
//                .addHeader("cache-control", "no-cache")
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

    @Override
    public String getReport(String token, String period_time) {
//        RequestBody body = RequestBody.create(JSON, getInfo);
        Request request = new Request.Builder()
                .url(URL_GET_REPORT)
                .get()
                .addHeader("Content-Type", "application/json charset=utf-8;")
                .addHeader(Constants.TOKEN, token)
                .addHeader(Constants.Period_Time, period_time)
                .addHeader("cache-control", "no-cache")
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
}