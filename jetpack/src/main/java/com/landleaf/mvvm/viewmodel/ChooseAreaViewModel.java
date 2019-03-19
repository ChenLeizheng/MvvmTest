package com.landleaf.mvvm.viewmodel;

import com.landleaf.mvvm.data.Constant;
import com.landleaf.mvvm.data.PlaceRepository;
import com.landleaf.mvvm.data.Resource;
import com.landleaf.mvvm.db.City;
import com.landleaf.mvvm.db.County;
import com.landleaf.mvvm.db.Province;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ChooseAreaViewModel extends ViewModel {
    //当前选中的级别
    int currentLevel = 0;

    public LiveData<Resource<List<Province>>> getProvinceList() {
        currentLevel = Constant.LEVEL_PROVINCE;
        return PlaceRepository.getInstance().getProvinceList();
    }

    public LiveData<Resource<List<City>>> getCityList(Long provinceId) {
        currentLevel = Constant.LEVEL_CITY;
        return PlaceRepository.getInstance().getCityList(provinceId);
    }

    public LiveData<Resource<List<County>>> getCountyList(Long provinceId, Long cityId) {
        currentLevel = Constant.LEVEL_COUNTY;
        return PlaceRepository.getInstance().getCountyList(provinceId, cityId);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
