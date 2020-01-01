package com.bishram.payment.keeper.models;

public class UserBasicDetails {
    private String userName;
    private String userMobile;
    private String uidAuth;

    public UserBasicDetails() {
        // Empty constructor used while reading data
    }

    public UserBasicDetails(String userName, String userMobile, String uidAuth) {
        this.userName = userName;
        this.userMobile = userMobile;
        this.uidAuth = uidAuth;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public String getUidAuth() {
        return uidAuth;
    }
}
