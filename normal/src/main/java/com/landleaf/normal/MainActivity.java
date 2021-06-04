package com.landleaf.normal;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.landleaf.normal.base.BaseActivity;
import com.landleaf.normal.bean.ResponseWeather;
import com.landleaf.normal.bean.SensorBean;
import com.landleaf.normal.dnake.Rs485Executor;
import com.landleaf.normal.fragment.DnakeMonitorFragment;
import com.landleaf.normal.fragment.DnakeNtControlFragment;
import com.landleaf.normal.fragment.DnakeSettingFragment;
import com.landleaf.normal.interfaces.PasswordDialogCallback;
import com.landleaf.normal.interfaces.WeatherService;
import com.landleaf.normal.utils.CommonUtil;
import com.landleaf.normal.utils.LaunchTime;
import com.landleaf.normal.utils.MaterialDialogUtil;
import com.landleaf.normal.widght.TimeView;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.yokeyword.fragmentation.SupportFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.landleaf.normal.utils.MaterialDialogUtil.TITLE_PASSWORD;

public class MainActivity extends BaseActivity {

    @BindView(R.id.timeView)
    TimeView timeView;
    @BindViews({R.id.ivSelectlLine, R.id.ivSelectlLine1, R.id.ivSelectlLine2})
    ImageView[] ivLineSelected;
    @BindViews({R.id.ivNormalLine, R.id.ivNormalLine1, R.id.ivNormalLine2})
    ImageView[] ivNormalLine;
    @BindViews({R.id.ivFragmentAir, R.id.ivFragmentHvac, R.id.ivFragmentSetting})
    ImageView[] ivFragmentIcon;
    int[] imageIconSelected = {R.drawable.icon_21_fragment_air_selected, R.drawable.icon_21_fragment_hvac_selected, R.drawable.icon_21_fragment_setting_selected};
    int[] imageIconUnSelected = {R.drawable.icon_21_fragment_air, R.drawable.icon_21_fragment_hvac, R.drawable.icon_21_fragment_setting};
    protected SupportFragment[] mFragments = new SupportFragment[3];
    protected SupportFragment currentFragment;
    private Disposable disposable;

    @Override
    protected void initActivity() {
        LaunchTime.startRecord();
        mFragments[0] = new DnakeMonitorFragment();
        mFragments[1] = new DnakeNtControlFragment();
        mFragments[2] = new DnakeSettingFragment();
        currentFragment = mFragments[0];
        loadMultipleRootFragment(R.id.flContent, 0,
                mFragments[0]
                , mFragments[1]
                , mFragments[2]
        );
        LaunchTime.endRecord();
        Log.d("LaunchTime", "DnakeMonitorFragment");
        setSelected(0);
        init();
    }

    @Override
    protected int getLayoutViewId() {
        return R.layout.activity_main;
    }

    @OnClick({R.id.rlAirMonitor, R.id.rlHvacControl, R.id.rlSysSet})
    public void onClickedView(View view) {
        switch (view.getId()) {
            case R.id.rlAirMonitor:
                showHideFragment(mFragments[0], currentFragment);
                setSelected(0);
                break;
            case R.id.rlHvacControl:
                showHideFragment(mFragments[1], currentFragment);
                setSelected(1);
                break;
            case R.id.rlSysSet:
                MaterialDialogUtil.getInstance().setPasswordDialog(MainActivity.this,TITLE_PASSWORD, "10086", new PasswordDialogCallback() {
                    @Override
                    public void success() {
                        showHideFragment(mFragments[2], currentFragment);
                        setSelected(2);
                    }

                    @Override
                    public void fail() {
                        Toast.makeText(MainActivity.this, "密码错误!", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                break;
        }
    }


    public static final String BaseUrl = "http://115.28.66.148:8080/";
    private void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService weatherService = retrofit.create(WeatherService.class);
        retrofit2.Call<ResponseWeather> weather = weatherService.getWeather("");
        byte[] readSendBytes = CommonUtil.getInstance().getReadSendBytes(1, 4, 0, 24);
        disposable = Observable.interval(5,3*60,TimeUnit.SECONDS)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Rs485Executor.getInstance().setup();
                    }
                }).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        requestWeather(weather);
                        requestSensor(readSendBytes);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("MainActivity", throwable.toString());
                    }
                });
    }

    private void setSelected(int index) {
        currentFragment = mFragments[index];
        hideNavigation();
        for (int i = 0; i < ivFragmentIcon.length; i++) {
            ivFragmentIcon[i].setImageResource(i == index ? imageIconSelected[i] : imageIconUnSelected[i]);
            ivLineSelected[i].setVisibility(i == index ? View.VISIBLE : View.GONE);
            ivNormalLine[i].setVisibility(i == index ? View.GONE : View.VISIBLE);
        }
    }

    private void requestWeather(retrofit2.Call<ResponseWeather> weatherCall){
        weatherCall.enqueue(new Callback<ResponseWeather>() {
            @Override
            public void onResponse(Call<ResponseWeather> call, Response<ResponseWeather> response) {
                ResponseWeather body = response.body();
                Log.d("MainActivity", body.toString());
                EventBus.getDefault().post(body.getWeatherModel());
            }

            @Override
            public void onFailure(Call<ResponseWeather> call, Throwable t) {
                Log.d("MainActivity", t.toString());
            }
        });
    }

    private void requestSensor(byte[] send){
        Rs485Executor.getInstance().send(send);
        try {
            byte[] receiveBytes = Rs485Executor.getInstance().receive(send, 300);
            Log.d("MainActivity", CommonUtil.getInstance().hexToString(send));
            Log.d("MainActivity", "-->receive:"+CommonUtil.getInstance().hexToString(receiveBytes));
            if (receiveBytes.length == 53 && CommonUtil.getInstance().checkBuf(receiveBytes)){
                byte[] readDataArray = CommonUtil.getInstance().calReadDataArray(receiveBytes);
                float[] floats = CommonUtil.getInstance().toFloatArray(readDataArray);
                if (floats!=null){
                    SensorBean sensorBean = new SensorBean(floats[CommonUtil.MT_INDEX_VOC], floats[CommonUtil.MT_INDEX_TEMP], floats[CommonUtil.MT_INDEX_HUMIDITY], floats[CommonUtil.MT_INDEX_CO2], floats[CommonUtil.MT_INDEX_PM25]);
                    EventBus.getDefault().post(sensorBean);
                }
            }else {
                Log.e("SimpleTestActivity", "CRC校验失败！");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        timeView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timeView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        MaterialDialogUtil.getInstance().dismissDialog();
    }
}
