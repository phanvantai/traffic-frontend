package com.gemvietnam.trafficgem.service;

import com.gemvietnam.trafficgem.library.Credential;
import com.gemvietnam.trafficgem.library.Report;
import com.gemvietnam.trafficgem.library.UpdateProfile;
import com.gemvietnam.trafficgem.library.User;

import java.io.IOException;

import okhttp3.Request;


public interface IDataExchange {
    // send
    public String sendCredential(String credential);

    public String sendRegistrationInfo(String user);

    public String updateProfile(String token, String profile);

    public String changePassword(String token, String oldPassword, String newPassword);

    public String getFuture(String token, int layer);

    public String getCurrent(String token, int layer);

    public String sendDataTraffic(String token, String dataTraffic);

    public String sendPicture(String token, String pathPicture);

    public String getUserProfile(String token);

    public String report(String token, String reportMessage);

    public String getReport(String token, String getInfo);
}
