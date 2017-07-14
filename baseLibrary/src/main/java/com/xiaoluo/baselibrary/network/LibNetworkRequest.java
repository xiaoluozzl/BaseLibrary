package com.xiaoluo.baselibrary.network;

import com.xiaoluo.baselibrary.common.LibConstant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Rx+Retrofit网络请求封装
 *
 * @author: xiaoluo
 * @date: 2017-02-28 14:31
 */
public class LibNetworkRequest {
    private final static String TAG = LibNetworkRequest.class.getSimpleName();

    private static final int DEFAULT_TIMEOUT = 15;
    private static String token = "";

    private Retrofit mRetrofit;
    private APIService mAPIService;
    private static LibNetworkRequest mInstance;

    public static LibNetworkRequest getInstance() {
        if (mInstance == null) {
            mInstance = new LibNetworkRequest();
        }
        return mInstance;
    }

    private LibNetworkRequest() {
        // 设置OkHttpClient并设置超时
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(new CustomInterceptor());
        //错误重连
        builder.retryOnConnectionFailure(true);

        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(LibConstant.LIB_BASE_URL)
                .build();

        mAPIService = mRetrofit.create(APIService.class);
    }

    /**
     * observable统一转换
     */
    private void toSubscribe(Observable observable, Subscriber subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 清除配置
     */
    public void clearNetwork() {
        mInstance = null;
    }

}
