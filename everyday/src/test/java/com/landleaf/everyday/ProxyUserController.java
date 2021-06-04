package com.landleaf.everyday;

import com.landleaf.everyday.gof.proxy.UserController;

public class ProxyUserController extends UserController {

    @Override
    public String login(String name, String phone) {
        System.out.println("aaa");
        return super.login(name, phone);
    }

    @Override
    public String register(String name, String phone) {
        return super.register(name, phone);
    }
}
