package com.xiaoluo.baselibrary.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.xiaoluo.baselibrary.common.LibConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 崩溃处理工具
 *
 * author: xiaoluo
 * date: 2017/7/4 17:59
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private final static String TAG = CrashHandler.class.getSimpleName();

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler instance;
    //程序的Context对象
    private Context mContext;

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    //
    private static String CRASH_HEAD;
    private static String versionName;
    private static int versionCode;
    private static String mCrachDir = Environment.getExternalStorageDirectory() + "/" + LibConstant.BASE_NAME + "/crash/";

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (instance == null)
            instance = new CrashHandler();
        return instance;
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(this);

        File dir = new File(mCrachDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        versionName = VersionUtil.getVersionName();
        versionCode = VersionUtil.getVersionCode();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!handleException(e) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(t, e);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Log.e(TAG, "error : ", ex);
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义异常处理
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        //使用Toast来显示异常信息
        Utils.showBar("程序出了点问题...");

        //保存日志文件
        saveFile(ex);
        return true;
    }

    /**
     * 保存错误信息到文件中
     */
    private String saveFile(Throwable ex) {
        long timestamp = System.currentTimeMillis();
        String time = formatter.format(new Date());
        String display = LibConstant.WIDTH_OF_SCREEN + " * " + LibConstant.HEIGHT_OF_SCREEN;
        // 设备信息
        CRASH_HEAD = "\n************* Crash Log Head ************************************************" +
                "\nDevice Manufacturer: " + Build.MANUFACTURER +   // 设备厂商
                "\nDevice Model       : " + Build.MODEL +          // 设备型号
                "\nCPU ABI            : " + Build.CPU_ABI +        // CPU类型
                "\nCPU ABI2           : " + Build.CPU_ABI2 +       // CPU类型
                "\nDisplay            : " + display +              // 屏幕
                "\nAndroid Version    : " + Build.VERSION.RELEASE +// 系统版本
                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +// SDK版本
                "\nApp VersionName    : " + versionName +          // APP版本名
                "\nApp VersionCode    : " + versionCode +          // APP版本号
                "\nCrash Time         : " + time +                 // 时间
                "\n************* Crash Log Head ************************************************\n\n";

        StringBuilder sb = new StringBuilder();
        sb.append(CRASH_HEAD);

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);

        try {
            String fileName = "crash_" + time + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                FileOutputStream fos = new FileOutputStream(mCrachDir + fileName);
                fos.write(sb.toString().getBytes());
                //发送给开发人员
                sendToBack(mCrachDir + fileName);
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

    /**
     * 将错误文件发送到后台
     */
    private void sendToBack(String fileName) {
        if (!new File(fileName).exists()) {
//            Utils.showBar("日志文件不存在!");
            return;
        }
    }
}
