package com.landleaf.normal.bean;

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
*/
public class Resource<T> {
    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int LOADING = 2;

    int status;
    T data;
    String message;

    public Resource(){}

    public Resource(int status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
