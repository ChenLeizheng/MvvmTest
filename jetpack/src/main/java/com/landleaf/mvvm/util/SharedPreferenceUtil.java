package com.landleaf.mvvm.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.landleaf.mvvm.bean.Weather;

public class SharedPreferenceUtil {

    private static String PREFERENCE_NAME = "landleaf";

    private SharedPreferenceUtil(){}

    public static SharedPreferenceUtil getInstance(){
        return ViewHolder.instance;
    }

    public void cacheWeatherInfo(Context context, Weather weather){
        if (weather!=null){
            SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            sp.edit().putString("weather",weather.toString()).apply();
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

}
