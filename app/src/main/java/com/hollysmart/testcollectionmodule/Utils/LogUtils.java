package com.hollysmart.testcollectionmodule.Utils;

import android.util.Log;

public class LogUtils {

    public static String TAG;

    public static void d(String log) {
        log(TAG,log);
    }

    public static void d(String TAG, String log) {
        log(TAG,log);
    }

    private static void log(String TAG, String log) {
        if(TAG==null) TAG = "";
        Log.e(TAG, log);
    }
}
