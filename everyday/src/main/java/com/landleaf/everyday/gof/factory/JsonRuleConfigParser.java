package com.landleaf.everyday.gof.factory;

public class JsonRuleConfigParser implements IRuleConfigParser {
    @Override
    public void parser(String txt) {
        System.out.println("解析json");
    }
}
