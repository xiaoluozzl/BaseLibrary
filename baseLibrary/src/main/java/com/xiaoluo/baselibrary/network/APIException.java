package com.xiaoluo.baselibrary.network;

/**
 * 自定义API错误
 * 提示后台info
 *
 * @author: xiaoluo
 * @date: 2017-02-28 16:12
 */
public class APIException extends RuntimeException {
    private String error;
    public APIException(String s) {
        super(s);
    }
}
