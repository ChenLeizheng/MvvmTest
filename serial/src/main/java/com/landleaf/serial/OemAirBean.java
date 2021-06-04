package com.landleaf.serial;

public class OemAirBean {

    int address;
    float ch2o;
    int pm25;
    int co2;
    float temp;
    float humidity;
    float voc;
    float co;

    public OemAirBean() {
    }

    public OemAirBean(int address, float ch2o, int pm25, int co2, float temp, float humidity, float voc, float co) {
        this.address = address;
        this.ch2o = ch2o * 0.001f;
        this.pm25 = pm25;
        this.co2 = co2;
        this.temp = temp * 0.1f;
        this.humidity = humidity * 0.1f;
        this.voc = voc * 0.001f;
        this.co = co * 0.01f;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public float getCh2o() {
        return ch2o;
    }

    public void setCh2o(float ch2o) {
        this.ch2o = ch2o;
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

    public float getVoc() {
        return voc;
    }

    public void setVoc(float voc) {
        this.voc = voc;
    }

    public float getCo() {
        return co;
    }

    public void setCo(float co) {
        this.co = co;
    }

    @Override
    public String toString() {
        return "OemAirBean{" +
                "address=" + address +
                ", ch2o=" + ch2o +
                ", pm25=" + pm25 +
                ", co2=" + co2 +
                ", temp=" + temp +
                ", humidity=" + humidity +
                ", voc=" + voc +
                ", co=" + co +
                '}';
    }
}
