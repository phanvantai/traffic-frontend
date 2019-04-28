package com.gemvietnam.trafficgem.utils;
import com.gemvietnam.trafficgem.library.UpdateProfile;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    // Url server
    private static final String URL_SERVER = "http://68.183.238.65:8000";
    public static final String URL_LOGIN = URL_SERVER + "/api/login";
    public static final String URL_MARKER = URL_SERVER + "/api/marker";
    public static final String URL_REGISTER = URL_SERVER + "/api/register";
    public static final String URL_CURRENT = URL_SERVER + "/api/getcurrent";
    public static final String URL_FUTURE = URL_SERVER + "/api/getfuture";
    public static final String URL_PASSWORD = URL_SERVER + "/api/user/changepassword";
    public static final String URL_GET_PROFILE = URL_SERVER + "/api/user/profile";
    public static final String URL_EDIT_PROFILE = URL_SERVER + "/api/user/editprofile";
    public static final String URL_REPORT = URL_SERVER + "/api/user/report";
    public static final String URL_GET_REPORT = URL_SERVER + "/api/user/notification";
    public static final String URL_AVATAR = URL_SERVER + "/api/user/avatar";

    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";
    public static final String TOKEN = "remember_token";

    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String VEHICLE = "vehicle";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final String AVATAR = "avatar";
    public static final String TIME = "time";
    public static final String LAYER = "layer";

    public static final String MY_TOKEN = "my_token";
    public static final String LAST_USER = "last_user";

    public static final String IDMsg = "IDMsg";
    public static final String Time_Stamp = "time_stamp";
    public static final String Latitude = "lat";
    public static final String Longitude = "lng";
    public static final String SALT_BCRYPT = "traffic";

    public static final long SESSION_TIME = 1000 * 60 * 60 * 24 * 7; // 7 days

    public static final String CHANEL_ID = "chanel_traffic";
    public static final String CHANEL_NAME = "traffic_gem";
    public static final String START_SERVICE = "start";
    public static final String STOP_SERVICE = "stop";
    public static final int ONGOING_NOTIFICATION_ID = 211;


    // Format date and time in JsonObject
    public static final SimpleDateFormat RECORD_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
    public static final SimpleDateFormat LOGIN_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault());
    public static final int REQUEST_IMAGE_CAPTURE = 234;
}
