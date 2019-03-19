package com.landleaf.mvvm.bean;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt")
        public String info;

        @Override
        public String toString() {
            return "More{" +
                    "info='" + info + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Now{" +
                "temperature='" + temperature + '\'' +
                ", more=" + more +
                '}';
    }
}
