package com.landleaf.mvvm.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.landleaf.mvvm.R;
import com.landleaf.mvvm.data.Constant;
import com.landleaf.mvvm.data.HandleDataCallback;
import com.landleaf.mvvm.data.PlaceService;
import com.landleaf.mvvm.data.Resource;
import com.landleaf.mvvm.db.City;
import com.landleaf.mvvm.db.County;
import com.landleaf.mvvm.db.Province;
import com.landleaf.mvvm.ui.MainActivity;
import com.landleaf.mvvm.ui.WeatherActivity;
import com.landleaf.mvvm.util.DbUtil;
import com.landleaf.mvvm.util.HttpUtil;
import com.landleaf.mvvm.viewmodel.ChooseAreaViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseAreaFragment extends Fragment {

    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> dataList = new ArrayList<>();

    private Province selectedProvince;
    private City selectedCity;
    //省列表
    List<Province> provinceList;
    //市列表
    List<City> cityList;
    //县列表
    List<County> countyList;
    private ChooseAreaViewModel chooseAreaViewModel;
    private ProgressBar pbLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_area,container,false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        pbLoading = view.findViewById(R.id.pbLoading);
        listView = view.findViewById(R.id.list_view);
        chooseAreaViewModel = ViewModelProviders.of(getActivity()).get(ChooseAreaViewModel.class);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (chooseAreaViewModel.getCurrentLevel() == Constant.LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    Log.d("ChooseAreaFragment", "selectedProvince:" + selectedProvince);
                    queryCities();
                }else if (chooseAreaViewModel.getCurrentLevel() == Constant.LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    Log.d("ChooseAreaFragment", "selectedCity:" + selectedCity);
                    queryCounties();
                }else {
                    String weatherId = countyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra(Constant.WEATHER_ID, weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.weatherViewModel.requestWeather(getActivity(),weatherId,activity.liveData);
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseAreaViewModel.getCurrentLevel() == Constant.LEVEL_COUNTY) {
                    queryCities();
                } else if (chooseAreaViewModel.getCurrentLevel() == Constant.LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        LiveData<Resource<List<Province>>> liveData = chooseAreaViewModel.getProvinceList();
        handleData(liveData, new HandleDataCallback<Province>() {
            @Override
            public void dataCallback(List<Province> data) {
                provinceList = data;
                for (Province province : data) {
                    dataList.add(province.getProvinceName());
                }
                Log.d("ChooseAreaFragment", "queryProvinces:" + dataList);
            }
        });
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        LiveData<Resource<List<City>>> liveData = chooseAreaViewModel.getCityList(selectedProvince.getProvinceCode());
        handleData(liveData, new HandleDataCallback<City>() {
            @Override
            public void dataCallback(List<City> data) {
                cityList = data;
                for (City city : data) {
                    dataList.add(city.getCityName());
                }
                Log.d("ChooseAreaFragment", "queryCities:" + dataList);
            }
        });
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        LiveData<Resource<List<County>>> liveData = chooseAreaViewModel.getCountyList(selectedProvince.getProvinceCode(), selectedCity.getCityCode());
        handleData(liveData, new HandleDataCallback<County>() {
            @Override
            public void dataCallback(List<County> data) {
                countyList = data;
                for (County county : data) {
                    dataList.add(county.getCountyName());
                }
                Log.d("ChooseAreaFragment", "queryCounties:" + dataList);
            }
        });
    }

    public <T> void handleData(final LiveData<Resource<List<T>>> liveData, final HandleDataCallback callback){
        liveData.observe(this, new Observer<Resource<List<T>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<T>> listResource) {
                //加载中
                if (listResource.getStatus() == Resource.LOADING){
                    showProgressBar();
                    return;
                }
                hideProgressBar();
                //加载失败
                if (listResource.getStatus() == Resource.ERROR){
                    Toast.makeText(getActivity(), listResource.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                //加载成功
                if (listResource.getData() != null){
                    dataList.clear();
                    callback.dataCallback(listResource.getData());
                    adapter.notifyDataSetChanged();
                    listView.setSelection(0);
                }
            }
        });
    }

    private void showProgressBar(){
        pbLoading.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        pbLoading.setVisibility(View.INVISIBLE);
    }
}
