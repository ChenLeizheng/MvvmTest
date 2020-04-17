package com.landleaf.everyday.factory;

public abstract class ComputerFactory {

    public abstract <T extends Computer> T createComputer(Class<T> clazz);
}
