package com.bishram.payment.keeper.models;

public class RentersOfHouse {
    private String renterNameMale;
    private String renterNameFemale;
    private String displayName;
    private String renterMaleMobile;
    private String renterFemaleMobile;
    private String alternateMobile;
    private String uniqueID;

    public RentersOfHouse() {
        // Default as well as mandatory constructor
        // Used while reading details
    }

    public RentersOfHouse(String renterNameMale,
                          String renterNameFemale,
                          String displayName,
                          String renterMaleMobile,
                          String renterFemaleMobile,
                          String alternateMobile,
                          String uniqueID) {
        this.renterNameMale = renterNameMale;
        this.renterNameFemale = renterNameFemale;
        this.displayName = displayName;
        this.renterMaleMobile = renterMaleMobile;
        this.renterFemaleMobile = renterFemaleMobile;
        this.alternateMobile = alternateMobile;
        this.uniqueID = uniqueID;
    }

    public String getRenterNameMale() {
        return renterNameMale;
    }

    public String getRenterNameFemale() {
        return renterNameFemale;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRenterMaleMobile() {
        return renterMaleMobile;
    }

    public String getRenterFemaleMobile() {
        return renterFemaleMobile;
    }

    public String getAlternateMobile() {
        return alternateMobile;
    }

    public String getUniqueID() {
        return uniqueID;
    }
}
