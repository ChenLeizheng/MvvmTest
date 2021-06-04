package com.landleaf.normal.fragment;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.landleaf.normal.R;
import com.landleaf.normal.adapter.RoomViewAdapter;
import com.landleaf.normal.adapter.SmartRoomAdapter;
import com.landleaf.normal.base.BaseFragment;
import com.landleaf.normal.bean.NtAllId;
import com.landleaf.normal.bean.NtResult;
import com.landleaf.normal.bean.NtTempId;
import com.landleaf.normal.bean.Resource;
import com.landleaf.normal.interfaces.PasswordDialogCallback;
import com.landleaf.normal.utils.CommonUtil;
import com.landleaf.normal.utils.DBUtil;
import com.landleaf.normal.utils.MaterialDialogUtil;
import com.landleaf.normal.utils.Prefs;
import com.landleaf.normal.viewmodel.NtControlViewModel;
import com.landleaf.normal.widght.CircleIndicator;
import com.landleaf.normal.widght.CustomViewPager;
import com.landleaf.normal.widght.DynamicViewPager;
import com.landleaf.normal.widght.LandleafView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

import static com.landleaf.normal.utils.CommonUtil.PLC_IP_STRING;
import static com.landleaf.normal.utils.MaterialDialogUtil.TITLE_PASSWORD;


public class DnakeNtControlFragment extends BaseFragment {

    public static final String TAG = "DnakeNtControlFragment";
    @BindView(R.id.dvp_room)
    DynamicViewPager roomViewPager;
    @BindView(R.id.cid_room)
    CircleIndicator cidRoom;
    @BindView(R.id.tvShowTemp)
    TextView tvShowTemp;  //当前面板显示温度
    @BindView(R.id.myCustomLayout)
    LandleafView myMonitorLayout;
    @BindView(R.id.tvUsedTime)
    TextView tvUsedTime; //滤网使用时间
    @BindView(R.id.rlJiashi)
    RelativeLayout rlJiaShi;
    @BindView(R.id.ivJiashi)
    ImageView ivJiaShi;
    @BindView(R.id.ivPower)
    ImageView ivPower;
    @BindView(R.id.ivLijia)
    ImageView ivLijia;
    @BindView(R.id.tvShowHumidity)
    TextView tvShowHumidity;
    @BindViews({R.id.ivModeSummer, R.id.ivModeWinter, R.id.ivModeWind, R.id.ivModeChushi})
    ImageView[] ivModes;
    @BindViews({R.id.ivDi,R.id.ivGao})  //低0 高1
    ImageView[] ivWindSpeed;
    int[] windSpeedOn = {R.drawable.icon_21_speed_di_selected,R.drawable.icon_21_speed_gao_selected};
    int[] windSpeedOff = {R.drawable.icon_21_speed_di,R.drawable.icon_21_speed_gao};
    int[] modeSelected = {R.drawable.icon_21_mode_zhileng_selected, R.drawable.icon_21_mode_zhire_selected, R.drawable.icon_21_mode_tongfeng_selected, R.drawable.icon_21_mode_chushi_selected};
    int[] modeUnSelected = {R.drawable.icon_21_mode_zhileng, R.drawable.icon_21_mode_zhire, R.drawable.icon_21_mode_tongfeng, R.drawable.icon_21_mode_chushi};
    @BindColor(R.color.colorZhiLeng)
    int colorZhiLeng;
    @BindColor(R.color.colorZhiRe)
    int colorZhiRe;
    @BindColor(R.color.colorTongFeng)
    int colorTongFeng;
    @BindColor(R.color.colorChuShi)
    int colorChuShi;
    int[] colorModes;
    String[] strModes = {"制冷","制热","通风","除湿"};

    private List<List<String>> roomPerPage = new ArrayList<>();
    private List<SmartRoomAdapter> smartRoomAdapters = new ArrayList<>();
    private NtControlViewModel ntControlViewModel;
    private NtAllId ntAllId;
    private boolean isLijia = false;
    private int currentMode = 0;
    private int minVal;
    private int maxVal;
    private int showSetTemp;
    private static final int maxRoomPage = 7;
    private NtTempId currentRoomNtTempId;


