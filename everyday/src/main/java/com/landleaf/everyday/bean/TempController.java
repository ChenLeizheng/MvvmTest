package com.landleaf.everyday.bean;

//温控器面板
public class TempController {
    public static final int READ_SWITCH_START_ADDRESS = 0x01B4;
    public static final int READ_SET_TEMP_START_ADDRESS = 0x01B6;
    public static final int READ_TEMP_START_ADDRESS = 0x01D6;
    public static final int READ_HUMIDITY_START_ADDRESS = 0x01F6;

    //11#温控面板对应寄存器    12#对应的地址为11#起始地址 +32(0x20)
    public static final int WRITE_SWITCH_START_ADDRESS = 0x029C;
    public static final int WRITE_SET_TEMP_START_ADDRESS = 0x029D;

    private int tempSwitch;
    private int setTemp;
    private int temp;
    private int humidity;
}
