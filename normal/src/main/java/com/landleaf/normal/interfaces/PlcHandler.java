package com.landleaf.normal.interfaces;

/**
 * Created by thinkpad on 2017/8/18.
 * <p>
 * PLC操作回调
 */
public interface PlcHandler {
    void readRes(int offset, float val);

    void writeRes(int offset, float val);

    void operDebug(String msg);

    void operRes(boolean operRes, String errorCode, int offset);
}