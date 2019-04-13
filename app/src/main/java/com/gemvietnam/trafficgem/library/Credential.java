package com.gemvietnam.trafficgem.library;

public class Credential {
    private String UserName;
    private String Password;

    public Credential(){}

    public Credential(String userName, String password){
        UserName = userName;
        Password = password;
    }
    public void setUserName(String userName){ UserName = userName;}

    public String getUserName() { return UserName; }

    public void setPassword(String password){ Password = password;}

    public String getPassword(){ return Password;}
}
