package com.landleaf.everyday.factory;

/**
 * 简单工厂 一个工厂对应多个对象
 */
public class SimpleFactory {

    public static Computer createComputer(String type){
        Computer mComputer=null;
        if ("hp".equals(type)){
            mComputer = new HpComputer();
        }
        if ("lenovo".equals(type)){
            mComputer = new LenovoComputer();
        }
        return mComputer;
    }
}
