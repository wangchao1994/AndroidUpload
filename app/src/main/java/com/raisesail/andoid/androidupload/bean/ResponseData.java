package com.raisesail.andoid.androidupload.bean;

import java.io.Serializable;

public class ResponseData<T> implements Serializable {
    private static final long serialVersionUID = 5213230387175987834L;

    public int code;
    public String msg;
    private T data;

    @Override
    public String toString() {
        return "ResponseData{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
