package com.landleaf.mvvm.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HeWeather {
    List<Weather> HeWeather;

    public List<Weather> getList() {
        return HeWeather;
    }

    public void setList(List<Weather> list) {
        this.HeWeather = list;
    }

    @Override
    public String toString() {
        return "HeWeather{" +
                "list=" + HeWeather +
                '}';
    }
}
