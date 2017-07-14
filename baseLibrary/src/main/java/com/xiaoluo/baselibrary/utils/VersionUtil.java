package com.xiaoluo.baselibrary.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.xiaoluo.baselibrary.base.LibBaseApplication;


/**
 * 版本工具类
 *
 * @author: xiaoluo
 * @date: 2016-12-21 15:43
 */
public final class VersionUtil {
    /**
     * 版本是否在2.1之后（API 7）
     */
    public static boolean isECLAIR_MR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1;
    }

    /**
     * 版本是否在2.2之后(API 8)
     */
    public static boolean is8Froyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * 版本是否在2.3之后（API 9）
     */
    public static boolean is9GingerBread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * 版本是否在3.0之后（API 11)
     */
    public static boolean is11Honeycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * 版本是否在3.2之后（API 13)
     */
    public static boolean is13HoneycombMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
    }

    /**
     * 版本是否在4.0之后（API 14)
     */
    public static boolean is14IceScreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * 版本是否再4.1之后(API 16)
     */
    public static boolean is16JellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * 版本是否在5.0之后(API 21)
     */
    public static boolean is21Lollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 版本是否在6.0之后(API 23)
     */
    public static boolean is23Marshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 获取应用名
     */
    public static String getAppName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = LibBaseApplication.getAppContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(LibBaseApplication.getAppContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    /*
    * 获取版本名
    */
    public static String getVersionName() {
        String versionName = null;
        try {
            //获取包管理者
            PackageManager pm = LibBaseApplication.getAppContext().getPackageManager();
            //获取packageInfo
            PackageInfo info = pm.getPackageInfo(LibBaseApplication.getAppContext().getPackageName(), 0);
            //获取versionName
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionName;
    }

    /*
     * 获取版本号
     */
    public static int getVersionCode() {
        int versionCode = 0;
        try {
            //获取包管理者
            PackageManager pm = LibBaseApplication.getAppContext().getPackageManager();
            //获取packageInfo
            PackageInfo info = pm.getPackageInfo(LibBaseApplication.getAppContext().getPackageName(), 0);
            //获取versionCode
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 比较版本号
     *
     * @return 0 : 相同
     * >0: version1 > version2
     * <0: version1 < version2
     */
    public static int compareVersion(String version1, String version2) {
        if (version1.equals(version2)) {
            return 0;
        }

        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");

        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;

        while (index < minLen && (diff = version1Array[index].length() - version2Array[index].length()) == 0//先比较长度
                && (diff = version1Array[index].compareTo(version2Array[index])) == 0) {
            index++;
        }

        if (diff != 0) {
            return diff;
        } else {
            return version1Array.length - version2Array.length > 0 ? 1 : -1;
        }
    }
}
