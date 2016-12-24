package com.sap.it.sr.util;

public class SessionHolder {
    public static final String USER_ID = "USER_ID";
    public static final String USER_FULLNAME = "USER_FULLNAME";

    private static final ThreadLocal<String> _USER_ID = new ThreadLocal<String>();
    private static final ThreadLocal<String> _USER_FULLNAME = new ThreadLocal<String>();

    public static String getUserId() {
        return _USER_ID.get();
    }

    public static String getUserName() {
        return _USER_FULLNAME.get();
    }

    public static void setContext(String userId, String userName) {
        _USER_ID.set(userId);
        _USER_FULLNAME.set(userName);
    }
}