    @Override
    protected void initFragment() {
        DBUtil.getInstance().initGreenDao(getActivity(),"normal_toc.db");
        CommonUtil.PLC_IP = Prefs.with(getContext()).read(PLC_IP_STRING, CommonUtil.PLC_IP);
        ntControlViewModel = ViewModelProviders.of(getActivity()).get(NtControlViewModel.class);
        colorModes = new int[]{colorZhiLeng,colorZhiRe,colorTongFeng,colorChuShi};
        String displayChushi = Prefs.with(getActivity()).read(CommonUtil.CHU_SHI_STRING, CommonUtil.HIDE_STRING);
        ivModes[3].setVisibility(CommonUtil.HIDE_STRING.equals(displayChushi) ? View.INVISIBLE : View.VISIBLE);
        String displayJiashi = Prefs.with(getActivity()).read(CommonUtil.JIA_SHI_STRING, CommonUtil.HIDE_STRING);
        rlJiaShi.setVisibility(CommonUtil.HIDE_STRING.equals(displayJiashi) ? View.INVISIBLE : View.VISIBLE);
        ntAllId = ntControlViewModel.requestNtAllId(getActivity());
        List<String> rooms = ntControlViewModel.requestRooms();
        Log.d(TAG, "rooms:" + rooms);
        getRooms(rooms);
        readCurrentStatus();
    }

    @Override
    protected int getViewID() {
        return R.layout.fragment_nt_control_dnake;
    }

    private void readCurrentStatus(){
        handleData(ntControlViewModel.readPlc(ntAllId.getPowerId()));
        handleData(ntControlViewModel.readPlc(ntAllId.getModeId()));
        handleData(ntControlViewModel.readPlc(ntAllId.getLijiaId()));
        handleData(ntControlViewModel.readPlc(ntAllId.getClearId()));
        handleData(ntControlViewModel.readPlc(ntAllId.getHourId()));
        handleData(ntControlViewModel.readPlc(ntAllId.getJiashiId()));
        handleData(ntControlViewModel.readPlc(ntAllId.getVolumeId()));
    }

    //读取当前房间温湿度信息
    private void readTemp(NtTempId ntTempId) {
        if(ntTempId == null){
            return;
        }
        handleData(ntControlViewModel.readPlc(ntTempId.getHumidityId()));
        handleData(ntControlViewModel.readPlc(ntTempId.getShowTempId()));
        handleData(ntControlViewModel.readPlc(ntTempId.getShowSetTempId()));
    }

    private int hour;
    private void handleData(LiveData<Resource<NtResult>> liveData) {
        liveData.observe(this, new Observer<Resource<NtResult>>() {
            @Override
            public void onChanged(Resource<NtResult> resource) {
                if (resource.getStatus() == Resource.ERROR) {
                    showWarnToast(getContext(), resource.getMessage());
                    return;
                }
                NtResult ntResult = resource.getData();
                Log.d(TAG, "ntResult:" + ntResult);
                //开关
                if (ntResult.getId() == ntAllId.getPowerId()) {
                    ivPower.setImageResource(ntResult.getValue() == NtControlViewModel.POWER_ON_VALUE ? R.drawable.icon_21_power_on:R.drawable.icon_21_power_off);
                }
                //居家离家状态
                if (ntResult.getId() == ntAllId.getLijiaId()){
                    isLijia = ntResult.getValue() == NtControlViewModel.LIJIA_VALUE;
                    ivLijia.setImageResource(isLijia ? R.drawable.icon_21_lijia_off:R.drawable.icon_21_jujia_on);
                }
                //模式
                if (ntResult.getId() == ntAllId.getModeId()){
                    setSelectedMode(ntResult.getValue());
                }
                //清零成功
                if (ntResult.getId() == ntAllId.getClearId()) {
                    hour = 0;
                    tvUsedTime.setText(hour + "小时");
                }
                //设置加湿
                if (ntResult.getId() == ntAllId.getJiashiId()) {
                    ivJiaShi.setImageResource(ntResult.getValue() == ntControlViewModel.JIASHI_VALUE ? R.drawable.icon_21_jiashi_selected:R.drawable.icon_21_jiashi);
                }
                //设置风量
                if (ntResult.getId() == ntAllId.getVolumeId()) {
                    setWindSpeed(ntResult.getValue());
                }
                //设置使用小时
                if (ntResult.getId() == ntAllId.getHourId()) {
                    hour = ntResult.getValue();
                    tvUsedTime.setText(hour + "小时");
                }
                if (currentRoomNtTempId == null){
                    return;
                }
                //写入 设定温度
                if (ntResult.getId() == currentRoomNtTempId.getSetTempId()) {
                    showSetTemp = ntResult.getValue();
                    myMonitorLayout.setProgress(ntResult.getValue() / 10, 0, 30, colorModes[currentMode], strModes[currentMode]);
                }
                //读取 设定温度显示
                if (ntResult.getId() == currentRoomNtTempId.getShowSetTempId()) {
                    showSetTemp = ntResult.getValue();
                    myMonitorLayout.setProgress(ntResult.getValue() / 10, 0, 30, colorModes[currentMode], strModes[currentMode]);
                }
                //实际温度
                if (ntResult.getId() == currentRoomNtTempId.getShowTempId()) {
                    tvShowTemp.setText(ntResult.getValue() / 10 +"℃");
                }
                //实际湿度
                if (ntResult.getId() == currentRoomNtTempId.getHumidityId()) {
                    tvShowHumidity.setText(ntResult.getValue() / 10 + "%");
                }
            }
        });
    }

