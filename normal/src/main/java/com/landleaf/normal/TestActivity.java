package com.landleaf.normal;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.landleaf.normal.utils.Prefs;
import com.landleaf.normal.utils.ThreadPoolManager;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout rlAirMonitor = findViewById(R.id.rlAirMonitor);
        RelativeLayout rlHvacControl = findViewById(R.id.rlHvacControl);

        rlAirMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TestActivity", "rlAirMonitor");
                ThreadPoolManager.getInstance().submitSingleJob(new Runnable() {

                    @Override
                    public void run() {
                        write();
                    }
                },"readPlc", THREAD_PRIORITY_BACKGROUND, "");
                Log.d("TestActivity", "rlAirMonitor22");
            }
        });

        rlHvacControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TestActivity", "rlHvacControl");
                ThreadPoolManager.getInstance().submitSingleJob(new Runnable() {

                    @Override
                    public void run() {
                        write();
                    }
                },"readPlc", THREAD_PRIORITY_BACKGROUND, "");
                Log.d("TestActivity", "rlHvacControl22");
            }
        });
    }


    private synchronized void write(){
        Log.d("TestActivity", "write");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("TestActivity", "write22");
    }
}
