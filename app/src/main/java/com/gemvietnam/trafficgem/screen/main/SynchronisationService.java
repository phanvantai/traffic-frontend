package com.gemvietnam.trafficgem.screen.main;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.gemvietnam.trafficgem.library.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SynchronisationService extends Service {
    @Override
    public IBinder onBind(Intent intent){ return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        super.onStartCommand(intent, flags, startID);

        new Thread((new Runnable() {
            @Override
            public void run() {
                OutputStream os = null;
                InputStream is = null;
                HttpsURLConnection conn = null;
                String myUrl = "";          // input need edit
                int id = 0;                // input need edit
                Location location = null;   // input need edit
                String date = "";           // input need edit
                String transport = "";      // input need edit
                double speed = 0.0;         // input need edit
                try {
                    URL url = new URL(myUrl);
                    JSONObject jsonObject = new JSONObject();
                    JsonObject object = new JsonObject(jsonObject, id);
                    object.init();
                    object.pushData(object.DataTraffic(location, date, transport, speed));
                    String message = object.exportString();

                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setFixedLengthStreamingMode(message.getBytes().length);

                    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                    conn.connect();

                    os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(message.getBytes());       // send data

                    os.flush();

                    is = conn.getInputStream();     // get data from server

                }   catch (IOException e){
                    e.printStackTrace();
                } finally {
                    try {
                        os.close();
                        is.close();
                    }   catch (IOException e){
                        e.printStackTrace();
                    }
                    conn.disconnect();
                }
            }
        })).start();
        stopSelf();
        return flags;
    }
}
