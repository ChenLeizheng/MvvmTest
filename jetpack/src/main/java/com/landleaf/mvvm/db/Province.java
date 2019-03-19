package com.landleaf.mvvm.db;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 省 {"id":1,"name":"北京"}
 * Gson使用@SerializedName注解来将对象里的属性跟json里字段对应值匹配起来
 */
@Entity
public class Province {
    @SerializedName("id")
    @Id
    private Long provinceCode;
    @SerializedName("name")
    private String provinceName;

    @Generated(hash = 837189159)
    public Province(Long provinceCode, String provinceName) {
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
    }

    @Generated(hash = 1309009906)
    public Province() {
    }

    @Override
    public String toString() {
        return "Province{" +
                "provinceName='" + provinceName + '\'' +
                ", provinceCode=" + provinceCode +
                '}';
    }

    public Long getProvinceCode() {
        return this.provinceCode;
    }

    public void setProvinceCode(Long provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return this.provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
