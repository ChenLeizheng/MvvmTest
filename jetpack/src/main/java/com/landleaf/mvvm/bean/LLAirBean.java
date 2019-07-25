package com.landleaf.mvvm.bean;

public class LLAirBean {

    //-20~85℃
    private int temp;
    //0~100%RH
    private int humidity;
    //0~500ug/m³
    private int pm25;
    //0~500ug/m³
    private int co2;
    //125-600ppb   (ppb->mg/m3 将输出值除以1000*1.79)
    private int voc;

    public LLAirBean() {
    }

    public LLAirBean(int temp, int humidity, int pm25, int co2, int voc) {
        this.temp = temp;
        this.humidity = humidity;
        this.pm25 = pm25;
        this.co2 = co2;
        this.voc = voc;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPm25() {
        return pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public int getVoc() {
        return voc;
    }

    public void setVoc(int voc) {
        this.voc = voc;
    }

    @Override
    public String toString() {
        return "LLAirBean{" +
                "temp=" + temp +
                ", humidity=" + humidity +
                ", pm25=" + pm25 +
                ", co2=" + co2 +
                ", voc=" + voc +
                '}';
    }
}
