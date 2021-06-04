package com.landleaf.normal.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class WeatherModel implements Parcelable,Serializable {

    private String cityName;
    private String week;
    private String date;
    private String calender;

    private String coldLevel;
    private String dressLevel;
    private String humidity;
    private String pm25;
    private String sportLevel;
    private String temp;
    private String updateTime;
    private String uvLevel;
    private String weatherStatus;
    private String windDirection;
    private String windLevel;

    private String picUrl;


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCalender() {
        return calender;
    }

    public void setCalender(String calender) {
        this.calender = calender;
    }

    public String getColdLevel() {
        return coldLevel;
    }

    public void setColdLevel(String coldLevel) {
        this.coldLevel = coldLevel;
    }

    public String getDressLevel() {
        return dressLevel;
    }

    public void setDressLevel(String dressLevel) {
        this.dressLevel = dressLevel;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getSportLevel() {
        return sportLevel;
    }

    public void setSportLevel(String sportLevel) {
        this.sportLevel = sportLevel;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUvLevel() {
        return uvLevel;
    }

    public void setUvLevel(String uvLevel) {
        this.uvLevel = uvLevel;
    }

    public String getWeatherStatus() {
        return weatherStatus;
    }

    public void setWeatherStatus(String weatherStatus) {
        this.weatherStatus = weatherStatus;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindLevel() {
        return windLevel;
    }

    public void setWindLevel(String windLevel) {
        this.windLevel = windLevel;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cityName);
        dest.writeString(this.week);
        dest.writeString(this.date);
        dest.writeString(this.calender);
        dest.writeString(this.coldLevel);
        dest.writeString(this.dressLevel);
        dest.writeString(this.humidity);
        dest.writeString(this.pm25);
        dest.writeString(this.sportLevel);
        dest.writeString(this.temp);
        dest.writeString(this.updateTime);
        dest.writeString(this.uvLevel);
        dest.writeString(this.weatherStatus);
        dest.writeString(this.windDirection);
        dest.writeString(this.windLevel);
        dest.writeString(this.picUrl);
    }

    public WeatherModel() {
    }

    protected WeatherModel(Parcel in) {
        this.cityName = in.readString();
        this.week = in.readString();
        this.date = in.readString();
        this.calender = in.readString();
        this.coldLevel = in.readString();
        this.dressLevel = in.readString();
        this.humidity = in.readString();
        this.pm25 = in.readString();
        this.sportLevel = in.readString();
        this.temp = in.readString();
        this.updateTime = in.readString();
        this.uvLevel = in.readString();
        this.weatherStatus = in.readString();
        this.windDirection = in.readString();
        this.windLevel = in.readString();
        this.picUrl = in.readString();
    }

    public static final Creator<WeatherModel> CREATOR = new Creator<WeatherModel>() {
        @Override
        public WeatherModel createFromParcel(Parcel source) {
            return new WeatherModel(source);
        }

        @Override
        public WeatherModel[] newArray(int size) {
            return new WeatherModel[size];
        }
    };

    @Override
    public String toString() {
        return "WeatherModel{" +
                "cityName='" + cityName + '\'' +
                ", week='" + week + '\'' +
                ", date='" + date + '\'' +
                ", calender='" + calender + '\'' +
                ", coldLevel='" + coldLevel + '\'' +
                ", dressLevel='" + dressLevel + '\'' +
                ", humidity='" + humidity + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", sportLevel='" + sportLevel + '\'' +
                ", temp='" + temp + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", uvLevel='" + uvLevel + '\'' +
                ", weatherStatus='" + weatherStatus + '\'' +
                ", windDirection='" + windDirection + '\'' +
                ", windLevel='" + windLevel + '\'' +
                ", picUrl='" + picUrl + '\'' +
                '}';
    }
}