    @OnClick({R.id.ivPower, R.id.ivMinus, R.id.ivPlus, R.id.ivClear, R.id.ivLijia, R.id.ivModeSummer,
            R.id.ivModeWinter, R.id.ivModeWind, R.id.ivModeChushi, R.id.ivGao, R.id.ivDi, R.id.ivJiashi})
    public void onClickView(View view) {
        switch (view.getId()) {
            //电源开关
            case R.id.ivPower:
                MaterialDialogUtil.getInstance().showSureDialog(getContext(), new PasswordDialogCallback() {
                    @Override
                    public void success() {
                        handleData(ntControlViewModel.writePlc(ntAllId.getPowerId(), -1));
                    }

                    @Override
                    public void fail() {

                    }
                });
                break;
            //设定温度
            case R.id.ivMinus:
                if(currentMode == NtControlViewModel.MODE_TONGFENG_VALUE){
                    showWarnToast(getActivity(),"通风模式温度不可调！");
                    return;
                }
                int tempMinus = showSetTemp;
                tempMinus -= 10;
                if (tempMinus <= minVal)
                    tempMinus = minVal;
                if (tempMinus >= maxVal)
                    tempMinus = maxVal;
                handleData(ntControlViewModel.writePlc(currentRoomNtTempId.getSetTempId(),tempMinus));
                break;
            case R.id.ivPlus:
                if(currentMode == NtControlViewModel.MODE_TONGFENG_VALUE){
                    showWarnToast(getActivity(),"通风模式温度不可调！");
                    return;
                }
                int tempPlus = showSetTemp;
                tempPlus += 10;
                if (tempPlus >= maxVal)
                    tempPlus = maxVal;
                if (tempPlus <= minVal)
                    tempPlus = minVal;
                handleData(ntControlViewModel.writePlc(currentRoomNtTempId.getSetTempId(),tempPlus));
                break;
            //滤网清零
            case R.id.ivClear:
                MaterialDialogUtil.getInstance().setPasswordDialog(getActivity(), TITLE_PASSWORD,"123456", new PasswordDialogCallback() {
                    @Override
                    public void success() {
                        handleData(ntControlViewModel.writePlc(ntAllId.getClearId(),1));
                        showWarnToast(getActivity(),"清零成功!");
                    }

                    @Override
                    public void fail() {
                        showWarnToast(getActivity(),"密码错误!");
                    }
                });
                break;
            //离家
            case R.id.ivLijia:
                if (currentMode == NtControlViewModel.MODE_TONGFENG_VALUE && !isLijia){
                    showWarnToast(getActivity(),"通风状态，不能切换离家模式!");
                    return;
                }
                handleData(ntControlViewModel.writePlc(ntAllId.getLijiaId(), -1));
                break;
            //模式
            case R.id.ivModeSummer:
                handleData(ntControlViewModel.writePlc(ntAllId.getModeId(), 0));
                break;
            case R.id.ivModeWinter:
                handleData(ntControlViewModel.writePlc(ntAllId.getModeId(), 1));
                break;
            case R.id.ivModeWind:
                if (isLijia){
                    showWarnToast(getActivity(),"离家模式，不能切换通风状态!");
                    return;
                }
                handleData(ntControlViewModel.writePlc(ntAllId.getModeId(), 2));
                break;
            case R.id.ivModeChushi:
                handleData(ntControlViewModel.writePlc(ntAllId.getModeId(), 3));
                break;
            //风速
            case R.id.ivGao:
                if (currentMode!=NtControlViewModel.MODE_TONGFENG_VALUE){
                    showWarnToast(getActivity(),"非通风模式!风速不可调!");
                    return;
                }
                handleData(ntControlViewModel.writePlc(ntAllId.getVolumeId(),1));
                break;
            case R.id.ivDi:
                if (currentMode!=ntControlViewModel.MODE_TONGFENG_VALUE){
                    showWarnToast(getActivity(),"非通风模式!风速不可调!");
                    return;
                }
                handleData(ntControlViewModel.writePlc(ntAllId.getVolumeId(),0));
                break;
            //加湿
            case R.id.ivJiashi:
                if (currentMode!=NtControlViewModel.MODE_DONGAJI_VALUE){
                    showWarnToast(getActivity(),"非制热模式!不可加湿!");
                    return;
                }
                handleData(ntControlViewModel.writePlc(ntAllId.getJiashiId(),-1));
                break;
            default:
                break;

        }
    }


