package com.landleaf.normal.bean;

public class SensorBean {

    float voc;
    float temp;
    float humidity;
    float co2;
    float pm25;

    public SensorBean(float voc, float temp, float humidity, float co2, float pm25) {
        this.voc = voc;
        this.temp = temp;
        this.humidity = humidity;
        this.co2 = co2;
        this.pm25 = pm25;
    }

    public float getVoc() {
        return voc;
    }

    public void setVoc(float voc) {
        this.voc = voc;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getCo2() {
        return co2;
    }

    public void setCo2(float co2) {
        this.co2 = co2;
    }

    public float getPm25() {
        return pm25;
    }

    public void setPm25(float pm25) {
        this.pm25 = pm25;
    }

    @Override
    public String toString() {
        return "SensorBean{" +
                "voc=" + voc +
                ", temp=" + temp +
                ", humidity=" + humidity +
                ", co2=" + co2 +
                ", pm25=" + pm25 +
                '}';
    }
}
