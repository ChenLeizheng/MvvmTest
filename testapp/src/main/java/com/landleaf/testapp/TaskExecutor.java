package com.landleaf.testapp;

import android.os.Process;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * corePoolSize: 线程池的核心线程数，默认情况下， 核心线程会在线程池中一直存活， 即使处于闲置状态. 但如果将allowCoreThreadTimeOut设置为true的话, 那么核心线程也会有超时机制， 在keepAliveTime设置的时间过后， 核心线程也会被终止.
 * maximumPoolSize: 最大的线程数， 包括核心线程， 也包括非核心线程， 在线程数达到这个值后，新来的任务将会被阻塞.
 * keepAliveTime: 超时的时间， 闲置的非核心线程超过这个时长，讲会被销毁回收， 当allowCoreThreadTimeOut为true时，这个值也作用于核心线程.
 * unit：超时时间的时间单位.
 * workQueue：线程池的任务队列， 通过execute方法提交的runnable对象会存储在这个队列中.
 * threadFactory: 线程工厂, 为线程池提供创建新线程的功能.
 * handler: 任务无法执行时，回调handler的rejectedExecution方法来通知调用者.
 * <p>
 * 作者：ahking17
 * 链接：http://www.jianshu.com/p/86eb8ea62141
 * 來源：简书
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
public class TaskExecutor {
    private static final String TAG = "TaskExecutor";
    /**
     * Android AsyncTask 核心配置参数
     * 核心线程数 = CPU数+1
     * 最大线程数 = CPU数*2 + 1
     * 非核心线程的超时时间为1秒
     * 任务队列的容量为128
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();//CPU数
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    //FixedThreadPool 特点：只有核心线程数，并且没有超时机制，因此核心线程即使闲置时，也不会被回收，因此能更快的响应外界的请求.
    //CachedThreadPool 特点：没有核心线程，非核心线程数量没有限制， 超时为60秒.适用于执行大量耗时较少的任务，当线程闲置超过60秒时就会被系统回收掉，当所有线程都被系统回收后，它几乎不占用任何系统资源.
    //ScheduledThreadPool 特点：核心线程数是固定的，非核心线程数量没有限制， 没有超时机制.主要用于执行定时任务和具有固定周期的重复任务.
    //SingleThreadExecutor 特点：只有一个核心线程，并没有超时机制.意义在于统一所有的外界任务到一个线程中， 这使得在这些任务之间不需要处理线程同步的问题.

    private static TaskExecutor instance;

    private PriorityThreadFactory priorityThreadFactory;

    private ExecutorService executorService;

    private Map<String, ExecutorService> executorServiceMap = new ConcurrentHashMap<>();

    private Map<String, ScheduledExecutorService> scheduledExecutorServiceMap = new ConcurrentHashMap<>();

    private TaskExecutor() {
        //默认线程工程类
        priorityThreadFactory = new PriorityThreadFactory(TAG, Process.THREAD_PRIORITY_BACKGROUND);

    }

    //保证线程安全
    public static synchronized TaskExecutor getInstance() {
        if (instance == null) {
            instance = new TaskExecutor();
        }
        return instance;
    }

    //取消执行FixJob
    public void removeSingleFixJob(String taskName) {
        if (executorServiceMap.containsKey(taskName)) {
            executorServiceMap.get(taskName).shutdownNow();
            executorServiceMap.get(taskName).shutdown();
            executorServiceMap.remove(taskName);
        }
    }

    //指定单线程的线程池
    public Future<?> submitSingleFixJob(Runnable r, String taskName, int priority) {
        priorityThreadFactory.setName(r.getClass(), taskName);
        priorityThreadFactory.setPriority(priority);
        if (!executorServiceMap.containsKey(taskName)) {
            executorService = Executors.newSingleThreadExecutor(priorityThreadFactory);
            executorServiceMap.put(taskName, executorService);
        } else {
            executorService = executorServiceMap.get(taskName);
            removeSingleFixJob(taskName);
        }
        return executorService.submit(r);
    }

    //指定单线程的线程池
    public Future<?> submitSingleFixJob(Callable<?> r, String taskName, int priority) {
        priorityThreadFactory.setName(r.getClass(), taskName);
        priorityThreadFactory.setPriority(priority);
        if (!executorServiceMap.containsKey(taskName)) {
            executorService = Executors.newSingleThreadExecutor(priorityThreadFactory);
            executorServiceMap.put(taskName, executorService);
        } else {
            executorService = executorServiceMap.get(taskName);
            removeSingleFixJob(taskName);
        }
        return executorService.submit(r);
    }

    //提交单线程任务,任务名必须保持唯一
    /**
     * @param r        提交的线程
     * @param taskName 线程任务名
     * @param priority 线程优先级
     * @param childThreadName    线程子任务名
     * @return 线程运行结果
     */
    public Future<?> submitSingleJob(Runnable r, String taskName, int priority, String childThreadName) {
        priorityThreadFactory.setName(r.getClass(), childThreadName);
        priorityThreadFactory.setPriority(priority);
        if (!executorServiceMap.containsKey(taskName)) {
            executorService = Executors.newSingleThreadExecutor(priorityThreadFactory);
            executorServiceMap.put(taskName, executorService);
        } else {
            executorService = executorServiceMap.get(taskName);
        }
        return executorService.submit(r);
    }

    //提交单线程任务

    /**
     * @param r        提交的线程
     * @param taskName 线程任务名
     * @param priority 线程优先级
     * @param param    线程子任务名
     * @return 线程运行结果
     */
    public Future<?> submitSingleJob(Callable<?> r, String taskName, int priority, String param) {
        priorityThreadFactory.setName(taskName, param);
        priorityThreadFactory.setPriority(priority);
        if (!executorServiceMap.containsKey(taskName)) {
            executorService = Executors.newSingleThreadExecutor(priorityThreadFactory);
            executorServiceMap.put(taskName, executorService);
        } else {
            executorService = executorServiceMap.get(taskName);
        }
        return executorService.submit(r);
    }

    //提交定时执行任务
    public Future<?> submitScheduleJob(Runnable r, int priority, String taskName, long init, long delay, TimeUnit timeUnit, String param) {
        ScheduledExecutorService executors;
        priorityThreadFactory.setName(taskName, param);
        priorityThreadFactory.setPriority(priority);
        if (!scheduledExecutorServiceMap.containsKey(taskName)) {
            executors = Executors.newScheduledThreadPool(CORE_POOL_SIZE, priorityThreadFactory);
            scheduledExecutorServiceMap.put(taskName, executors);
        } else {
            executors = scheduledExecutorServiceMap.get(taskName);
        }
        return executors.scheduleAtFixedRate(r, init, delay, timeUnit);
    }

    //移除定时任务
    public void removeScheduleJob(String taskName) {
        if (scheduledExecutorServiceMap.containsKey(taskName)) {
            ScheduledExecutorService service = scheduledExecutorServiceMap.get(taskName);
            service.shutdown();
            service.shutdownNow();
            scheduledExecutorServiceMap.remove(taskName);
        }
    }
}