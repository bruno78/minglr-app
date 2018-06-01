package com.brunogtavares.minglr.FirebaseData;

/**
 * Created by brunogtavares on 5/31/18.
 */

public final class FirebaseContract {

    private FirebaseContract(){}

    public static abstract class FirebaseEntry {

        public static final String TABLE_NAME = "Users";

        public static final String COLUMN_SEX_FEMALE = "Female";
        public static final String COLUMN_SEX_MALE = "Male";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CONNECTIONS = "connections";
        public static final String COLUMN_NOPE = "nope";
        public static final String COLUMN_YEP = "yep";
        public static final String COLUMN_MATCHES = "matches";

        // This direct child from Users, stores user's info
        public static final String COLUMN_CUSTOMERS = "Customers";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_PROFILE_IMAGE_URL = "profileImageUrl";
    }
}