package com.xiaoluo.baselibrary.rxbus;

/**
 * author: xiaoluo
 * date: 2017/6/23 11:05
 */
public class RxMessage {
    private int code;
    private Object object;

    public RxMessage() {

    }

    public RxMessage(int code, Object o) {
        this.code = code;
        this.object = o;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
