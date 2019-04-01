package com.gemvietnam.trafficgem.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.JsonObject;
import com.gemvietnam.trafficgem.library.Traffic;
import com.gemvietnam.trafficgem.screen.main.MainActivity;
import com.gemvietnam.trafficgem.utils.AppUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.gemvietnam.trafficgem.utils.AppUtils.ONGOING_NOTIFICATION_ID;
import static com.gemvietnam.trafficgem.utils.AppUtils.START_SERVICE;
import static com.gemvietnam.trafficgem.utils.AppUtils.STOP_SERVICE;
import static com.gemvietnam.trafficgem.utils.AppUtils.dateFormat;
import static com.gemvietnam.trafficgem.utils.AppUtils.timeFormat;

/**
 * Created by TaiPV on 25/03/2019
 * Service collect location data
 */
public class LocationTracker extends Service
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Context mContext;

    // location, update each 5s
    private Location mCurrentLocation;

    // date and time when get location
    private String mDate;
    private String mTimeStamp;

    // user's transport
    private String mTransport;

    // user's speed
    private double mSpeed;

    // user's direction
    private String mDirection;
    // API and LocationRequest with time update request
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds

    // JsonObject to write to cache file
    JsonObject mObject;
    // user's id
    int idJson;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();

        // we build google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
        super.onCreate();
    }

    /**
     * Service do something here
     * @param intent intent from MainActivity, get data from this
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        String action = intent.getAction();
        if (action.equals(START_SERVICE)) {
            startInForeground(this);
            doStart();
        } else if (action.equals(STOP_SERVICE)) {
            Log.i("TaiPV", "Received Stop Foreground Intent");
            //your end service code
            stopForeground(true);
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * service do something here when start
     * @param //add later (id, transport, ...)
     */
    private void doStart() {
        idJson = 0;
        JSONObject jsonObject = new JSONObject();
        mObject = new JsonObject();
        mObject.setJsonObject(jsonObject);
        mObject.init();
        mTransport = "car";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Location temp = null;
                float distanceTo;
                while (true) {
                    if (ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&  ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    // Permissions ok, we get last location
                    mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    if (temp == null) {
                        distanceTo = 0;
                        temp = mCurrentLocation;    // avoid errors direction
                    } else {
                        distanceTo = mCurrentLocation.distanceTo(temp);
                    }

                    if (mCurrentLocation != null) {
                        mDate = dateFormat.format(new Date());
                        mTimeStamp = timeFormat.format(new Date());
                        mSpeed = (3.6*distanceTo)/5d;
                        mDirection = getDirection(temp, mCurrentLocation);
//                        if (Build.VERSION.SDK_INT >= 26) {
//                            mSpeed = location.getSpeedAccuracyMetersPerSecond();
//                        } else {
//                            mSpeed = location.getmSpeed();
//                        }

                        String tmp = mDate + " " + mTimeStamp + " " + "Lat " + Double.toString(mCurrentLocation.getLatitude()) +
                                " Long " + Double.toString(mCurrentLocation.getLongitude()) +
                                " Speed " + Double.toString(mSpeed)+ " Direction "+mDirection;

                        Log.e("TaiPV1", tmp);
                        AppUtils.writeLog(tmp);
                        Traffic traffic = new Traffic(mCurrentLocation, mTimeStamp, mDate, mTransport, mSpeed, mDirection);

                        try {
                            mObject.pushDataTraffic(traffic);
//                            Log.i("TaiPV", mObject.exportString());
                        } catch (Exception e) {
                            //
                        }

                    }


                    temp = mCurrentLocation;
                    try {
                        // Sleep 5s
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    // orientation based on 2 position
    private String getDirection(Location loc1, Location loc2){
        Directions direction = null;
        final double Cos45 = 1.0d/Math.sqrt(2.0d);
        final double Cos45d2 = Math.sqrt((Cos45 + 1.0d)/2.0d);
        final double Sin45d2 = Math.sqrt(1.0d-Cos45d2*Cos45d2);
        double X,Y;
        X = loc2.getLatitude() - loc1.getLatitude();
        Y = loc2.getLongitude() - loc1.getLongitude();
        double denta = Math.sqrt(X*X + Y*Y);
        double CosToOx, CosToOy;
        CosToOx = X/denta;
        CosToOy = Y/denta;

        if(CosToOy >= 0){
            if(CosToOx >= 0){
                if(CosToOx >= Cos45d2)  return direction.East;
                else if(CosToOx <= Sin45d2) return direction.North;
                else return direction.NorthEast;
            }   else {
                if(CosToOx <= -Cos45d2)   return direction.West;
                else if(CosToOx >= -Sin45d2) return direction.North;
                else return direction.NorthWest;
            }
        }   else {
            if(CosToOx >= 0){
                if(CosToOx >= Cos45d2)  return direction.East;
                else if(CosToOx <= Sin45d2) return direction.South;
                else return direction.SouthEast;
            }   else {
                if(CosToOx <= -Cos45d2) return direction.West;
                else if(CosToOx >= -Sin45d2) return direction.South;
                else return direction.SouthWest;
            }
        }
    }

    /**
     * startForeground with android O and above
     * @param context
     */
    private void startInForeground(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= 26) {
            Notification notification = new Notification.Builder(context, "TaiPV")
                    .setContentTitle("TrafficGEM")
                    .setContentText("Collecting location data..")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(ONGOING_NOTIFICATION_ID, notification);
        }
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
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        startLocationUpdates();
    }

    /**
     * Create Location update request
     */
    private void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
            mDate = dateFormat.format(new Date());
            mTimeStamp = timeFormat.format(new Date());
            if (Build.VERSION.SDK_INT >= 26) {
                mSpeed = location.getSpeedAccuracyMetersPerSecond();
            } else {
                mSpeed = location.getSpeed();
            }
            mTransport = "car";

            //
            String tmp = mDate + " " + mTimeStamp + " " + "Lat " + Double.toString(location.getLatitude()) +
                    " Long " + Double.toString(location.getLongitude()) +
                    " Speed " + Float.toString(location.getSpeed());
            //
//            Log.e("TaiPV", tmp);
            //AppUtils.writeLog(tmp);
            //mObject.pushData(mObject.DataTraffic(mCurrentLocation, mDate, mTransport, mSpeed));
        }
    }
}
