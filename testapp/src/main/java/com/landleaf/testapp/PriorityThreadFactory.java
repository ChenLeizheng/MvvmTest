package com.landleaf.testapp;

import android.os.Process;

import java.util.concurrent.ThreadFactory;

/**
 * A thread factory that create threads with a given thread priority
 *
 * @author jony
 * @version 1.0
 */
class PriorityThreadFactory implements ThreadFactory {

    private static final String TAG = "PriorityThreadFactory";
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
    public Thread newThread(Runnable r) {
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
