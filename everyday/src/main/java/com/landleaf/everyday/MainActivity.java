package com.landleaf.everyday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        myHandler = new MyHandler(this);
        Message msg = Message.obtain();
        msg.what = 0;
        myHandler.sendMessage(msg);
        Button btError = findViewById(R.id.btError);
        Button btError1 = findViewById(R.id.btError1);
        btError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        for (int i = 0; i < 3; i++) {
            //短期发送多个sticky只有最后一个会被收到,除非发送时已经注册了
            EventBus.getDefault().postSticky("hello" + i);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getMessage(String msg) {
        Log.d("MainActivity", msg);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //参数为null时,all callbacks and messages will be removed
        myHandler.removeCallbacksAndMessages(null);
    }

    static class MyHandler extends Handler {

        WeakReference<MainActivity> mContext;

        public MyHandler(MainActivity context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MainActivity context = mContext.get();
            context.showToast(msg.toString());
        }
    }
}
