package com.landleaf.mvvm;


import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TasksViewModel extends ViewModel {

    private final MutableLiveData<Event<String>> mOpenTaskEvent = new MutableLiveData<>();
    private final MutableLiveData<Event<List<String>>> mOpenTaskEvent2 = new MutableLiveData<>();

    public LiveData<Event<String>> getOpenTaskEvent() {
        return mOpenTaskEvent;
    }

    //调用 setValue 方法，Observer 的 onChanged 方法会在调用 serValue 方法的线程回调。而postValue 方法，Observer 的 onChanged 方法将会在主线程回调
    void openTask(String taskId) {
        mOpenTaskEvent.setValue(new Event<>(taskId));
    }

    void save(List<String> list){
        mOpenTaskEvent2.postValue(new Event<List<String>>(list));
    }
}
