package com.landleaf.mvvm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.landleaf.mvvm.R;
import com.landleaf.mvvm.bean.HeWeather;
import com.landleaf.mvvm.bean.Weather;
import com.landleaf.mvvm.data.WeatherService;
import com.landleaf.mvvm.util.DbUtil;
import com.landleaf.mvvm.util.SharedPreferenceUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DbUtil.getInstance().initGreenDao(this,"city.db");
        Weather cachedWeatherInfo = SharedPreferenceUtil.getInstance().getCachedWeatherInfo(this);
        if (cachedWeatherInfo!=null){
            startActivity(new Intent(this,WeatherActivity.class));
            finish();
        }

//        //声明日志类
//        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//        //设定日志级别
//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        //自定义OkHttpClient
//        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
//        //添加拦截器
//        okHttpClient.addInterceptor(httpLoggingInterceptor);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://guolin.tech/")
//                .client(okHttpClient.build())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        WeatherService weatherService = retrofit.create(WeatherService.class);
//        retrofit2.Call<HeWeather> call1 = weatherService.getWeather("CN101010100", "79a79674ca6149c0a6a0900eadf99576");
//        call1.enqueue(new retrofit2.Callback<HeWeather>() {
//            @Override
//            public void onResponse(retrofit2.Call<HeWeather> call, retrofit2.Response<HeWeather> response) {
//                Log.d("MainActivity", "response.body():" + response.body());
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<HeWeather> call, Throwable t) {
//                Log.d("MainActivity", t.toString());
//            }
//        });
//
//        Request build = new Request.Builder().url("http://guolin.tech/api/weather?cityid=CN101010100&key=79a79674ca6149c0a6a0900eadf99576").build();
//        Call call = okHttpClient.build().newCall(build);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("MainActivity", e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String string = response.body().string();
//                Gson gson = new Gson();
//
//                HeWeather heWeather = gson.fromJson(string, HeWeather.class);
//                Log.d("MainActivity", "heWeather:" + heWeather);
//            }
//        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
