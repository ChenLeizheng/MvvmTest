package com.landleaf.normal.utils;

import android.os.Process;

import java.util.Map;
import java.util.Objects;
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
 */
public class ThreadPoolManager {
    private static final String TAG = ThreadPoolManager.class.getSimpleName();
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

    private static ThreadPoolExecutor fixedThreadPool;//定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
    private static ThreadPoolExecutor cachedThreadPool;//缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程

    //FixedThreadPool 特点：只有核心线程数，并且没有超时机制，因此核心线程即使闲置时，也不会被回收，因此能更快的响应外界的请求.
    //CachedThreadPool 特点：没有核心线程，非核心线程数量没有限制， 超时为60秒.适用于执行大量耗时较少的任务，当线程闲置超过60秒时就会被系统回收掉，当所有线程都被系统回收后，它几乎不占用任何系统资源.
    //ScheduledThreadPool 特点：核心线程数是固定的，非核心线程数量没有限制， 没有超时机制.主要用于执行定时任务和具有固定周期的重复任务.
    //SingleThreadExecutor 特点：只有一个核心线程，并没有超时机制.意义在于统一所有的外界任务到一个线程中， 这使得在这些任务之间不需要处理线程同步的问题.

    private static ThreadPoolManager instance;

    private PriorityThreadFactory priorityThreadFactory;

    private ExecutorService executorService;

    private Map<String, ExecutorService> executorServiceMap = new ConcurrentHashMap<>();

    private Map<String, ScheduledExecutorService> scheduledExecutorServiceMap = new ConcurrentHashMap<>();

