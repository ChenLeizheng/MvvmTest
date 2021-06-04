package com.landleaf.testapp;

import android.util.Log;

import java.util.concurrent.Callable;

public class MyThread implements Callable<String> {
    @Override
    public String call() throws Exception {
        for(int x = 0;x < 20;x ++) {
            System.out.println("卖票，x = "+x);
        }
        return "售空";
    }
}
