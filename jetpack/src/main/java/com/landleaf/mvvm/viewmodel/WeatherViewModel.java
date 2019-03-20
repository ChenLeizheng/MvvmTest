package com.landleaf.mvvm.viewmodel;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.landleaf.mvvm.bean.HeWeather;
import com.landleaf.mvvm.bean.Weather;
import com.landleaf.mvvm.data.Constant;
import com.landleaf.mvvm.data.Resource;
import com.landleaf.mvvm.data.WeatherService;
import com.landleaf.mvvm.util.CoolWeatherExecutors;
import com.landleaf.mvvm.util.HttpUtil;
import com.landleaf.mvvm.util.SharedPreferenceUtil;

import java.io.IOException;
import java.util.concurrent.Executors;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherViewModel extends ViewModel {

    public MutableLiveData<Resource<Weather>> getCacheWeather(Context context,String weatherId){
        MutableLiveData<Resource<Weather>> liveData = new MutableLiveData<>();
        Weather cachedWeatherInfo = SharedPreferenceUtil.getInstance().getCachedWeatherInfo(context);
        if (cachedWeatherInfo == null){
            liveData.postValue(new Resource<Weather>(Resource.LOADING,null,null));
            requestWeather(context,weatherId,liveData);
        }else {
            liveData.setValue(new Resource<Weather>(Resource.SUCCESS,cachedWeatherInfo,null));
        }
        return liveData;
    }

    public void saveWeatherInfo(Context context,Weather weather){
        Gson gson = new Gson();
        String json = gson.toJson(weather);
        SharedPreferenceUtil.getInstance().cacheWeatherInfo(context,json);
    }

    public void requestWeather(final Context context, String weatherId, final MutableLiveData<Resource<Weather>> liveData){
        Call<HeWeather> weather = HttpUtil.getInstance().getWeatherService().getWeather(weatherId, Constant.KEY);
        weather.enqueue(new Callback<HeWeather>() {
            @Override
            public void onResponse(Call<HeWeather> call, Response<HeWeather> response) {
                HeWeather heWeather = response.body();
                final Weather weatherInfo = heWeather.getList().get(0);
                liveData.postValue(new Resource<Weather>(Resource.SUCCESS,weatherInfo,null));
                Log.d("WeatherViewModel", "onResponse:" + weatherInfo);
                CoolWeatherExecutors.diskIO.execute(new Runnable() {
                    @Override
                    public void run() {
                        saveWeatherInfo(context,weatherInfo);
                    }
                });
            }

            @Override
            public void onFailure(Call<HeWeather> call, Throwable t) {
                Log.d("WeatherViewModel", "onFailure:"+t.toString());
                liveData.postValue(new Resource<Weather>(Resource.ERROR,null,"天气加载失败!"));
            }
        });
    }

    public MutableLiveData<Resource<String>> getBingPic(){
        final MutableLiveData<Resource<String>> liveData = new MutableLiveData<>();
        WeatherService weatherService = HttpUtil.getInstance().getWeatherService();
        Call<String> bingPic = weatherService.getBingPic();
        bingPic.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String url = response.body();
                liveData.postValue(new Resource<String>(Resource.SUCCESS,url,null));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("WeatherViewModel", "getBingPic:"+t.toString());
                liveData.postValue(new Resource<String>(Resource.ERROR,null,"图片加载失败！"));
            }
        });
        return liveData;
    }
}
