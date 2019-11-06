package com.bishram.payment.keeper;

public class Constants {
    // All these constants will be used everywhere in the app classes
    // This is because to not duplicate and prevent redundancy of variables

    // Country codes
    public static final String COUNTRY_CODE_INDIA = "+91";

    public static final String KEY_UID = "key_uid";
    public static final String KEY_PHONE_NUM = "key_phone_num";
    public static final String KEY_CATEGORY = "key_category";
    public static final String KEY_USER = "user_type";
    public static final String KEY_OWNER = "key_owner";
    public static final String KEY_RENTER = "key_renter";
    public static final String USER_OWNER = "Owner";
    public static final String USER_RENTER = "Renter";

    // Phone number verification time out
    public static final long REQUEST_TIME_OUT = 60;

    // FIREBASE REALTIME DATABASE PATHS
    // Path for the owner
    public static final String FIREBASE_USER_OWNER_PATH = "users/owners";
    public static final String FIREBASE_USERS_OWNERS_BASIC_PATH = "users/owners/basic";
    public static final String FIREBASE_USERS_OWNERS_ADVANCE_PATH = "users/owners/advance";

    // Path for the renter
    public static final String FIREBASE_USER_RENTER_PATH = "users/renters";
    public static final String FIREBASE_USERS_RENTERS_BASIC_PATH = "users/renters/basic";
    public static final String FIREBASE_USERS_RENTERS_ADVANCE_PATH = "users/renters/advance";

    // Path for the list of renters rented an owner
    public static final String FIREBASE_OWNERS_RENTED_PATH = "owned_rented/owners";

    // Path for the list of owners owned by a renter
    public static final String FIREBASE_RENTERS_OWNED_PATH = "owned_rented/renters";
}
