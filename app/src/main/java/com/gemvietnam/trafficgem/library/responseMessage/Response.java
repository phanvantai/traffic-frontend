package com.gemvietnam.trafficgem.library.responseMessage;

public abstract class Response {
    public abstract String getResponseMessage();        // All message string format json from server.
    public abstract boolean getSuccess();               // success: true/false;
    public abstract String getMessage();                // message: represent success.
}
