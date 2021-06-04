package com.landleaf.normal.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.landleaf.normal.bean.NtAllId;
import com.landleaf.normal.bean.NtResult;
import com.landleaf.normal.bean.NtTempId;
import com.landleaf.normal.bean.Resource;
import com.landleaf.normal.bean.Room;
import com.landleaf.normal.interfaces.PlcHandler;
import com.landleaf.normal.plc.PlcExecutor;
import com.landleaf.normal.plc.PlcUnitType;
import com.landleaf.normal.utils.CommonUtil;
import com.landleaf.normal.utils.DBUtil;
import com.landleaf.normal.utils.Prefs;
import com.landleaf.normal.utils.ThreadPoolManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static com.landleaf.normal.plc.PlcOperType.OPER_READ;
import static com.landleaf.normal.plc.PlcOperType.OPER_WRITE;

public class NtControlViewModel extends ViewModel {

    public static final int POWER_ON_VALUE = 1;
    public static final int LIJIA_VALUE = 1;
    public static final int MODE_XIAJI_VALUE = 0;
    public static final int MODE_DONGAJI_VALUE = 1;
    public static final int MODE_TONGFENG_VALUE = 2;
    public static final int MODE_CHUSHI_VALUE = 3;
    public static final int JIASHI_VALUE = 1;
    private NtAllId ntAllId;
    private HashMap<Integer, Integer> hs = new HashMap<>();
    public static final String AD_TYPE_HUMIDIFICATION = "加湿";//开 关
    public static final String AD_TYPE_POWER_SWITCH = "开关";//开 关
    public static final String AD_TYPE_POWER = "电源";
    public static final String AD_TYPE_AIR_QUANTITY = "风量";//低 中 高 超高 自动
    public static final String AD_TYPE_AWAY_HOME = "离家";//居家 离家
    public static final String AD_TYPE_MODE = "模式";
    public static final String AD_TYPE_HOUR = "时间";//滤网使用时间
    public static final String AD_TYPE_CLEAR = "清零";//滤网时间清零

    //获取主机点位等
    public NtAllId requestNtAllId(Context context) {
        if (ntAllId == null) {
            int powerSwitch = Prefs.with(context).readInt(AD_TYPE_POWER_SWITCH, 2004);
            int modeSwitch = Prefs.with(context).readInt(AD_TYPE_MODE, 2006);
            int outHomeSwitch = Prefs.with(context).readInt(AD_TYPE_AWAY_HOME, 2008);
            int speedSwitch = Prefs.with(context).readInt(AD_TYPE_AIR_QUANTITY, 2010);
            int humiditySwitch = Prefs.with(context).readInt(AD_TYPE_HUMIDIFICATION, 2014);
            int hourSwitch = Prefs.with(context).readInt(AD_TYPE_HOUR, 1180);
            int clearSwitch = Prefs.with(context).readInt(AD_TYPE_CLEAR, 1038);
            ntAllId = new NtAllId(humiditySwitch, hourSwitch, clearSwitch, powerSwitch, modeSwitch, outHomeSwitch, speedSwitch);
        }
        return ntAllId;
    }

    public List<NtTempId> requestAllNtTempId(Context context) {
        List<Room> roomList = DBUtil.getInstance().getAllRoom();
        Gson gson = new Gson();
        ArrayList<NtTempId> ntTempIds = new ArrayList<>();
        for (Room room : roomList) {
            String json = Prefs.with(context).read(room.getRoomName(), "");
            NtTempId ntTempId = gson.fromJson(json, NtTempId.class);
            ntTempIds.add(ntTempId);
        }
        return ntTempIds;
    }

    public NtTempId requestNtTempIdByRoomName(Context context, String roomName) {
        Gson gson = new Gson();
        String json = Prefs.with(context).read(roomName, "");
        return gson.fromJson(json, NtTempId.class);
    }

    public List<String> requestRooms() {
        List<Room> rooms = DBUtil.getInstance().getAllRoom();
        List<String> list = new ArrayList<>();
        for (Room room : rooms) {
            list.add(room.getRoomName());
        }
        return list;
    }

    //冬季模式才能加湿    通风和离家互斥
    public Integer getCurrentMode(int offset) {
        return hs.get(offset);
    }

    public LiveData<Resource<NtResult>> readPlc(int offset) {
        MutableLiveData<Resource<NtResult>> liveData = new MutableLiveData<>();
        ThreadPoolManager.getInstance().submitSingleJob(new Runnable() {
            @Override
            public void run() {
                try {
                    PlcExecutor.getInstance().plcOperateMethod(offset, 0, CommonUtil.PLC_IP, PlcUnitType.VW, OPER_READ, new PlcHandler() {
                        @Override
                        public void readRes(int offset, float val) {
                            hs.put(offset, (int) val);
                            liveData.postValue(new Resource<NtResult>(Resource.SUCCESS, new NtResult(offset, (int) val), null));
                        }

                        @Override
                        public void writeRes(int offset, float val) {

                        }

                        @Override
                        public void operDebug(String msg) {
                            liveData.postValue(new Resource<NtResult>(Resource.ERROR, null, msg));
                        }

                        @Override
                        public void operRes(boolean operRes, String errorCode, int offset) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "readPlc", THREAD_PRIORITY_BACKGROUND, "");
        return liveData;
    }

    public LiveData<Resource<NtResult>> writePlc(int offset, int value) {
        if (value == -1) {
            if (hs.containsKey(offset)) {
                value = hs.get(offset) == 0 ? 1 : 0;
            } else {
                value = 0;
            }
        }
        MutableLiveData<Resource<NtResult>> liveData = new MutableLiveData<>();
        int finalValue = value;
        ThreadPoolManager.getInstance().submitSingleJob(new Runnable() {
            @Override
            public void run() {
                try {
                    PlcExecutor.getInstance().plcOperateMethod(offset, finalValue, CommonUtil.PLC_IP, PlcUnitType.VW, OPER_WRITE, new PlcHandler() {
                        @Override
                        public void readRes(int offset, float val) {

                        }

                        @Override
                        public void writeRes(int offset, float val) {
                            hs.put(offset, (int) val);
                            liveData.postValue(new Resource<NtResult>(Resource.SUCCESS, new NtResult(offset, (int) val), null));
                        }

                        @Override
                        public void operDebug(String msg) {
                            liveData.postValue(new Resource<NtResult>(Resource.ERROR, null, msg));
                        }

                        @Override
                        public void operRes(boolean operRes, String errorCode, int offset) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "readPlc", THREAD_PRIORITY_BACKGROUND, "");
        return liveData;
    }
}
