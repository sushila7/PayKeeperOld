package com.bishram.payment.keeper;

public class Constants {
    // All these constants will be used everywhere in the app classes
    // This is because to not duplicate and prevent redundancy of variables

    // Country codes
    public static final String COUNTRY_CODE_INDIA = "+91";

    // Phone number verification time out
    public static final long REQUEST_TIME_OUT = 60;

    // FIREBASE REALTIME DATABASE PATHS
    // Path for the owner
    public static final String FIREBASE_USER_OWNER_PATH = "users/owners";

    // Path for the renter
    public static final String FIREBASE_USER_RENTER_PATH = "users/renters";

    // Path for the list of renters rented an owner
    public static final String FIREBASE_OWNERS_RENTED_PATH = "owned_rented/owners";

    // Path for the list of owners owned by a renter
    public static final String FIREBASE_RENTERS_OWNED_PATH = "owned_rented/renters";
}
