package com.gemvietnam.trafficgem.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.gemvietnam.trafficgem.library.JsonObject;
import com.gemvietnam.trafficgem.utils.AppUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GPSTracker extends Service implements LocationListener {

    private Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for Network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    // JsonObject to write to cache file
    JsonObject mObject;
    int idJson;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 30 * 1; // 30s

    // Declaring a Location Manager
    protected LocationManager locationManager;

    private Location location;
    private String date;
    private String transport;
    private double speed;
    private String timeStamp;
    double latitude; // latitude
    double longitude; // longitude

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        idJson = 0;
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        //List<String> providers = locationManager.getProviders(true);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("TaiPV", "GPS provider is enabled");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            //location = getLocation();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        location = getLocation();
                        date = dateFormat.format(new Date());
                        timeStamp = timeFormat.format(new Date());
                        speed = location.getSpeed();
                        String tmp = date + " " + timeStamp + " " + "Lat " + Double.toString(location.getLatitude()) +
                                " Long " + Double.toString(location.getLongitude()) +
                                " Speed " + Float.toString(location.getSpeed());
                        AppUtils.writeLog(tmp);
                        JSONObject jsonObject = new JSONObject();
                        JsonObject object = new JsonObject(jsonObject, idJson);
                        object.init();
                        object.pushData(object.DataTraffic(location, date, transport, speed));
                        String message = object.exportString();
                        try {
                            Thread.sleep(MIN_TIME_BW_UPDATES);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private Location getLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
//        for (String provider : providers) {
//            Log.e("TaiPV", provider);
//            //locationManager.requestLocationUpdates(provider, 0, 0, this);
//            Location l = locationManager.getLastKnownLocation(provider);
//            if (l == null) {
//                continue;
//            }
//            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
//                // Found best last known location: %s", l);
//                bestLocation = l;
//            }
//        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return TODO;
        }
        bestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (bestLocation == null) {
            Log.e("TaiPV", "chua co gi");
        } else {
            Log.e("TaiPV", "Lat " + Double.toString(bestLocation.getLatitude()) +
                    " Lang " + Double.toString(bestLocation.getLongitude()) +
                    " Speed " + Float.toString(bestLocation.getSpeed()));
        }
        return bestLocation;
//        try {
//            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
//
//            // getting GPS status
//            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//            // getting network status
//            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//            if (!isGPSEnabled && !isNetworkEnabled) {
//                // no network provider is enabled
//            } else {
//                this.canGetLocation = true;
//                if (ActivityCompat.checkSelfPermission(this,
//                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(this,
//                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                            android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//
//                    } else {
//                        ActivityCompat.requestPermissions(this,
//                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                                1);
//                    }
//                }
//                // First get location from Network Provider
//                if (isNetworkEnabled) {
//
//                    locationManager.requestLocationUpdates(
//                            LocationManager.NETWORK_PROVIDER,
//                            0,
//                            0, this);
//
//                    Log.e("Network", "Network");
//                    if (locationManager != null) {
//                        location = locationManager
//                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//                        if (location != null) {
//                            latitude = location.getLatitude();
//                            longitude = location.getLongitude();
//                        }
//                    }
//                }
//
//                // if GPS Enabled get lat/long using GPS Services
//                if (isGPSEnabled) {
//                    if (location == null) {
//                        locationManager.requestLocationUpdates(
//                                LocationManager.GPS_PROVIDER,
//                                0,
//                                0, this);
//
//                        Log.d("GPS Enabled", "GPS Enabled");
//                        if (locationManager != null) {
//                            location = locationManager
//                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//                            if (location != null) {
//                                latitude = location.getLatitude();
//                                longitude = location.getLongitude();
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("TaiPV", "Lat " + Double.toString(location.getLatitude()) +
                " Lang " + Double.toString(location.getLongitude()) +
                " Speed " + Float.toString(location.getSpeed()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
