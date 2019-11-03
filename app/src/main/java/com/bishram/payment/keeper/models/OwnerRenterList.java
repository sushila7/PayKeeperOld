package com.bishram.payment.keeper.models;

public class OwnerRenterList {
    private String stringNickName;
    private String stringRentPaying;
    private String stringStatus;
    private String uniqueID;

    public OwnerRenterList() {
    }

    public OwnerRenterList(String stringNickName, String stringRentPaying, String stringStatus, String uniqueID) {
        this.stringNickName = stringNickName;
        this.stringRentPaying = stringRentPaying;
        this.stringStatus = stringStatus;
        this.uniqueID = uniqueID;
    }

    public String getStringNickName() {
        return stringNickName;
    }

    public String getStringRentPaying() {
        return stringRentPaying;
    }

    public String getStringStatus() {
        return stringStatus;
    }

    public String getUniqueID() {
        return uniqueID;
    }
}
