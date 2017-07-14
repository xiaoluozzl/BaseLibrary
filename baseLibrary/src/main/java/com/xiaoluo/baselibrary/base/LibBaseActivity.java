package com.xiaoluo.baselibrary.base;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.xiaoluo.baselibrary.rxbus.NetReceiver;

import butterknife.ButterKnife;

/**
 * Activity基类
 *
 * author: xiaoluo
 * date: 2016-12-21 10:14
 */
public abstract class LibBaseActivity extends RxAppCompatActivity implements View.OnClickListener, Handler.Callback {
    private final static String TAG = LibBaseActivity.class.getSimpleName();
    protected Context mContext;
    private NetReceiver mNetworkReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivityContext();
        int layoutId = loadParentLayout();
        if (layoutId != 0) {
            setContentView(layoutId);
        }

        ButterKnife.bind(this);
        initViews();
        initListeners();

        mNetworkReceiver = new NetReceiver();
        //注册网络监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver, filter);

        initLogic();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkReceiver);
    }

    @Override
    public boolean handleMessage(Message msg) {
        return true;
    }

    /**
     * 获取Activity.this
     *
     * @return context
     */
    protected abstract Context getActivityContext();

    /**
     * 加载页面layout
     *
     * @return layoutResId 布局id
     */
    protected abstract int loadParentLayout();

    /**
     * 初始化页面
     */
    protected abstract void initViews();

    /**
     * 初始化监听器
     */
    protected abstract void initListeners();

    /**
     * 初始化逻辑处理
     */
    protected abstract void initLogic();

    public enum ActivityState {
        ACTIVITY_CREATE,
        ACTIVITY_PAUSE,
        ACTIVITY_RESUME,
        ACTIVITY_STOP,
        ACTIVITY_DESTORY
    }
}
