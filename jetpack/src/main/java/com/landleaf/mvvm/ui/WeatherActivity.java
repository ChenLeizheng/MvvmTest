package com.landleaf.mvvm.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.landleaf.mvvm.R;
import com.landleaf.mvvm.bean.Forecast;
import com.landleaf.mvvm.bean.Weather;
import com.landleaf.mvvm.data.Constant;
import com.landleaf.mvvm.data.Resource;
import com.landleaf.mvvm.util.SharedPreferenceUtil;
import com.landleaf.mvvm.viewmodel.WeatherViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class WeatherActivity extends AppCompatActivity {

    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    private ScrollView weatherLayout;
    private Button navButton;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private String weatherId;
    public WeatherViewModel weatherViewModel;
    public MutableLiveData<Resource<Weather>> liveData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置沉浸式状态栏
        if (Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView();
        weatherId = getIntent().getStringExtra(Constant.WEATHER_ID);
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        liveData = weatherViewModel.getCacheWeather(this, weatherId);
        liveData.observe(this, new Observer<Resource<Weather>>() {
            @Override
            public void onChanged(Resource<Weather> weatherResource) {
                swipeRefresh.setRefreshing(false);
                if (weatherResource.getStatus() == Resource.LOADING){
                    Toast.makeText(WeatherActivity.this, "loading...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (weatherResource.getStatus() == Resource.ERROR){
                    Toast.makeText(WeatherActivity.this, weatherResource.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                showWeatherInfo(weatherResource.getData());
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                weatherViewModel.requestWeather(WeatherActivity.this,weatherId, liveData);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        MutableLiveData<Resource<String>> bingPic = weatherViewModel.getBingPic();
        bingPic.observe(this, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> stringResource) {
                if (stringResource.getStatus() == Resource.SUCCESS){
                    Glide.with(WeatherActivity.this).load(stringResource.getData()).into(bingPicImg);
                }else {
                    Toast.makeText(WeatherActivity.this, stringResource.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        bingPicImg = findViewById(R.id.bingPicImg);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        weatherLayout = findViewById(R.id.weatherLayout);
        titleCity = findViewById(R.id.titleCity);
        titleUpdateTime = findViewById(R.id.titleUpdateTime);
        degreeText = findViewById(R.id.degreeText);
        weatherInfoText = findViewById(R.id.weatherInfoText);
        forecastLayout = findViewById(R.id.forecastLayout);
        aqiText = findViewById(R.id.aqiText);
        pm25Text = findViewById(R.id.pm25Text);
        comfortText = findViewById(R.id.comfortText);
        carWashText = findViewById(R.id.carWashText);
        sportText = findViewById(R.id.sportText);
        drawerLayout =  findViewById(R.id.drawerLayout);
        navButton =  findViewById(R.id.navButton);
    }

    private void showWeatherInfo(Weather weather) {
        Log.d("WeatherActivity", "weather:" + weather);
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.dateText);
            TextView infoText = view.findViewById(R.id.infoText);
            TextView maxText = view.findViewById(R.id.maxText);
            TextView minText = view.findViewById(R.id.minText);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        aqiText.setText(weather.aqi.city.aqi);
        pm25Text.setText(weather.aqi.city.pm25);
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        //设置全屏显示  上方的时间电量都木有
//        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }
}
