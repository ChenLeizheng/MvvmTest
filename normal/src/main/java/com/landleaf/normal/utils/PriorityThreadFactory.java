package com.landleaf.normal.utils;

import android.os.Process;

import androidx.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

/**
 * A thread factory that create threads with a given thread priority
 *
 * @author jony
 * @version 1.0
 */
class PriorityThreadFactory implements ThreadFactory {

    private String name;
    private int priority;

    PriorityThreadFactory(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setName(Class<?> name, String param) {
        this.name = name.getSimpleName() + "-" + param;
    }

    void setName(String name, String param) {
        this.name = name + "-" + param;
    }

    public int getPriority() {
        return priority;
    }

    void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        return new Thread(r, name) {
            @Override
            public void run() {
                // 设置线程的优先级
                Process.setThreadPriority(priority);
                super.run();
            }
        };
    }
}
