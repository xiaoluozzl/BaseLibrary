package com.xiaoluo.baselibrary.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.WindowManager;

import com.squareup.leakcanary.LeakCanary;
import com.xiaoluo.baselibrary.common.LibConstant;
import com.xiaoluo.baselibrary.utils.CrashHandler;
import com.xiaoluo.baselibrary.utils.VersionUtil;

import java.lang.reflect.Field;

/**
 * Application基类
 *
 * author: xiaoluo
 * date: 2017/7/14 10:14
 */
public abstract class LibBaseApplication extends Application {
    protected static Context mLibAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mLibAppContext = this.getApplicationContext();

        // 获取应用名
        getAppName();

        // 注册Crash捕捉上报系统
        CrashHandler.getInstance().init(mLibAppContext);

        // 内存泄漏分析
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        // 获取屏幕宽高和状态栏高度
        getDisplyParas();
    }

    /**
     * 获取应用名
     */
    private void getAppName() {
        String appName = VersionUtil.getAppName();
        if (!TextUtils.isEmpty(appName)) {
            LibConstant.BASE_NAME = appName;
        }
    }

    /**
     * 获取屏幕宽高和状态栏高度
     */
    private void getDisplyParas() {
        // 获取屏幕宽高
        Point outSize = new Point();
        WindowManager windowManager = (WindowManager) mLibAppContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(outSize);
        LibConstant.WIDTH_OF_SCREEN = outSize.x;
        LibConstant.HEIGHT_OF_SCREEN = outSize.y;

        // 状态状宽高
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = mLibAppContext.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        LibConstant.HEIGHT_OF_STATUSBAR = sbar;
    }


    /**
     * 获取AppContext
     */
    public static Context getAppContext() {
        return mLibAppContext;
    }
}
