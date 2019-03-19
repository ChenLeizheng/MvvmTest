package com.landleaf.mvvm.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.landleaf.mvvm.data.PlaceService;
import com.landleaf.mvvm.data.WeatherService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HttpUtil {

    private static final String BASE_URL = "http://guolin.tech/";
    private Retrofit mRetrofit;
    private PlaceService mPlaceService;
    private WeatherService mWeatherService;

    private HttpUtil(){}

    public static HttpUtil getInstance(){
        return ViewHolder.instance;
    }

    public Retrofit getRetrofit(){
        if (mRetrofit != null){
            return mRetrofit;
        }
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                //设置内容格式,这种对应的数据返回值是String类型
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public PlaceService getPlaceService(){
        if (mPlaceService == null){
            mPlaceService = getRetrofit().create(PlaceService.class);
        }
        return mPlaceService;

    }

    public WeatherService getWeatherService(){
        if (mWeatherService == null){
            mWeatherService = getRetrofit().create(WeatherService.class);
        }
        return mWeatherService;
    }

    static class ViewHolder{
        static HttpUtil instance = new HttpUtil();
    }
}
