package com.example.utilemodule;

import android.util.Log;

import static com.example.commontmodule.commit.Comment.IS_DEBUG;

public class LogToot {
    private final static boolean isDebug = IS_DEBUG;

    private static final String TAG = LogToot.class.getName();

    public static void i(String msg) {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (isDebug)
            Log.v(TAG, msg);
    }

    public static void i(String TAG, String msg) {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String TAG, String msg) {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String TAG, String msg) {
        if (isDebug)
            Log.v(TAG, msg);
    }

}
