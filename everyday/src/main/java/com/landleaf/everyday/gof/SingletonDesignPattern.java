package com.landleaf.everyday.gof;

/**
 * 单例设计模式  一个类只允许创建一个对象（或者实例）
 */
public class SingletonDesignPattern {

    /**
     * 饿汉式
     * 在类加载的时候，instance 静态实例就已经创建并初始化好了，所以，instance 实例的创建过程是线程安全的。
     * 不过，这样的实现方式不支持延迟加载（在真正用到 IdGenerator 的时候，再创建实例）
     */
    private static final SingletonDesignPattern instance1 = new SingletonDesignPattern();
    public static SingletonDesignPattern getInstance1(){
        return instance1;
    }


    /**
     * 懒汉式  懒汉式相对于饿汉式的优势是支持延迟加载。
     * 不过懒汉式的缺点也很明显，我们给 getInstance() 这个方法加了一把大锁（synchronzed），
     * 导致这个函数的并发度很低。量化一下的话，并发度是 1，也就相当于串行操作了。而这个函数是在单例使用期间，
     * 一直会被调用。如果这个单例类偶尔会被用到，那这种实现方式还可以接受。但是，如果频繁地用到，那频繁加锁、
     * 释放锁及并发度低等问题，会导致性能瓶颈，这种实现方式就不可取了。
     */
    private static SingletonDesignPattern instance2;
    public static synchronized SingletonDesignPattern getInstance2(){
        if (instance2 == null){
            instance2 = new SingletonDesignPattern();
        }
        return instance2;
    }

    /**
     * 双重检测
     * 饿汉式不支持延迟加载，懒汉式有性能问题，不支持高并发。那我们再来看一种既支持延迟加载、
     * 又支持高并发的单例实现方式，也就是双重检测实现方式。在这种实现方式中，只要 instance 被创建之后，
     * 即便再调用 getInstance() 函数也不会再进入到加锁逻辑中了。所以，这种实现方式解决了懒汉式并发度低的问题。
     */
    public static SingletonDesignPattern getInstance(){
        if (instance2 == null){
            synchronized (SingletonDesignPattern.class){
                if (instance2 == null){
                    instance2 = new SingletonDesignPattern();
                }
            }
        }
        return instance2;
    }


    /**
     * 静态内部类
     * SingletonHolder 是一个静态内部类，当外部类 IdGenerator 被加载的时候，并不会创建 SingletonHolder 实例对象。
     * 只有当调用 getInstance() 方法时，SingletonHolder 才会被加载，这个时候才会创建 instance。
     * instance 的唯一性、创建过程的线程安全性，都由 JVM 来保证。所以，这种实现方法既保证了线程安全，又能做到延迟加载。
     */
    private static class SingletonHolder{
        private static SingletonDesignPattern instance = new SingletonDesignPattern();
    }

    public static SingletonDesignPattern getInstance3(){
        return SingletonHolder.instance;
    }

    /**
     * 枚举
     * 最后，我们介绍一种最简单的实现方式，基于枚举类型的单例实现。这种实现方式通过 Java 枚举类型本身的特性，
     * 保证了实例创建的线程安全性和实例的唯一性。
     */
    public enum IdGenerator {
        INSTANCE;
    }
}
