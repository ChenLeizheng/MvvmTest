package com.landleaf.serial;

import android.app.Application;
import android.util.Log;

import com.landleaf.serial.dnake.Rs485Executor;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Rs485Executor.getInstance().setup();
        Log.d("App", "init RS485");
    }
}
