package com.landleaf.mvvm.db;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ErrorCode {
    @Id(autoincrement = true)
    private Long id;
    @SerializedName("register_address")
    public int registerAddress;
    @SerializedName("register_index")
    public int registerIndex;
    @SerializedName("error_code")
    public String errorCode;

    @Generated(hash = 1940532126)
    public ErrorCode(Long id, int registerAddress, int registerIndex,
            String errorCode) {
        this.id = id;
        this.registerAddress = registerAddress;
        this.registerIndex = registerIndex;
        this.errorCode = errorCode;
    }

    @Generated(hash = 165101139)
    public ErrorCode() {
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "id=" + id +
                ", registerAddress=" + registerAddress +
                ", registerIndex=" + registerIndex +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRegisterAddress() {
        return this.registerAddress;
    }

    public void setRegisterAddress(int registerAddress) {
        this.registerAddress = registerAddress;
    }

    public int getRegisterIndex() {
        return this.registerIndex;
    }

    public void setRegisterIndex(int registerIndex) {
        this.registerIndex = registerIndex;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
