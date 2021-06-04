package com.landleaf.everyday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.landleaf.everyday.adapter.SceneAdapter;
import com.landleaf.everyday.text.TopAlignSpan;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private MyHandler myHandler;
    private ServerManager mServerManager;

    @Inject
    Demo demo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mServerManager = new ServerManager(this);
        mServerManager.register();
        mServerManager.startServer();
        myHandler = new MyHandler(this);
        Message msg = Message.obtain();
        msg.what = 0;
        myHandler.sendMessage(msg);
        ImageView ivClick = findViewById(R.id.ivClick);
        final TextView tvDelay = findViewById(R.id.tvDelay);
        ivClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showLvWangTip(MainActivity.this);
                tvDelay.setBackground(null);
                display();
            }
        });

        tvDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "aaa", Toast.LENGTH_SHORT).show();
            }
        });

        //将依赖注入
        DaggerMainActivityComponent.builder().build().inject(this);
        if (demo != null){
            demo.work();
        }

        SpannableString spannableString = new SpannableString("22.8" + "℃");
        SuperscriptSpan superscriptSpan = new TopAlignSpan();
        spannableString.setSpan(superscriptSpan, spannableString.length()-1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvDelay.setText(spannableString);

        SpannableString ss=new SpannableString("22.8" + "℃");
        RelativeSizeSpan relativeSizeSpan=new RelativeSizeSpan(0.5f);
        ss.setSpan(relativeSizeSpan,spannableString.length()-1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDelay.setText(ss);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ArrayList<String> list = new ArrayList<>();
        list.add("在家");
        list.add("在家2");
        list.add("在家3");
        list.add("在家4");
        list.add("在家5");
        list.add("在家6");
        list.add("在家6");
        list.add("在家6");
        list.add("在家6");
        list.add("在家6");
        list.add("在家6");
        list.add("在家6");
        list.add("在家6");
        list.add("在家6");
        list.add("在家6");
        list.add("在家6");
        list.add("在家6");
        list.add("在家6");
        SceneAdapter sceneAdapter = new SceneAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        recyclerView.setAdapter(sceneAdapter);
    }

    boolean showTips = false;
    public void handlerData(){
        showLvWangTip(this);
    }

    private Disposable disposable;
    private void display(){
        Log.d("TAG", "display");
        if (disposable!=null && !disposable.isDisposed()){
            disposable.dispose();
        }
        disposable = Single.timer(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d("TAG", Thread.currentThread().getName());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("TAG", throwable.toString());
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

    public void showLvWangTip(Activity context){
        //获取当前Activity的Window的decorView作为测量的父布局
        FrameLayout decorView = (FrameLayout) context.getWindow().getDecorView();
//填充获得自己的popwindow的contentView
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_lvwnag_tips, decorView,false);
//构造父类的withSpec和heightSpec
        int withSpec = View.MeasureSpec.makeMeasureSpec(decorView.getMeasuredWidth(), View.MeasureSpec.AT_MOST);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(decorView.getMeasuredHeight(), View.MeasureSpec.AT_MOST);
//真正的提前计算内容高度
        contentView.measure(withSpec, heightSpec);
//设置PopWindow的contentView的计算得出的实际高度
        final PopupWindow popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,contentView.getLayoutParams().height);
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_lvwnag_tips, null, false);
//        final PopupWindow popupWindow = new PopupWindow(view, view.getHeight(), view.getHeight(),false);
////        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//        TextView tvDelay = view.findViewById(R.id.tvDelay);
//        TextView tvHandler = view.findViewById(R.id.tvHandler);
//        tvDelay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//            }
//        });
//        tvHandler.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//            }
//        });
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
        mServerManager.stopServer();
    }

    public void onServerStart(String ip) {
        Log.d("MainActivity", "onServerStart: "+ip);
    }

    public void onServerError(String error) {
        Log.d("MainActivity", "onServerError: "+error);
    }

    public void onServerStop() {
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
