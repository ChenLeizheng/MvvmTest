package com.landleaf.normal.bean;


public class NtAllId {
    private int jiashiId;//0关 1开   2014
    private int hourId;
    private int clearId;//写1清零
    private int powerId;//0关 1开  2004
    private int modeId;//0 夏季 1冬季 2通风  2006
    private int lijiaId;//0居家 1离家  2008
    private int volumeId;//高 1 低 0  2010

    public NtAllId(int jiashiId, int hourId, int clearId, int powerId, int modeId, int lijiaId, int volumeId) {
        this.jiashiId = jiashiId;
        this.hourId = hourId;
        this.clearId = clearId;
        this.powerId = powerId;
        this.modeId = modeId;
        this.lijiaId = lijiaId;
        this.volumeId = volumeId;
    }

    public int getJiashiId() {
        return jiashiId;
    }

    public void setJiashiId(int jiashiId) {
        this.jiashiId = jiashiId;
    }

    public int getHourId() {
        return hourId;
    }

    public void setHourId(int hourId) {
        this.hourId = hourId;
    }

    public int getClearId() {
        return clearId;
    }

    public void setClearId(int clearId) {
        this.clearId = clearId;
    }

    public int getPowerId() {
        return powerId;
    }

    public void setPowerId(int powerId) {
        this.powerId = powerId;
    }

    public int getModeId() {
        return modeId;
    }

    public void setModeId(int modeId) {
        this.modeId = modeId;
    }

    public int getLijiaId() {
        return lijiaId;
    }

    public void setLijiaId(int lijiaId) {
        this.lijiaId = lijiaId;
    }

    public int getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(int volumeId) {
        this.volumeId = volumeId;
    }

    @Override
    public String toString() {
        return "NtAllId{" +
                "jiashiId=" + jiashiId +
                ", hourId=" + hourId +
                ", clearId=" + clearId +
                ", powerId=" + powerId +
                ", modeId=" + modeId +
                ", lijiaId=" + lijiaId +
                ", volumeId=" + volumeId +
                '}';
    }
}
