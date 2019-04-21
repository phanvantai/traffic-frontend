package com.gemvietnam.trafficgem.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    // Url server
    public static final String URL_SERVER = "";
    public static final String URL_LOGIN = URL_SERVER + "/api/login";
    public static final String URL_MARKER = URL_SERVER + "/api/marker";
    public static final String URL_REGISTER = URL_SERVER + "/api/register";
    public static final String URL_CURRENT = URL_SERVER + "/api/getcurrent";
    public static final String URL_PASSWORD = URL_SERVER + "api/user/changepassword";
    public static final String URL_PROFILE = URL_SERVER + "api/user/editprofile";

    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";
    public static final String TOKEN = "remember_token";

    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String VEHICLE = "vehicle";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final String IMAGE = "image";

    public static final String MY_TOKEN = "my_token";
    public static final String LAST_USER = "last_user";

    public static final String SALT_BCRYPT = "traffic";

    public static final long SESSION_TIME = 1000 * 60 * 60 * 24 * 7; // 7 days

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
