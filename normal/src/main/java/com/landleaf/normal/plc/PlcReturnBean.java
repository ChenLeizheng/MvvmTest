package com.landleaf.normal.plc;

/**
 * Created by thinkpad on 2017/8/18.
 * <p>
 * 返回操作plc的结果
 */
public class PlcReturnBean {
    private int offset;//读取  or 写入地址
    private float res;//读取结果
    private int errorCode;//错误码
    private String errorInfo;//错误信息
    private String ip;//连接plc ip地址

    private boolean ok;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public float getRes() {
        return res;
    }

    public void setRes(float res) {
        this.res = res;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "PlcReturnBean{" +
                "offset=" + offset +
                ", res=" + res +
                ", errorCode=" + errorCode +
                ", errorInfo='" + errorInfo + '\'' +
                ", ip='" + ip + '\'' +
                ", ok=" + ok +
                '}';
    }
}
