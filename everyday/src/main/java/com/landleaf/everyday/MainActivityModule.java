package com.landleaf.everyday;

import dagger.Module;
import dagger.Provides;

/**
 * Author：Lei on 2020/12/10
 */
@Module
public class MainActivityModule {

    @Provides
    public Demo provideDemo(){
        return new Demo();
    }
}
