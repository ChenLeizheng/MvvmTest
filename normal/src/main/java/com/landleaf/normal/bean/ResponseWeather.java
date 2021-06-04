package com.landleaf.normal.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ResponseWeather extends ResponseData implements Parcelable {

    private WeatherModel results;

    @Override
    public String toString() {
        return super.toString() + "ResponseWeather{" +
                "results=" + results +
                '}';
    }


    public WeatherModel getWeatherModel() {
        return results;
    }

    public void setWeatherModel(WeatherModel weatherModel) {
        this.results = weatherModel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.returnCode);
        dest.writeString(this.returnMsg);
        dest.writeParcelable(this.results, flags);
    }

    public ResponseWeather() {
    }

    private ResponseWeather(Parcel in) {
        this.returnCode = in.readInt();
        this.returnMsg = in.readString();
        this.results = in.readParcelable(WeatherModel.class.getClassLoader());
    }

    public static final Creator<ResponseWeather> CREATOR = new Creator<ResponseWeather>() {
        @Override
        public ResponseWeather createFromParcel(Parcel source) {
            return new ResponseWeather(source);
        }

        @Override
        public ResponseWeather[] newArray(int size) {
            return new ResponseWeather[size];
        }
    };
}
