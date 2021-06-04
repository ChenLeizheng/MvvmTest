package com.landleaf.mvvm.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.landleaf.mvvm.R;
import com.landleaf.mvvm.TasksViewModel;
import com.landleaf.mvvm.bean.HeWeather;
import com.landleaf.mvvm.bean.Weather;
import com.landleaf.mvvm.data.WeatherService;
import com.landleaf.mvvm.util.DbUtil;
import com.landleaf.mvvm.util.SharedPreferenceUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    String test = "";
    private SharedPreferences sp ;

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
        sp = getSharedPreferences("test",Context.MODE_PRIVATE);
        final TasksViewModel tasksViewModel = ViewModelProviders.of(this).get(TasksViewModel.class);
        final List<String> list = new ArrayList<>();
        list.add("MainActivity");
        tasksViewModel.save(list);
        Log.d("TEST", "tasksViewModel:" + tasksViewModel);
        Log.d("TEST", "==="+tasksViewModel.read().getValue());
        Button btSend = findViewById(R.id.btSend);
        test = sp.getString("test", "");
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //对于检测app是否开启通知，同样是针对4.4以上的系统才有效,4.4以下调用该方法并不会出错，只是全部返回true
                NotificationManagerCompat manager = NotificationManagerCompat.from(MainActivity.this);
                boolean isOpened = manager.areNotificationsEnabled();
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isOpened) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", MainActivity.this.getPackageName());
                    intent.putExtra("app_uid", MainActivity.this.getApplicationInfo().uid);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                    startActivity(intent);
                }



//                MainActivityPermissionsDispatcher.requestPermissionWithPermissionCheck(MainActivity.this);
//                tasksViewModel.save(list);
//                sp.edit().putString("test",test);
//                String result = sp.getString("test", "");
//
//                for (int i = 0; i < 50; i++) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            sp.edit().putString("test",test).apply();
//                            int length1 = test.length();
//                            Log.d("MainActivity", "test.length():" + length1 +test.substring(0,5)+test.substring(length1-5,length1));
//                        }
//                    }).start();
//
//                }
//                for (int i = 0; i < 50; i++) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String result = sp.getString("test", "");
//                            int length = result.length();
//                            Log.d("MainActivity", "test.length():" + length +test.substring(0,5)+test.substring(length-5,length));
//                        }
//                    }).start();
//                }
            }
        });

//        FrameLayout flameLayout = findViewById(R.id.flameLayout);
//        TextView textView = new TextView(this);
//        textView.setText("aaaaa");
//        textView.setGravity(Gravity.BOTTOM|Gravity.END);
//
//        TextView textView1 = new TextView(this);
//        textView1.setText("aaaaabbbbb");
//        textView.setBackgroundColor(Color.YELLOW);
//        FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.gravity = Gravity.BOTTOM;
//        flameLayout.addView(textView,layoutParams1);
//        flameLayout.addView(textView1,layoutParams);
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

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    public void requestPermission(){
        Log.d("MainActivity", "requestPermission");
        Toast.makeText(this, "request", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})//用户选择的不再询问
    public void multiNeverAsk() {
        Log.d("MainActivity", "multiNeverAsk");
        Toast.makeText(this, "已拒绝一个或以上权限，并不再询问", Toast.LENGTH_SHORT).show();
    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    public void multiDenied(){
        Log.d("MainActivity", "multiDenied");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("MainActivity", "onRequestPermissionsResult");
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    //重写onSupportNavigateUp（）方法，目的是将back事件委托出去。若栈中有两个以上Fragment，点击back键就会返回到上一个Fragment。
    @Override
    public boolean onSupportNavigateUp() {
        Log.d("TEST", "onNavigateUp");
        Fragment fragmentById = getSupportFragmentManager().findFragmentById(R.id.chooseAreaFragment);
        return NavHostFragment.findNavController(fragmentById).navigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