    public void getRooms(List<String> rooms) {
        if (rooms.isEmpty()) {
            return;
        }
        currentRoomNtTempId = ntControlViewModel.requestNtTempIdByRoomName(getActivity(),rooms.get(0));
        readTemp(currentRoomNtTempId);
        int total = rooms.size();
        int pageSize = total / maxRoomPage;
        if (pageSize == 0) {
            //小于最大页数，直接放入buffer
            roomPerPage.add(rooms);
        } else {
            //大于最大页数
            int startIndex = 0;
            int lastIndex = maxRoomPage;
            for (int i = 0; i < pageSize; i++) {
                List<String> subList = rooms.subList(startIndex, lastIndex);
                startIndex += maxRoomPage;
                lastIndex += maxRoomPage;
                roomPerPage.add(subList);
            }
            int remainder = total % maxRoomPage;
            //最后剩余的数据（小于最大页数的数据)
            if (remainder > 0)
                roomPerPage.add(rooms.subList(total - remainder, total));
        }
        //分配数据
        for (int i = 0; i < roomPerPage.size(); i++) {
            List<String> room = roomPerPage.get(i);
            SmartRoomAdapter smartRoomAdapter = new SmartRoomAdapter(this.getActivity(), room);
            smartRoomAdapter.setOnItemClickListener((view1, textView, position) -> {
                //获取选中房间的温度信息
                currentRoomNtTempId = ntControlViewModel.requestNtTempIdByRoomName(getActivity(), room.get(position));
                Log.d(TAG, "currentRoomNtTempId:" + currentRoomNtTempId+","+room.get(position));
                readTemp(currentRoomNtTempId);
            });
            smartRoomAdapters.add(smartRoomAdapter);
        }
        roomViewPager.setAdapter(new RoomViewAdapter(smartRoomAdapters));
        roomViewPager.addOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //获取滑页选中第一个房间的状态
                Log.d(TAG, "设置第一个房间的状态!");
                currentRoomNtTempId = ntControlViewModel.requestNtTempIdByRoomName(getActivity(), roomPerPage.get(position).get(0));
                readTemp(currentRoomNtTempId);
                smartRoomAdapters.get(position).initSelection();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        cidRoom.setViewPager(roomViewPager);
    }

    private void setSelectedMode(int index) {
        currentMode = index;
        if (currentMode == NtControlViewModel.MODE_DONGAJI_VALUE){
            minVal = 160;
            maxVal = 260;
        }else {
            minVal = 200;
            maxVal = 300;
        }
        for (int i = 0; i < ivModes.length; i++) {
            ivModes[i].setImageResource(i == index ? modeSelected[i] : modeUnSelected[i]);
        }
    }

    private void setWindSpeed(int index) {
        for (int i = 0; i < ivWindSpeed.length; i++) {
            ivWindSpeed[i].setImageResource(index == i? windSpeedOn[i]:windSpeedOff[i]);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MaterialDialogUtil.getInstance().dismissDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleRemotePlc(NtResult ntResult){
        MutableLiveData<Resource<NtResult>> liveData = new MutableLiveData<>();
        liveData.setValue(new Resource<NtResult>(Resource.SUCCESS,ntResult,null));
        handleData(liveData);
    }
}
