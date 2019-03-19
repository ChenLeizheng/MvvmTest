package com.landleaf.mvvm.db;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * 市 {"id":31,"name":"沈阳"}
 */

@Entity
public class City {
    @SerializedName("id")
    @Id
    private Long cityCode;
    @SerializedName("name")
    private String cityName;
    private Long provinceId;


    @Generated(hash = 579643614)
    public City(Long cityCode, String cityName, Long provinceId) {
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.provinceId = provinceId;
    }

    @Generated(hash = 750791287)
    public City() {
    }


    @Override
    public String toString() {
        return "City{" +
                "cityCode=" + cityCode +
                ", cityName='" + cityName + '\'' +
                ", provinceId=" + provinceId +
                '}';
    }

    public Long getCityCode() {
        return this.cityCode;
    }

    public void setCityCode(Long cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getProvinceId() {
        return this.provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }
}
