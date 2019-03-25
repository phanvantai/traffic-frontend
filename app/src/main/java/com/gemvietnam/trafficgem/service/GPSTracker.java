package com.gemvietnam.trafficgem.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.JsonObject;
import com.gemvietnam.trafficgem.screen.main.MainActivity;
import com.gemvietnam.trafficgem.utils.AppUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by TaiPV on 25/03/2019
 * Service collect location data
 */
public class GPSTracker extends Service
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int ONGOING_NOTIFICATION_ID = 0211;
    private Context mContext;

    private Location location;
    private String date;
    private String transport;
    private double speed;
    private String timeStamp;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds

    // JsonObject to write to cache file
    JsonObject mObject;
    int idJson;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= 26) {
            Notification notification = new Notification.Builder(this, "TaiPV")
                    .setContentTitle("TrafficGEM")
                    .setContentText("Collecting location data..")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(ONGOING_NOTIFICATION_ID, notification);
        }

        idJson = 0;
        JSONObject jsonObject = new JSONObject();
        mObject = new JsonObject(jsonObject, idJson);
        mObject.init();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&  ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    // Permissions ok, we get last location
                    location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                    if (location != null) {
                        date = dateFormat.format(new Date());
                        timeStamp = timeFormat.format(new Date());
                        if (Build.VERSION.SDK_INT >= 26) {
                            speed = location.getSpeedAccuracyMetersPerSecond();
                        } else {
                            speed = location.getSpeed();
                        }
                        transport = "car";

                        //
                        String tmp = date + " " + timeStamp + " " + "Lat " + Double.toString(location.getLatitude()) +
                                " Long " + Double.toString(location.getLongitude()) +
                                " Speed " + Float.toString(location.getSpeed());
                        //
                        //Log.e("TaiPV", tmp);
                        AppUtils.writeLog(tmp);

                        mObject.pushData(mObject.DataTraffic(location, date, transport, speed));
                    }
                    try {
                        // Sleep 5s
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        if (intent != null) {
            String action = intent.getAction();
            if (action.equals("Stop")) {
                Log.i("TaiPV", "Received Stop Foreground Intent");
                //your end service code
                stopForeground(true);
                stopSelf();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            date = dateFormat.format(new Date());
            timeStamp = timeFormat.format(new Date());
            if (Build.VERSION.SDK_INT >= 26) {
                speed = location.getSpeedAccuracyMetersPerSecond();
            } else {
                speed = location.getSpeed();
            }
            transport = "car";

            //
            String tmp = date + " " + timeStamp + " " + "Lat " + Double.toString(location.getLatitude()) +
                    " Long " + Double.toString(location.getLongitude()) +
                    " Speed " + Float.toString(location.getSpeed());
            //
            Log.e("TaiPV", tmp);
            //AppUtils.writeLog(tmp);
            //mObject.pushData(mObject.DataTraffic(location, date, transport, speed));
        }
    }
}
