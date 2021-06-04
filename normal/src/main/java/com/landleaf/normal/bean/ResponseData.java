package com.landleaf.normal.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ResponseData implements Parcelable {
     int returnCode;

     String returnMsg;

    @Override
    public String toString() {
        return "ResponseData{" +
                "returnCode=" + returnCode +
                ", returnMsg='" + returnMsg + '\'' +
                '}';
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.returnCode);
        dest.writeString(this.returnMsg);
    }

    ResponseData() {
    }

    private ResponseData(Parcel in) {
        this.returnCode = in.readInt();
        this.returnMsg = in.readString();
    }

    public static final Creator<ResponseData> CREATOR = new Creator<ResponseData>() {
        @Override
        public ResponseData createFromParcel(Parcel source) {
            return new ResponseData(source);
        }

        @Override
        public ResponseData[] newArray(int size) {
            return new ResponseData[size];
        }
    };
}
