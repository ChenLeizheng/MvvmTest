package com.landleaf.everyday;

import dagger.Component;

/**
 * Author：Lei on 2020/12/10
 */
@Component(modules = MainActivityModule.class)
public interface MainActivityComponent {
    void inject(MainActivity activity);
}
