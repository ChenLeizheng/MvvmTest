package com.landleaf.mvvm.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.landleaf.mvvm.bean.Weather;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SharedPreferenceUtil {

    private static String PREFERENCE_NAME = "landleaf";

    private SharedPreferenceUtil(){}

    public static SharedPreferenceUtil getInstance(){
        return ViewHolder.instance;
    }

    public void cacheWeatherInfo(Context context, String weather){
        if (weather!=null){
            SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            sp.edit().putString("weather",weather).apply();
        }
    }

    public Weather getCachedWeatherInfo(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String weatherStr = sp.getString("weather", "");
        if (TextUtils.isEmpty(weatherStr)){
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(weatherStr, Weather.class);
    }

    static class ViewHolder{
        static SharedPreferenceUtil instance = new SharedPreferenceUtil();
    }


    static boolean init = false;
    static String CLASS_QUEUED_WORK = "android.app.QueuedWork";
    static String FIELD_PENDING_FINISHERS = "sPendingWorkFinishers";
    static ConcurrentLinkedQueue<Runnable> sPendingWorkFinishers = null;

    public static void beforeSPBlock(String tag){
        if (!init){
            getPendingWorkFinishers();
            init = true;
        }
        if (sPendingWorkFinishers != null){
            sPendingWorkFinishers.clear();
        }
    }

    static void getPendingWorkFinishers(){
        try {
            Class<?> aClass = Class.forName(CLASS_QUEUED_WORK);
            //获取特定的成员变量
            Field field = aClass.getDeclaredField(FIELD_PENDING_FINISHERS);
            if (field!=null){
                field.setAccessible(true);
                sPendingWorkFinishers  = (ConcurrentLinkedQueue<Runnable>) field.get(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
