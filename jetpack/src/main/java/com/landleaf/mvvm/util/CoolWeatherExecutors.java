package com.landleaf.mvvm.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoolWeatherExecutors {
    public static ExecutorService diskIO = Executors.newSingleThreadExecutor();
    public static ExecutorService networkIO = Executors.newFixedThreadPool(3);
}
