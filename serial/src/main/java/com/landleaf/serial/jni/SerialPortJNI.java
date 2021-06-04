package com.landleaf.serial.jni;

import java.io.FileDescriptor;

public class SerialPortJNI {

    static {
        System.loadLibrary("serialport");
    }

    protected FileDescriptor mFd;

    protected static final int RS485Read = 0;

    protected static final int RS485Write = 1;

    protected int cmdWrite;

    protected int cmdRead;

    public native int setSpeed(FileDescriptor fd, int speed);

    public native int setParity(FileDescriptor fd, int dataBits, int stopBits, int parity);

    public native FileDescriptor openDev(String devNum);

    public native FileDescriptor open485Dev(String devNum);

    public native int closeDev(FileDescriptor fd);

    public native int close485Dev(FileDescriptor fd);

    public native int readBytes(FileDescriptor fd, byte[] buffer, int length, int time);

    public native boolean writeBytes(FileDescriptor fd, byte[] buffer, int length);

    public native int set485mod(int mode);
}