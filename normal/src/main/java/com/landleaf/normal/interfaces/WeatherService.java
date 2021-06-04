package com.landleaf.normal.interfaces;


import com.landleaf.normal.bean.ResponseWeather;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Lei on 2017/12/26.
 */

public interface WeatherService {
    @POST("Langshi/ds/android/weather4")
    Call<ResponseWeather> getWeather(@Body String deviceId);
}
