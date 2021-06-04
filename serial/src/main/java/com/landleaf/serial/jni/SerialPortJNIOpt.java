package com.landleaf.serial.jni;

import android.util.Log;

import com.landleaf.serial.CommonUtils;

import java.io.FileDescriptor;
import java.io.FileInputStream;

/**
 * 调用JNI的串口
 */
public class SerialPortJNIOpt extends SerialPortJNI {

    private boolean is485Dev;

    protected FileInputStream mFileInputStream;

    protected FileDescriptor openDev(String devNum, boolean is485Dev) {
        this.is485Dev = is485Dev;
        if (is485Dev)
            super.mFd = super.open485Dev(devNum);
        if (!is485Dev)
            super.mFd = super.openDev(devNum);
        mFileInputStream = new FileInputStream(super.mFd);
        if (super.mFd == null)
            return null;
        return super.mFd;
    }

    public int setSpeed(FileDescriptor optFd, int speed) {
        return super.setSpeed(optFd, speed);
    }

    public int setParity(FileDescriptor optFd, int dataBit, int stopBit, int parity) {
        return super.setParity(optFd, dataBit, stopBit, parity);
    }

    public int closeDev(FileDescriptor optFd) {
        int retStatus = 0;
        if (optFd != null) {
            if (is485Dev)
                retStatus = super.closeDev(optFd);
            if (!is485Dev)
                retStatus = super.close485Dev(optFd);
            super.mFd = null;
        }
        return retStatus;
    }

    protected boolean writeBytes(byte[] buffer) {
        boolean ret;
        if (is485Dev) {
            super.set485mod(RS485Write);
            ret = super.writeBytes(mFd, buffer, buffer.length);
            super.set485mod(RS485Read);
        } else {
            ret = super.writeBytes(mFd, buffer, buffer.length);
        }
        return ret;
    }

    protected int readBytes(byte[] buffer, int time){
        Log.d("SerialPortJNIOpt", CommonUtils.getInstance().hexToString(buffer));
        return super.readBytes(mFd, buffer, buffer.length, time);
    }
}