package com.landleaf.everyday.gof;

public class TestUsed {

    public void testBuild(){
        BuilderPattern build = new BuilderPattern.Builder()
                .setName("test")
                .setMaxIdle(1)
                .setMaxTotal(1)
                .setMinIdle(1)
                .build();
    }
}
