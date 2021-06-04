package com.landleaf.normal.utils;

import android.util.Log;

public class LaunchTime {

    private static final  String TAG = "LaunchTime";
    private static long sTime;
    public static void startRecord(){
        sTime = System.currentTimeMillis();
    }

    public static void endRecord(){
        long cost = System.currentTimeMillis() - sTime;
        Log.d(TAG, "执行耗时：" + cost);
    }
}
