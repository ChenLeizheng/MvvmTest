package com.landleaf.mvvm.data;

import com.landleaf.mvvm.bean.HeWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @Query
 * 向 url 追加参数
 * String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
 */

public interface WeatherService {

    @GET("api/weather")
    public Call<HeWeather> getWeather(@Query("cityid")String weatherId, @Query("key")String key);

    @GET("api/bing_pic")
    public Call<String> getBingPic();
}
