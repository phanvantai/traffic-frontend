package com.gemvietnam.trafficgem.utils;

public class CustomToken {
    private static CustomToken instance;
    private String mToken;
    private long mDate;

    private CustomToken() { }

    public static CustomToken getInstance() {
        if (instance == null) {
            instance = new CustomToken();
        }
        return instance;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public String getToken() {
        return mToken;
    }

    public void setDate(long date) {
        this.mDate = date;
    }

    public long getDate() {
        return mDate;
    }

    public void removeToken() {
        mToken = null;
    }

    public boolean isExpired() {
        if (mToken == null) {
            return true;
        }
        long size;
        size = System.currentTimeMillis() - this.mDate;
        if (size >= 60*5*1000 /*432000000*/) {// 5 days
            return true;
        }
        else return false;
    }
}
