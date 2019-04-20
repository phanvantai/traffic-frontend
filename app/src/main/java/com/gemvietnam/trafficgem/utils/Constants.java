package com.gemvietnam.trafficgem.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    // Url server
    public static final String URL_SERVER = "";

    public static final String MY_TOKEN = "my_token";
    public static final String LAST_USER = "last_user";

    public static final String CHANEL_ID = "chanel_traffic";
    public static final String CHANEL_NAME = "traffic_gem";
    public static final String START_SERVICE = "start";
    public static final String STOP_SERVICE = "stop";
    public static final int ONGOING_NOTIFICATION_ID = 211;


    // Format date and time in JsonObject
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public static final int REQUEST_IMAGE_CAPTURE = 234;
}
