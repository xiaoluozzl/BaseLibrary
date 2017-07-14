package com.xiaoluo.baselibrary.base;

/**
 * 基础返回数据
 *
 * @author: xiaoluo
 * @date: 2017-02-28 10:07
 */
public class LibBaseResponse<T> {
    public int ret;
    public int code;
    public String info;
    public String token;
    public T data;
}
