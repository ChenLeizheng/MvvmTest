package com.landleaf.everyday.gof;

import com.landleaf.everyday.gof.proxy.IUserController;
import com.landleaf.everyday.gof.proxy.UserController;
import com.landleaf.everyday.gof.proxy.UserControllerProxy;

/**
 * 代理模式：它在不改变原始类（或叫被代理类）代码的情况下，通过引入代理类来给原始类附加功能。
 * 代理类 UserControllerProxy 和原始类 UserController 实现相同的接口 IUserController。UserController 类只负责业务功能。
 * 代理类 UserControllerProxy 负责在业务代码执行前后附加其他逻辑代码，并通过委托的方式调用原始类来执行业务代码。
 * 对于外部类的扩展，我们一般都是采用继承的方式。我们让代理类继承原始类，然后扩展附加功能。
 *
 * 动态代理（Dynamic Proxy），就是我们不事先为每个原始类编写代理类，而是在运行的时候，动态地创建原始类对应的代理类，然后在系统中用代理类替换掉原始类。
 */
public class ProxyDesignPattern {

    public void test(){
        //UserControllerProxy使用举例
        // 因为原始类和代理类实现相同的接口，是基于接口而非实现编程
        // 将UserController类对象替换为UserControllerProxy类对象，不需要改动太多代码
        IUserController userController = new UserControllerProxy(new UserController());
    }
}
