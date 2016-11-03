package com.sap.it.sr.util;

public class SessionHolder {
    public static final String USER_ID = "USER_ID";
//    public static final String LOCALE = "LOCALE";

    private static final ThreadLocal<String> _USER_ID = new ThreadLocal<String>();
//    private static final ThreadLocal<String> _LOCALE = new ThreadLocal<String>();

    public static String getUserId() {
        return _USER_ID.get();
    }

//    public static String getLocale() {
//        String local = _LOCALE.get();
//        return local == null ? "en_US" : local;
//    }

    public static void setContext(String userId) {
        _USER_ID.set(userId);
//        _LOCALE.set(locale);
    }
}
