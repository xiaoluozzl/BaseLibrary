package com.xiaoluo.baselibrary.network;

import com.xiaoluo.baselibrary.base.LibBaseResponse;

import rx.functions.Func1;

/**
 * Retrofit数据预处理
 * 感觉在这里预处理比较好
 * 无法解决同一接口成功和失败data返回格式不同的问题
 * 可以改在Gson解析时预处理,稍麻烦一些
 *
 * @author: xiaoluo
 * @date: 2017-02-28 15:57
 */
public class ResponseFunc<T> implements Func1<LibBaseResponse<T>, T> {

    @Override
    public T call(LibBaseResponse<T> tLibBaseResponse) {

        return tLibBaseResponse.data;
    }


}
