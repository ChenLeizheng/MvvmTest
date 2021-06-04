package com.landleaf.everyday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.DialogBehavior;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.main.DialogLayout;
import com.landleaf.everyday.widget.AirProgressView2;
import com.landleaf.everyday.widget.CirclePercentView;
import com.landleaf.everyday.widget.CircleTempView;
import com.landleaf.everyday.widget.DialView;
import com.landleaf.everyday.widget.HumitureCircleView;
import com.landleaf.everyday.widget.HumitureCircleView1;
import com.landleaf.everyday.widget.HumitureCircleView2;
import com.landleaf.everyday.widget.RulerView;
import com.landleaf.everyday.widget.TimeViewH;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class TestActivity extends Activity {

    private List<Integer> indexError;
    private AirProgressView2 airProgressView;
    private int temp;
    private TimeViewH timeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Button btError = findViewById(R.id.btError);
        Button btError1 = findViewById(R.id.btError1);
        final RulerView rulePM25Progress = findViewById(R.id.rulePM25Progress);
        airProgressView = findViewById(R.id.airProgressView);
        final DialView dialView = findViewById(R.id.dialView);
        timeView = findViewById(R.id.timeView);
        final CircleTempView cvTempOut = findViewById(R.id.cvTempOut);
        final HumitureCircleView humitureCircleView = findViewById(R.id.humitureCircleView);
        final HumitureCircleView2 humitureCircleView1 = findViewById(R.id.humitureCircleView1);
        btError.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("TestActivity", "longClick");
                getIpByHostName("www.lokowit.com");
                return true;
            }
        });
//        Test test = findViewById(R.id.test);
//
//        TranslateAnimation translateAnimation = new TranslateAnimation(0, -900, 0, 0);
//        translateAnimation.setDuration(10000);
//        translateAnimation.setRepeatCount(Animation.INFINITE);
//        translateAnimation.setRepeatMode(Animation.RESTART);
//        test.setAnimation(translateAnimation);

        btError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rulePM25Progress.setProgress(22);
                airProgressView.setProgress(62.9f,"ug/m³",true,"优");
                dialView.setProgress(300,"m/s",false);
                cvTempOut.setProgress(37,false);
                humitureCircleView.setProgress(25.58f,99.9f);
                humitureCircleView1.setProgress(1);
//                PopUtils.getInstance().showSetTempDialog(TestActivity.this, new DialogCallback() {
//                    @Override
//                    public void onSure(String result) {
//                        Log.d("TestActivity", result);
//                    }
//                });
            }
        });
        btError1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PopUtils.getInstance().showSetDiySceneDialog(TestActivity.this, new DialogCallback() {
//                    @Override
//                    public void onSure(String result) {
//                        Log.d("TestActivity", result);
//                    }
//                });
                Intent intent = new Intent(TestActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }


    public void getIpByHostName(final String hostname) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress address = InetAddress.getByName(hostname);
                    Log.d("TestActivity", address.getHostAddress());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        timeView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        airProgressView.onStop();
        timeView.stop();
    }

    //16个寄存器 ->32byte
    private void checkErrorCode(byte[] data){
        indexError.clear();
        int index = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0){
                continue;
            }
            for (int j = 0; j < 8; j++) {
                index = i%2==0? ((i+1)*8+j+1):((i-1)*8+j+1);
                if ((((data[i]&0xFF)>>>j)&0x01) == 1){
                    indexError.add(index);
                }
            }
        }
    }


}
