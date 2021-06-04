package com.landleaf.serial;

/**
 * KF-800F新风控制器属性状态
 */
public class KFWindStatus {
    //工作模式 数据范围： 0－3 ； 0：关机 1：自动模式 2：手动模式 3：定时模式
    private int runningMode;
    //当前风速 数据范围： 0、 1、 2、 3； 0：停止 1：低速 2：中速 3：高速
    private int windSpeed;
    //当前 CO2 浓度 数据范围： 350——3000
    private int CO2;
    //当前温度 数据范围： 0——50
    private int temp;

    public KFWindStatus() {
    }

    public KFWindStatus(int runningMode, int windSpeed, int CO2, int temp) {
        this.runningMode = runningMode;
        this.windSpeed = windSpeed;
        this.CO2 = CO2;
        this.temp = temp;
    }

    public int getRunningMode() {
        return runningMode;
    }

    public void setRunningMode(int runningMode) {
        this.runningMode = runningMode;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getCO2() {
        return CO2;
    }

    public void setCO2(int CO2) {
        this.CO2 = CO2;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "KFWindStatus{" +
                "runningMode=" + runningMode +
                ", windSpeed=" + windSpeed +
                ", CO2=" + CO2 +
                ", temp=" + temp +
                '}';
    }
}
