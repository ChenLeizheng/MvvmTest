package com.landleaf.mvvm.data;

import java.util.List;

public interface HandleDataCallback<T> {
    void dataCallback(List<T> data);
}
