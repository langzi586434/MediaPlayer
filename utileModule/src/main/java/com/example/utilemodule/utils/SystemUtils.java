package com.example.utilemodule.utils;

import android.annotation.SuppressLint;

import com.example.utilemodule.LogToot;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class SystemUtils {
    private static ExecutorService cachedThreadPool;

    static void onChangeSystemSetting(boolean b) {
    }

    static void onChangeSystemSetting(boolean b, String prop) {
    }

    private static void initThread() {
        if (null == cachedThreadPool) cachedThreadPool = Executors.newCachedThreadPool();
    }

    public static void setSystemProp(String key, String value) {
        initThread();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                setProp(key, value);
                String prop = getProp(key);
                onChangeSystemSetting(!prop.equals("-1"));
            }
        });
    }

    public static void getSystemProp(String key, String value) {
        initThread();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String prop = getProp(key);
                onChangeSystemSetting(!prop.equals("-1"), prop.equals("-1") ? "null" : prop);
            }
        });
    }

    @SuppressLint("PrivateApi")
    private static void setProp(String key, String value) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Method method = cls.getMethod("set", String.class, String.class);
            method.invoke(null, key, value);
        } catch (Exception e) {
            LogToot.e(e.getMessage());
        }
    }

    @SuppressLint("PrivateApi")
    private static String getProp(String key) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Method method = cls.getMethod("get", String.class, String.class);
            return (String) method.invoke(null, key, "-1");
        } catch (Exception e) {
            LogToot.e(e.getMessage());
        }
        return "-1";
    }

}