    private ThreadPoolManager() {
        //默认线程工程类
        priorityThreadFactory = new PriorityThreadFactory(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        //创建默认线程池
        fixedThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        cachedThreadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    //保证线程安全
    public static synchronized ThreadPoolManager getInstance() {
        if (instance == null) {
            instance = new ThreadPoolManager();
        }
        return instance;
    }

    //取消执行FixJob
    public void removeSingleFixJob(String taskName) {
        if (executorServiceMap.containsKey(taskName)) {
            Objects.requireNonNull(executorServiceMap.get(taskName)).shutdownNow();
            Objects.requireNonNull(executorServiceMap.get(taskName)).shutdown();
            executorServiceMap.remove(taskName);
        }
    }

    //指定单线程的线程池
    public Future submitSingleFixJob(Runnable r, String taskName, int priority) {
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
    public Future submitSingleFixJob(Callable<?> r, String taskName, int priority) {
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


    //取消执行FixJob
    public boolean removeFixJob(Runnable task) {
        return fixedThreadPool.remove(task);
    }

    //控制并发任务
    public Future submitFixJob(Runnable r, String param, int priority) {
        priorityThreadFactory.setName(r.getClass(), param);
        priorityThreadFactory.setPriority(priority);
        fixedThreadPool.setThreadFactory(priorityThreadFactory);
        return fixedThreadPool.submit(r);
    }

    //取消执行CacheJob
    public boolean removeJob(Runnable task) {
        return cachedThreadPool.remove(task);
    }

    //执行异步任务调用该方法

    /**
     * @param r        提交的线程
     * @param priority 线程优先级
     * @param param    线程子任务名
     * @return 线程运行结果
     */
    public Future submitJob(Runnable r, int priority, String param) {
        priorityThreadFactory.setName(r.getClass(), param);
        priorityThreadFactory.setPriority(priority);
        cachedThreadPool.setThreadFactory(priorityThreadFactory);
        return cachedThreadPool.submit(r);
    }

    //执行异步任务调用该方法

    /**
     * @param r        提交的线程
     * @param priority 线程优先级
     * @param param    线程子任务名
     * @return 线程运行结果
     */
    public Future submitJob(Callable<?> r, int priority, String param) {
        priorityThreadFactory.setName(param);
        priorityThreadFactory.setPriority(priority);
        cachedThreadPool.setThreadFactory(priorityThreadFactory);
        return cachedThreadPool.submit(r);
    }

    //提交单线程任务,任务名必须保持唯一

    /**
     * @param r               提交的线程
     * @param taskName        线程任务名
     * @param priority        线程优先级
     * @param childThreadName 线程子任务名
     * @return 线程运行结果
     */
    public Future submitSingleJob(Runnable r, String taskName, int priority, String childThreadName) {
        priorityThreadFactory.setName(r.getClass(), childThreadName);
        priorityThreadFactory.setPriority(priority);
        if (!executorServiceMap.containsKey(taskName)) {
            executorService = Executors.newSingleThreadExecutor(priorityThreadFactory);
            executorServiceMap.put(taskName, executorService);
        } else {
            executorService = executorServiceMap.get(taskName);
        }
        return Objects.requireNonNull(executorService).submit(r);
    }

    //提交单线程任务

    /**
     * @param r        提交的线程
     * @param taskName 线程任务名
     * @param priority 线程优先级
     * @param param    线程子任务名
     * @return 线程运行结果
     */
    public Future submitSingleJob(Callable<?> r, String taskName, int priority, String param) {
        priorityThreadFactory.setName(taskName, param);
        priorityThreadFactory.setPriority(priority);
        if (!executorServiceMap.containsKey(taskName)) {
            executorService = Executors.newSingleThreadExecutor(priorityThreadFactory);
            executorServiceMap.put(taskName, executorService);
        } else {
            executorService = executorServiceMap.get(taskName);
        }
        return Objects.requireNonNull(executorService).submit(r);
    }

    //提交定时执行任务scheduleAtFixedRate 方法，顾名思义，它的方法名称的意思是：已固定的频率来执行某项计划(任务)
    public Future submitScheduleJob(Runnable r, int priority, String taskName, long init, long delay, TimeUnit timeUnit, String param) {
        ScheduledExecutorService executors;
        priorityThreadFactory.setName(taskName, param);
        priorityThreadFactory.setPriority(priority);
        if (!scheduledExecutorServiceMap.containsKey(taskName)) {
            executors = Executors.newScheduledThreadPool(CORE_POOL_SIZE, priorityThreadFactory);
            scheduledExecutorServiceMap.put(taskName, executors);
        } else {
            executors = scheduledExecutorServiceMap.get(taskName);
        }
        return Objects.requireNonNull(executors).scheduleAtFixedRate(r, init, delay, timeUnit);
    }

    //即无论某个任务执行多长时间，等执行完了，我再延迟指定的时间。也就是第二个方法，它受计划执行时间的影响
    public Future submitScheduleWithFixedDealyJob(Runnable r, int priority, String taskName, long init, long delay, TimeUnit timeUnit, String param) {
        ScheduledExecutorService executors;
        priorityThreadFactory.setName(taskName, param);
        priorityThreadFactory.setPriority(priority);
        if (!scheduledExecutorServiceMap.containsKey(taskName)) {
            executors = Executors.newScheduledThreadPool(CORE_POOL_SIZE, priorityThreadFactory);
            scheduledExecutorServiceMap.put(taskName, executors);
        } else {
            executors = scheduledExecutorServiceMap.get(taskName);
        }
        return Objects.requireNonNull(executors).scheduleWithFixedDelay(r, init, delay, timeUnit);
    }

    //移除定时任务
    public void removeScheduleJob(String taskName) {
        if (scheduledExecutorServiceMap.containsKey(taskName)) {
            ScheduledExecutorService service = scheduledExecutorServiceMap.get(taskName);
            Objects.requireNonNull(service).shutdown();
            service.shutdownNow();
            scheduledExecutorServiceMap.remove(taskName);
        }
    }

    public void removeAllScheduleJob(){
        for (String taskName : scheduledExecutorServiceMap.keySet()) {
            removeScheduleJob(taskName);
        }
    }
}