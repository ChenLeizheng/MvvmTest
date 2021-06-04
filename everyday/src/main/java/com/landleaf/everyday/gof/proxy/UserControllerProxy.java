package com.landleaf.everyday.gof.proxy;

public class UserControllerProxy implements IUserController {

    private UserController userController;

    public UserControllerProxy(UserController userController) {
        this.userController = userController;
    }

    @Override
    public String login(String name, String phone) {
        long startTimestamp = System.currentTimeMillis();
        //委托
        userController.login(name,phone);
        long endTimestamp = System.currentTimeMillis();
        //计时统计逻辑处理
        return null;
    }

    @Override
    public String register(String name, String phone) {
        long startTimestamp = System.currentTimeMillis();
        //委托
        userController.register(name,phone);
        long endTimestamp = System.currentTimeMillis();
        //计时统计逻辑处理
        return null;
    }
}
