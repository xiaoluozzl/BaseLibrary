package com.xiaoluo.baselibrary.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 添加统一参数
 * verify验证参数
 * token头
 *
 * 如果只有token的话,也可以直接在NetworkRequest添加
 *
 * author: xiaoluo
 * date: 2017-03-15 10:45
 */
public class CustomInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        HttpUrl url = original.url().newBuilder()
                .build();

        // 添加token
        Request request = original.newBuilder()
                .addHeader("token", "")
                .method(original.method(), original.body())
                .url(url)
                .build();

        return chain.proceed(request);
    }

}

