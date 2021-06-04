package com.landleaf.serial;

public class Constant {

    //读写功能码
    public static final int KF_READ_FUNCTION = 0x03;
    public static final int KF_WRITE_FUNCTION = 0x06;
    //读取新风机状态起始地址
    public static final int KF_READ_START_ADDRESS = 0x0B;
    //设置模式
    public static final int KF_WRITE_MODE_ADDRESS = 0x01;
    //设置风速
    public static final int KF_WRITE_WIND_SPEED_ADDRESS = 0x02;
    //设置CO2浓度
    public static final int KF_WRITE_CO2_ADDRESS = 0x03;
}
