package com.landleaf.everyday.bean;

//水力模块
public class HydraulicModule {
    public static final int READ_START_ADDRESS = 0x2C;
    public static final int READ_LENGTH = 5;
    //运行模式  1--制冷2--除湿3--制热4--通风
    public static final int WRITE_RUNNING_MODE_ADD = 0x2C;
    //系统开关  0--关1--开
    public static final int WRITE_SYSTEM_SWITCH_ADD = 0x2D;
    //系统运行快捷方式  0--无1--居家2--会客3--睡眠4--离家5--度假
    public static final int WRITE_SYSTEM_SHORTCUT_ADD = 0x2E;
    //温控器数量  0--32
    public static final int WRITE_TEMP_CONTROLLER_NUM_ADD = 0x2F;
    //水力模块通讯地址 1--99
    public static final int WRITE_HYDRAULIC_ADD = 0x30;

    public static final byte MODE_ZHILENG = 0x01;
    public static final byte MODE_CHUSHI = 0x02;
    public static final byte MODE_ZHIRE = 0x03;
    public static final byte MODE_TONGFENG = 0x04;
    public static final byte POWER_ON = 0x01;
    public static final byte POWER_OFF = 0x00;
    public static final byte SHORTCUT_DEFAULT = 0x00;
    public static final byte SHORTCUT_JUJIA = 0x01;
    public static final byte SHORTCUT_HUIKE = 0x02;
    public static final byte SHORTCUT_SLEEP = 0x03;
    public static final byte SHORTCUT_LIJIA = 0x04;
    public static final byte SHORTCUT_HOLIDAY = 0x05;


    private int mode;
    private int systemSwitch;
    private int systemShortcut;
    private int tempControllerNum;
    private int hydraulicAddress;

    public HydraulicModule(int mode, int systemSwitch, int systemShortcut, int tempControllerNum, int hydraulicAddress) {
        this.mode = mode;
        this.systemSwitch = systemSwitch;
        this.systemShortcut = systemShortcut;
        this.tempControllerNum = tempControllerNum;
        this.hydraulicAddress = hydraulicAddress;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getSystemSwitch() {
        return systemSwitch;
    }

    public void setSystemSwitch(int systemSwitch) {
        this.systemSwitch = systemSwitch;
    }

    public int getSystemShortcut() {
        return systemShortcut;
    }

    public void setSystemShortcut(int systemShortcut) {
        this.systemShortcut = systemShortcut;
    }

    public int getTempControllerNum() {
        return tempControllerNum;
    }

    public void setTempControllerNum(int tempControllerNum) {
        this.tempControllerNum = tempControllerNum;
    }

    public int getHydraulicAddress() {
        return hydraulicAddress;
    }

    public void setHydraulicAddress(int hydraulicAddress) {
        this.hydraulicAddress = hydraulicAddress;
    }

    @Override
    public String toString() {
        return "HydraulicModule{" +
                "mode=" + mode +
                ", systemSwitch=" + systemSwitch +
                ", systemShortcut=" + systemShortcut +
                ", tempControllerNum=" + tempControllerNum +
                ", hydraulicAddress=" + hydraulicAddress +
                '}';
    }
}
