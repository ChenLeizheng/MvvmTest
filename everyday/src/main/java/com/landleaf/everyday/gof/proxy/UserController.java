package com.landleaf.everyday.gof.proxy;

public class UserController implements IUserController {
    @Override
    public String login(String name, String phone) {
        System.out.println("UserController login");
        //处理登录业务逻辑
        return null;
    }

    @Override
    public String register(String name, String phone) {
        //处理注册业务逻辑
        return null;
    }
}
