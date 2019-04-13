package com.gemvietnam.trafficgem.service;

import com.gemvietnam.trafficgem.library.Credential;
import com.gemvietnam.trafficgem.library.Message;
import com.gemvietnam.trafficgem.library.User;

public interface ISendMode {
    public void sendCredential(Credential credential);

    public void sendMessage(String token, Message message);

    public void sendRegistrationInfo(User user);

    public void sendDataTraffic(String token, String data);
}
