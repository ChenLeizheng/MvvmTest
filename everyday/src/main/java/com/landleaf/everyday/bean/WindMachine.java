package com.landleaf.everyday.bean;

//新风机
public class WindMachine {
    public static final int READ_START_ADDRESS = 0x9C;
    public static final int READ_LENGTH = 10;
    //开关机  0--关1--开
    public static final int WRITE_SYSTEM_SWITCH_ADD = 0x9C;
    //运行模式  1--制冷2--除湿3--制热4--通风
    public static final int WRITE_RUNNING_MODE_ADD = 0x9D;
    //风量  1--高2--中3--低
    public static final int WRITE_WIND_SPEED_ADD = 0x9E;
    //新风机加湿开关  0--关1--开
    public static final int WRITE_HUMIDITY_SWITCH_ADD = 0x9F;
    //滤网清零  0--监测中1--清空
    public static final int WRITE_ClEAR_HOURS_ADD = 0xA0;
    //急停连锁  0--保持运行状态1--急停
    public static final int WRITE_STOP_ADD = 0xA1;
    //新风机通信地址 0-128
    public static final int WRITE_WIND_MACHINE_ADD = 0xA4;
    //滤网清理提示间隔设定 0-9999 默认值2160
    public static final int WRITE_CLEAR_INTERVAL_TIPS_ADD = 0xA5;

    public static final byte SPEED_HIGH = 0x01;
    public static final byte SPEED_MIDDLE = 0x02;
    public static final byte SPEED_LOW = 0x03;
    public static final byte HUMIDITY_SWITCH_OFF = 0x00;
    public static final byte HUMIDITY_SWITCH_ON = 0x01;
    public static final byte ClEAR_HOURS_ON = 0x01;
    public static final byte STOP_ON = 0x01;

}
