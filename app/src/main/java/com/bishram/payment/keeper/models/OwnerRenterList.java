package com.bishram.payment.keeper.models;

public class OwnerRenterList {
    private String fullName;
    private String phoneNumber;
    private String monthRent;
    private String currentStatus;
    private String pushUid;

    public OwnerRenterList() {
    }

    public OwnerRenterList(String fullName, String phoneNumber, String monthRent, String currentStatus, String pushUid) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.monthRent = monthRent;
        this.currentStatus = currentStatus;
        this.pushUid = pushUid;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMonthRent() {
        return monthRent;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getPushUid() {
        return pushUid;
    }
}
