package com.xiaoluo.jcenterdemo;

import android.app.Activity;

import com.squareup.leakcanary.RefWatcher;
import com.xiaoluo.baselibrary.base.LibBaseApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * author: xiaoluo
 * date: 2017/7/12 16:50
 */
public class DemoApp extends LibBaseApplication {

    private static DemoApp instance;
    private RefWatcher refWatcher;

    // 记录当前栈里所有的activity
    private List<Activity> mActivityList = new ArrayList<Activity>();

    public static DemoApp getInstance() {
        if (instance == null) {
            instance = new DemoApp();
        }
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
