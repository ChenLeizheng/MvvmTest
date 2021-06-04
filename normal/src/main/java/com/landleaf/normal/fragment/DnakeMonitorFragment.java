package com.landleaf.normal.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.landleaf.normal.R;
import com.landleaf.normal.base.BaseFragment;
import com.landleaf.normal.bean.SensorBean;
import com.landleaf.normal.bean.WeatherModel;
import com.landleaf.normal.utils.CommonUtil;
import com.landleaf.normal.widght.LandleafView1;
import com.landleaf.normal.widght.RulerView;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindColor;
import butterknife.BindView;

public class DnakeMonitorFragment extends BaseFragment {

    private static final String TAG = DnakeMonitorFragment.class.getSimpleName();

    @BindView(R.id.rulePM25Progress)
    RulerView rulePM25Progress;
    @BindView(R.id.ruleCO2Progress)
    RulerView ruleCO2Progress;
    @BindView(R.id.landleafView)
    LandleafView1 landleafView;
    @BindView(R.id.tvTempIn)
    TextView tvTempIn;
    @BindView(R.id.tvTempOut)
    TextView tvTempOut;
    @BindView(R.id.tvHumidityIn)
    TextView tvHumidityIn;
    @BindView(R.id.tvHumidityOut)
    TextView tvHumidityOut;
    //室外天气
    @BindView(R.id.ivWeatherStatus)
    ImageView ivWeatherStatus;
    @BindView(R.id.tvWeatherStatus)
    TextView tvWeatherStatus;//
    @BindView(R.id.tvUpdateTime)
    TextView tvUpdateTime;//更新时间
    @BindView(R.id.tvCalender)
    TextView tvCalender;//万年历
    @BindView(R.id.tvWindDir)
    TextView tvWindDirection;//风向
    @BindView(R.id.tvWindLevel)
    TextView tvWindLevel;//风力
    @BindView(R.id.tvSport)
    TextView tvSport;//运动指数
    @BindView(R.id.tvSunny)
    TextView tvSunny;//紫外线指数
    @BindView(R.id.tvGanmao)
    TextView tvCold;//健康指数
    @BindView(R.id.tvChuanyi)
    TextView tvClothes;//穿衣指数

    @BindColor(R.color.colorLevelGood)
    int colorLevelGood;
    @BindColor(R.color.colorLevelMedium)
    int colorLevelMedium;
    @BindColor(R.color.colorLevelBad)
    int colorLevelBad;

    private int[] colorLevels;
    String[] pm25LevelText = {"舒适", "良好", "中等"};

    @Override
    protected void initFragment() {
        colorLevels = new int[]{colorLevelGood, colorLevelMedium, colorLevelBad};
    }

    @Override
    protected int getViewID() {
        return R.layout.fragment_monitor_dnake;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getSensorBean(SensorBean sensorBean) {
        float co2 = sensorBean.getCo2();
        int co2Level = CommonUtil.getInstance().getCO2Level(co2);
        ruleCO2Progress.setProgress((int) co2,colorLevels[co2Level],false);

        float temp = sensorBean.getTemp();
        int tempLevel = CommonUtil.getInstance().getTempLevel(temp);
        tvTempIn.setTextColor(colorLevels[tempLevel]);
        tvTempIn.setText((int) temp + "");

        float humidity = sensorBean.getHumidity();
        int humidityLevel = CommonUtil.getInstance().getHumidityLevel(humidity);
        tvHumidityIn.setTextColor(colorLevels[humidityLevel]);
        tvHumidityIn.setText((int) humidity + "");

        float pm25 = sensorBean.getPm25();
        int pm25Level = CommonUtil.getInstance().getPm25Level(pm25);
        landleafView.setProgress((int)pm25, 0, 200, colorLevels[pm25Level], pm25LevelText[pm25Level]);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getWeather(WeatherModel weatherModel) {
        Log.d(TAG, "收到天气:" + weatherModel);
        this.tvCalender.setText(weatherModel.getCalender());
        this.tvWeatherStatus.setText(weatherModel.getWeatherStatus());
        this.tvUpdateTime.setText(weatherModel.getUpdateTime());
        this.tvWindDirection.setText(weatherModel.getWindDirection());
        this.tvWindLevel.setText(weatherModel.getWindLevel());
        this.tvSport.setText(weatherModel.getSportLevel());
        this.tvSunny.setText(weatherModel.getUvLevel());
        this.tvClothes.setText(weatherModel.getDressLevel());
        this.tvCold.setText(weatherModel.getColdLevel());
        //加载logo
        Glide.with(this).load(weatherModel.getPicUrl()).into(ivWeatherStatus);
        //温度
        if (CommonUtil.getInstance().isNumber(weatherModel.getTemp())) {
            float temp = Float.parseFloat(weatherModel.getTemp());
            int tempLevel = CommonUtil.getInstance().getTempLevel(temp);
            tvTempOut.setTextColor(colorLevels[tempLevel]);
            tvTempOut.setText(weatherModel.getTemp());
        }
        //湿度
        if (CommonUtil.getInstance().isNumber(weatherModel.getHumidity())) {
            float humidity = Float.parseFloat(weatherModel.getHumidity());
            int humidityLevel = CommonUtil.getInstance().getHumidityLevel(humidity);
            tvHumidityOut.setTextColor(colorLevels[humidityLevel]);
            tvHumidityOut.setText(weatherModel.getHumidity());
        }
        //PM2.5
        if (CommonUtil.getInstance().isNumber(weatherModel.getPm25())) {
            float pm25 = Float.parseFloat(weatherModel.getPm25());
            int pm25Level = CommonUtil.getInstance().getPm25Level(pm25);
            rulePM25Progress.setProgress(pm25,colorLevels[pm25Level],false);
        }
    }
}
