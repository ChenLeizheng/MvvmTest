package com.landleaf.normal.bean;

/**
 * Created by Chenyifei on 2017/5/9.
 * <p>
 * 暖通描述
 */
public class NtTempId {
    private int setTempId;
    private int showTempId;
    private int showSetTempId;
    private int humidityId;
    private String roomName;

    public NtTempId() {
    }

    //实际温度 实际湿度 设定温度 发送设定温度
    public NtTempId(int showTempId, int humidityId, int showSetTempId, int setTempId, String roomName) {
        this.setTempId = setTempId;
        this.showTempId = showTempId;
        this.showSetTempId = showSetTempId;
        this.humidityId = humidityId;
        this.roomName = roomName;
    }

    public int getSetTempId() {
        return setTempId;
    }

    public void setSetTempId(int setTempId) {
        this.setTempId = setTempId;
    }

    public int getShowTempId() {
        return showTempId;
    }

    public void setShowTempId(int showTempId) {
        this.showTempId = showTempId;
    }

    public int getShowSetTempId() {
        return showSetTempId;
    }

    public void setShowSetTempId(int showSetTempId) {
        this.showSetTempId = showSetTempId;
    }

    public int getHumidityId() {
        return humidityId;
    }

    public void setHumidityId(int humidityId) {
        this.humidityId = humidityId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public String toString() {
        return "NtTempId{" +
                "setTempId=" + setTempId +
                ", showTempId=" + showTempId +
                ", showSetTempId=" + showSetTempId +
                ", humidityId=" + humidityId +
                ", roomName='" + roomName + '\'' +
                '}';
    }
}
