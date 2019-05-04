package com.gemvietnam.trafficgem.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.JsonObject;
import com.gemvietnam.trafficgem.library.Traffic;
import com.gemvietnam.trafficgem.library.User;
import com.gemvietnam.trafficgem.library.responseMessage.Response;
import com.gemvietnam.trafficgem.library.responseMessage.SendMarkerResponse;
import com.gemvietnam.trafficgem.screen.main.MainActivity;
import com.gemvietnam.trafficgem.utils.CustomToken;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.orhanobut.hawk.Hawk;

import org.json.JSONObject;

import java.util.Date;

import static com.gemvietnam.trafficgem.utils.Constants.CHANEL_ID;
//import static com.gemvietnam.trafficgem.utils.Constants.DATE_FORMAT;
import static com.gemvietnam.trafficgem.utils.Constants.LAST_USER;
import static com.gemvietnam.trafficgem.utils.Constants.MY_TOKEN;
import static com.gemvietnam.trafficgem.utils.Constants.ONGOING_NOTIFICATION_ID;
import static com.gemvietnam.trafficgem.utils.Constants.RECORD_TIME_FORMAT;
import static com.gemvietnam.trafficgem.utils.Constants.START_SERVICE;
import static com.gemvietnam.trafficgem.utils.Constants.STOP_SERVICE;
//import static com.gemvietnam.trafficgem.utils.Constants.TIME_FORMAT;
import static com.gemvietnam.trafficgem.utils.Constants.URL_MARKER;

/**
 * Created by TaiPV on 25/03/2019
 * Service collect location data
 */
public class LocationTracker extends Service {
    private Context mContext;

    // location, update each 5s
    private Location mCurrentLocation;

    // CustomToken
    CustomToken mCustomToken;
    // User
    User mLastUser;

    // date and time when get location
//    private String mDate;
//    private String mTimeStamp;
    private String mRecord_Time;

    // user's transport
    private String mTransport;

    // user's speed
    private double mSpeed;

    // user's direction
    private String mDirection;
    // API and LocationRequest with time update request
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds

    // JsonObject to write to cache file
    JsonObject mObject;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();

        initFusedProvider();

        super.onCreate();
    }

    private void initFusedProvider() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        LocationCallback mLocationCallback = new LocationCallback();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Location temp = null;
                mCustomToken = Hawk.get(MY_TOKEN);
                mLastUser = Hawk.get(LAST_USER);
                float distanceTo;
                int count = 0;
                JSONObject jsonObject = new JSONObject();
                mObject = new JsonObject();
                mObject.setJsonObject(jsonObject);
                mObject.init();
                while (true) {
                    if (count == 3) {
                        try {
                            DataExchange trafficData = new DataExchange(URL_MARKER);
                            Log.d("json-test-traffic-data", mObject.exportStringFormatJson());
                            trafficData.sendDataTraffic(mCustomToken.getToken(), mObject.exportStringFormatJson());
                            // send done

                            // receive response
                            String response = trafficData.getResponse();

                            //
//                            SendMarkerResponse responseMsg = new SendMarkerResponse(response);
//                            responseMsg.analysis();
//                            responseMsg.getSuccess();
//                            responseMsg.getMessage();
                            //
                        } catch (Exception e) {
                            //
                        }
                        count = 0;
                        mObject = new JsonObject();
                        mObject.setJsonObject(jsonObject);
                        mObject.init();
                    }
                    if (ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&  ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    // Permissions ok, we get last location
                    mFusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object
                                        mCurrentLocation = location;
                                    }
                                }
                            });


                    if (temp == null) {
                        distanceTo = 0;
                        temp = mCurrentLocation;    // avoid errors direction
                    } else {
                        distanceTo = mCurrentLocation.distanceTo(temp);
                    }

                    if (mCurrentLocation != null) {
//                        mDate = DATE_FORMAT.format(new Date());
//                        mTimeStamp = TIME_FORMAT.format(new Date());
                        mRecord_Time = RECORD_TIME_FORMAT.format(new Date());
                        mSpeed = (3.6*distanceTo)/5d;
                        mDirection = getDirection(temp, mCurrentLocation);

                        Traffic traffic = new Traffic(mCurrentLocation, mRecord_Time, mTransport, mSpeed, mDirection);

                        try {
                            mObject.pushDataTraffic(traffic);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    count++;
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
        if(denta == 0)  return "";
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
            Notification notification = new Notification.Builder(context, CHANEL_ID)
                    .setContentTitle("TrafficGEM")
                    .setContentText("Collecting location data..")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(ONGOING_NOTIFICATION_ID, notification);
        }
    }
}
