package com.gemvietnam.trafficgem.service;

import com.gemvietnam.trafficgem.library.Credential;
import com.gemvietnam.trafficgem.library.Message;
import com.gemvietnam.trafficgem.library.Traffic;
import com.gemvietnam.trafficgem.library.UpdateProfile;
import com.gemvietnam.trafficgem.library.User;


public interface IDataExchange {
    // send
    public void sendCredential(Credential credential);

    public void sendRegistrationInfo(User user);

    public void updateProfile(String token, UpdateProfile profile);

    public void changePassword(String token, String oldPassword, String newPassword);

    public void getFuture(String token, int layer);

    public void getCurrent(String token, int layer);

    public void sendDataTraffic(String token, Traffic dataTraffic);

    public void sendPicture(String token, String pathPicture);

    public void getUserProfile(String token);

    public void report(String token, Message reportMessage);

    // receive

    public String getResponse();
}
