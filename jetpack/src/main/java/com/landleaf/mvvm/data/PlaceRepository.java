package com.landleaf.mvvm.data;

import android.util.Log;

import com.landleaf.mvvm.db.City;
import com.landleaf.mvvm.db.County;
import com.landleaf.mvvm.db.Province;
import com.landleaf.mvvm.util.CoolWeatherExecutors;
import com.landleaf.mvvm.util.DbUtil;
import com.landleaf.mvvm.util.HttpUtil;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceRepository {

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private PlaceRepository(){}

    public static PlaceRepository getInstance(){
        return ViewHolder.instance;
    }

    public LiveData<Resource<List<Province>>> getProvinceList(){
        final MutableLiveData<Resource<List<Province>>> liveDataProvince = new MutableLiveData<>();
        liveDataProvince.setValue(new Resource<List<Province>>(Resource.LOADING,null,null));
        provinceList = DbUtil.getInstance().getAllProvince();
        if (provinceList.isEmpty()){
            PlaceService placeService = HttpUtil.getInstance().getPlaceService();
            final Call<List<Province>> provinces = placeService.getProvinces();
            provinces.enqueue(new Callback<List<Province>>() {
                @Override
                public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                    provinceList = response.body();
                    Log.d("PlaceRepository", "onResponse:"+provinceList.toString());
                    CoolWeatherExecutors.diskIO.execute(new Runnable() {
                        @Override
                        public void run() {
                            for (Province province : provinceList) {
                                DbUtil.getInstance().saveProvince(province);
                            }
                            liveDataProvince.postValue(new Resource<List<Province>>(Resource.SUCCESS,provinceList,null));
                        }
                    });
                }

                @Override
                public void onFailure(Call<List<Province>> call, Throwable t) {
                    Log.d("PlaceRepository", "onFailure:"+t.toString());
                    liveDataProvince.setValue(new Resource<List<Province>>(Resource.ERROR,null,"Province加载失败!"));
                }
            });
        }else {
            Log.d("PlaceRepository", "success cache Province");
            liveDataProvince.setValue(new Resource<List<Province>>(Resource.SUCCESS,provinceList,null));
        }
        return liveDataProvince;
    }

    public LiveData<Resource<List<City>>> getCityList(final Long provinceId){
        final MutableLiveData<Resource<List<City>>> liveDataCity = new MutableLiveData<>();
        liveDataCity.setValue(new Resource<List<City>>(Resource.LOADING,null,null));
        cityList = DbUtil.getInstance().getCityByProvinceId(provinceId);
        if (cityList.isEmpty()){
            PlaceService placeService = HttpUtil.getInstance().getPlaceService();
            placeService.getCities(provinceId).enqueue(new Callback<List<City>>() {
                @Override
                public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                    cityList = response.body();
                    Log.d("PlaceRepository", "onResponse:"+cityList.toString());
                    CoolWeatherExecutors.diskIO.execute(new Runnable() {
                        @Override
                        public void run() {
                            for (City city : cityList) {
                                city.setProvinceId(provinceId);
                                DbUtil.getInstance().saveCity(city);
                            }
                            liveDataCity.postValue(new Resource<List<City>>(Resource.SUCCESS,cityList,null));
                        }
                    });
                }

                @Override
                public void onFailure(Call<List<City>> call, Throwable t) {
                    Log.d("PlaceRepository", "onFailure:"+t.toString());
                    liveDataCity.postValue(new Resource<List<City>>(Resource.ERROR,null,"City加载失败!"));
                }
            });
        }else {
            Log.d("PlaceRepository", "success cache City");
            liveDataCity.setValue(new Resource<List<City>>(Resource.SUCCESS,cityList,null));
        }
        return liveDataCity;
    }

    public LiveData<Resource<List<County>>> getCountyList(Long provinceId, final Long cityId){
        final MutableLiveData<Resource<List<County>>> countyLiveData = new MutableLiveData<>();
        countyLiveData.setValue(new Resource<List<County>>(Resource.LOADING,null,null));
        countyList = DbUtil.getInstance().getCountyByCityId(cityId);
        if (countyList.isEmpty()){
            PlaceService placeService = HttpUtil.getInstance().getPlaceService();
            placeService.getCounties(provinceId,cityId).enqueue(new Callback<List<County>>() {
                @Override
                public void onResponse(Call<List<County>> call, Response<List<County>> response) {
                    countyList = response.body();
                    Log.d("PlaceRepository", "onResponse:"+countyList.toString());
                    CoolWeatherExecutors.diskIO.execute(new Runnable() {
                        @Override
                        public void run() {
                            for (County county : countyList) {
                                county.setCityId(cityId);
                                DbUtil.getInstance().saveCounty(county);
                            }
                            countyLiveData.postValue(new Resource<List<County>>(Resource.SUCCESS,countyList,null));
                        }
                    });
                }

                @Override
                public void onFailure(Call<List<County>> call, Throwable t) {
                    Log.d("PlaceRepository", "onFailure:"+t.toString());
                    countyLiveData.setValue(new Resource<List<County>>(Resource.ERROR,null,"County加载失败!"));
                }
            });
        }else {
            Log.d("PlaceRepository", "success cache County");
            countyLiveData.setValue(new Resource<List<County>>(Resource.SUCCESS,countyList,null));
        }
        return countyLiveData;
    }

    private static class ViewHolder{
        public static PlaceRepository instance = new PlaceRepository();
    }
}
