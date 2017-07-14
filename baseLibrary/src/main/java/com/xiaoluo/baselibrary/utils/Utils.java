package com.xiaoluo.baselibrary.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.xiaoluo.baselibrary.base.LibBaseApplication;
import com.xiaoluo.baselibrary.widgets.TopSnackBar;

import java.util.Locale;

/**
 * 系统工具类
 *
 * @author: xiaoluo
 * @date: 2016-12-21 15:01
 */
public final class Utils {
    private static Toast toast = null;

    /**
     * snackbar提示
     */
    public static void showBar(String text) {
        TopSnackBar.make(LibBaseApplication.getAppContext(), text, TopSnackBar.LENGTH_SHORT).show();
    }

    /**
     * 单例toast提示,短时间
     */
    public static void showToast(String text) {
        if (toast == null) {
            toast = Toast.makeText(LibBaseApplication.getAppContext(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 单例toast提示,自定义时间
     */
    public static void showToast(String text, int time) {
        if (toast == null) {
            toast = Toast.makeText(LibBaseApplication.getAppContext(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.setDuration(time);
        toast.show();
    }


    /**
     * 获得基于xxhdpi的屏幕宽高比
     */
    public static float getWidthScale(Context context) {
        float result;
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(outMetrics);
        result = pxToDpFloat(context, outMetrics.widthPixels) / pxToDpOrigin(context, 1080f);
        return result;
    }

    /**
     * px转dp(float)
     */
    public static float pxToDpFloat(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    /**
     * px转dp(int)
     */
    public static int pxToDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 以xxhdpi为基准，将px转化为dp
     */
    public static float pxToDpOrigin(Context context, float dpValue) {
        return dpValue / 3 + 0.5f;
    }

    /**
     * dp转换成像素(int)
     */
    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 判断系统语言是否中文
     */
    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh")) {
            return true;
        } else {
            return false;
        }
    }
}
