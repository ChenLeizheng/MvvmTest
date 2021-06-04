package com.landleaf.mvvm;


import java.util.ArrayList;
import java.util.List;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class TasksViewModel extends ViewModel {

    private final MutableLiveData<Event<String>> mOpenTaskEvent = new MutableLiveData<>();
    private final MutableLiveData<List<String>> mOpenTaskEvent2 = new MutableLiveData<>();

    public LiveData<Event<String>> getOpenTaskEvent() {
        return mOpenTaskEvent;
    }

    //调用 setValue 方法，Observer 的 onChanged 方法会在调用 serValue 方法的线程回调。而postValue 方法，Observer 的 onChanged 方法将会在主线程回调
    void openTask(String taskId) {
        mOpenTaskEvent.setValue(new Event<>(taskId));
    }

    public void save(List<String> list){
        mOpenTaskEvent2.setValue(list);
    }

    public LiveData<List<String>> read(){
        return mOpenTaskEvent2;
    }

    public LiveData<String> useMap(){
        LiveData<String> map = Transformations.map(mOpenTaskEvent, new Function<Event<String>, String>() {
            @Override
            public String apply(Event<String> input) {
                return input.getContent();
            }
        });
        return map;
    }
}
