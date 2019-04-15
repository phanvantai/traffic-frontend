package com.gemvietnam.trafficgem.utils;

public class MyToken {
    private static MyToken instance;
    private String mToken;
    private long mDate;

    private MyToken() { }

    public static MyToken getInstance() {
        if (instance == null) {
            instance = new MyToken();
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
        long size;
        size = System.currentTimeMillis() - this.mDate;
        if (size >= 60*5 /*432000000*/) {// 5 days
            return true;
        }
        else return false;
    }
}
