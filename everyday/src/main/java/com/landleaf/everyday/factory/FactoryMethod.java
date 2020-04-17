package com.landleaf.everyday.factory;

public class FactoryMethod extends ComputerFactory {
    @Override
    public <T extends Computer> T createComputer(Class<T> clazz) {
        Computer computer = null;
        String name = clazz.getName();
        //通过反射获取对应的对象
        try {
            computer = (Computer) Class.forName(name).newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (T) computer;
    }
}
