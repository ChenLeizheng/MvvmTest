package com.landleaf.serial.jni;

import java.io.FileDescriptor;

public class SerialPort extends SerialPortJNIOpt {

    static {
        //狄耐克部分屏，需要注释掉该部分代码;
        System.loadLibrary("serialport");
    }
    private static final String TAG = "ModbusExecutor";

    private boolean isOpen;

    private String devNum;

    private int speed;

    private int dataBits;

    private int stopBits;

    private int parity;

    private boolean is485Dev;

    public boolean openDev(String devNum, int speed, int dataBits, int stopBits, int parity, boolean is485Dev, int cmdWrite, int cmdRead) {
        this.devNum = devNum;
        this.speed = speed;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        this.is485Dev = is485Dev;
        FileDescriptor fd = this.openDev(devNum, is485Dev);
        if (fd != null) {
            this.setSpeed(fd, speed);
            this.setParity(fd, dataBits, stopBits, parity);
            isOpen = true;
            return true;
        } else {
            isOpen = false;
            return false;
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void closeDev() {
        if (this.mFd != null)
            this.closeDev(this.mFd);
        isOpen = false;
    }

    public boolean sendBytes(byte[] buffer) {
        return this.mFd != null && this.writeBytes(buffer);
    }

    public byte[] receiveBytes(int len, int time) {
        //set buffer with give length
        byte[] buffer = new byte[len];
        //read bytes by jni
        int length = this.readBytes(buffer, time);
        //Log.d(TAG,"Read len:"+length);
        //get and copy bytes
        if (length > 0 && mFileInputStream != null) {
            byte[] read = new byte[length];
            //copy from buffer to result
            System.arraycopy(buffer, 0, read, 0, length);
            return read;
        } else {
            return null;
        }
    }

    public void setOpen(boolean open) {
        isOpen = open;
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

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
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
}