package com.xiaoluo.baselibrary.rxbus;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Rxbus实现事件总线
 *
 * author: xiaoluo
 * date: 2017/6/23 10:59
 */
public class RxBus {
    public static final int RXBUS_FINISH_ACTIVITY = 100;    // 关闭Activity
    public static final int RXBUS_REFRESH_USER_DESC = 110;  // 刷新主页简介
    public static final int RXBUS_REFRESH_USER_NAME = 120;  // 刷新主页昵称
    public static final int RXBUS_SEARCH = 130;             // 搜索
    public static final int RXBUS_REFRESH_PROFILE = 140;    // 刷新个人中心
    public static final int RXBUS_REFRESH_FEED_LIST = 150;  // 刷新动态列表
    public static final int RXBUS_DOWNLOAD_RESULT = 160;    // 下载结果
    public static final int RXBUS_DOWNLOAD_APK = 170;       // 更新下载apk
    public static final int RXBUS_UPDATE = 180;             // 更新

    private static volatile RxBus mInstance;
    private final Subject bus;

    public RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getInstance() {
        RxBus rxBus = mInstance;
        if (mInstance == null) {
            synchronized (RxBus.class) {
                rxBus = mInstance;
                if (mInstance == null) {
                    rxBus = new RxBus();
                    mInstance = rxBus;
                }
            }
        }
        return rxBus;
    }

    /**
     * 提供事件,单一俊发
     */
    public void post(Object o) {
        bus.onNext(o);
    }

    /**
     * 提供事件,可根据code分发
     */
    public void post(int code, Object o) {
        bus.onNext(new RxMessage(code, o));

    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }

    /**
     * 根据传递的code和 eventType 类型返回特定类型(eventType)的 被观察者
     */
    public <T> Observable<T> toObservable(final int code, final Class<T> eventType) {
        return bus.ofType(RxMessage.class)
                .filter(new Func1<RxMessage, Boolean>() {
                    @Override
                    public Boolean call(RxMessage o) {
                        //过滤code和eventType都相同的事件
                        return o.getCode() == code && eventType.isInstance(o.getObject());
                    }
                }).map(new Func1<RxMessage, Object>() {
                    @Override
                    public Object call(RxMessage o) {
                        return o.getObject();
                    }
                }).cast(eventType);
    }


}
