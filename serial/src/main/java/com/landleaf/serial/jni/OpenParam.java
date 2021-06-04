package com.landleaf.serial.jni;

/**
 * Created by Chenyifei on 2017/4/26.
 * 串口打开参数
 */
public class OpenParam {
    private String devNum;
    private int speed;
    private int dataBit;
    private int stopBit;
    private int parity;
    private boolean is485Dev;

    private int cmdRead;
    private int cmdWrite;

    public OpenParam(String devNum, int speed, int dataBit, int stopBit, int parity, boolean is485Dev) {
        this.devNum = devNum;
        this.speed = speed;
        this.dataBit = dataBit;
        this.stopBit = stopBit;
        this.parity = parity;
        this.is485Dev = is485Dev;
    }

    public int getCmdRead() {
        return cmdRead;
    }

    public void setCmdRead(int cmdRead) {
        this.cmdRead = cmdRead;
    }

    public int getCmdWrite() {
        return cmdWrite;
    }

    public void setCmdWrite(int cmdWrite) {
        this.cmdWrite = cmdWrite;
    }

    public String getDevNum() {
        return devNum;
    }

    public void setDevNum(String devNum) {
        this.devNum = devNum;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDataBit() {
        return dataBit;
    }

    public void setDataBit(int dataBit) {
        this.dataBit = dataBit;
    }

    public int getStopBit() {
        return stopBit;
    }

    public void setStopBit(int stopBit) {
        this.stopBit = stopBit;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public boolean is485Dev() {
        return is485Dev;
    }

    public void setIs485Dev(boolean is485Dev) {
        this.is485Dev = is485Dev;
    }

    @Override
    public String toString() {
        return "OpenParam{" +
                "devNum='" + devNum + '\'' +
                ", speed=" + speed +
                ", dataBit=" + dataBit +
                ", stopBit=" + stopBit +
                ", parity=" + parity +
                ", is485Dev=" + is485Dev +
                ", cmdRead=" + cmdRead +
                ", cmdWrite=" + cmdWrite +
                '}';
    }
}
