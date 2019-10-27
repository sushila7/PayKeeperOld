package com.bishram.payment.keeper.models;

public class OwnersOfHouse {
    private String landlordName;
    private String landladyName;
    private String displayName;
    private String landlordMobile;
    private String landladyMobile;
    private String alternateMobile;
    private String uniqueID;

    public OwnersOfHouse() {
        // Default as well as mandatory constructor
        // Used while reading details
    }

    public OwnersOfHouse(String landlordName,
                         String landladyName,
                         String displayName,
                         String landlordMobile,
                         String landladyMobile,
                         String alternateMobile,
                         String uniqueID) {
        this.landlordName = landlordName;
        this.landladyName = landladyName;
        this.displayName = displayName;
        this.landlordMobile = landlordMobile;
        this.landladyMobile = landladyMobile;
        this.alternateMobile = alternateMobile;
        this.uniqueID = uniqueID;
    }

    public String getLandlordName() {
        return landlordName;
    }

    public String getLandladyName() {
        return landladyName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLandlordMobile() {
        return landlordMobile;
    }

    public String getLandladyMobile() {
        return landladyMobile;
    }

    public String getAlternateMobile() {
        return alternateMobile;
    }

    public String getUniqueID() {
        return uniqueID;
    }
}
