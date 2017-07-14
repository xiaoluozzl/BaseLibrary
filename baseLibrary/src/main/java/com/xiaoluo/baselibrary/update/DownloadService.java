package com.xiaoluo.baselibrary.update;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.xiaoluo.baselibrary.common.LibConstant;
import com.xiaoluo.baselibrary.utils.FileUtil;
import com.xiaoluo.baselibrary.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * 后台下载服务
 *
 * author: xiaoluo
 * date: 2017/7/5 16:22
 */
public class DownloadService extends Service {
    private final static String TAG = DownloadService.class.getSimpleName();
    private final static int NOTIFICATION_CODE = 100;  // 更新notification
    private final static int MAX_DOWNLOAD_TIME = 200 * 1000;  // 下载超时时间
    public static boolean isDownloading = false;

    private Context mContext;
    private String mApkDownloadDir = FileUtil.SDPath + "/" + LibConstant.LIB_BASE_URL + "/download/";
    private String mApkName = LibConstant.BASE_NAME + ".apk";
    private int mDownloadProgress = 0;   // 当前下载进度
    private String mUpdateUrl;

    private Retrofit.Builder mRetrofit;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;
        isDownloading = true;
        mUpdateUrl = intent.getStringExtra(LibConstant.IntentKey.UPDATE_URL);
        downloadApk();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelNotification();
    }

    /**
     * 下载apk
     */
    private void downloadApk() {
        if (TextUtils.isEmpty(mUpdateUrl)) {
            return;
        }
        initNotification();
        FileUtil.createDir(mApkDownloadDir);
        FileUtil.deleteFile(mApkDownloadDir + mApkName);
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder();
        }
        mRetrofit.baseUrl("http://o8swwgh2r.bkt.clouddn.com/")
                .client(initOkHttpClient())
                .build()
                .create(ApkDownloadAPI.class)
                .loadFile(mUpdateUrl)
                .enqueue(new ApkDownloadCallback(mApkDownloadDir, mApkName) {

                    @Override
                    public void onSuccess(File file) {
                        // 安装软件
                        cancelNotification();
                        installApk(file);
                    }

                    @Override
                    public void onLoading(long progress, long total) {
                        updateNotification(progress * 100 / total);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Utils.showToast("更新出了点问题,请稍候再试");
                        cancelNotification();
                    }
                });
    }

    /**
     * 初始化OkHttpClient
     */
    private OkHttpClient initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(MAX_DOWNLOAD_TIME, TimeUnit.SECONDS);
        builder.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse
                        .newBuilder()
                        .body(new DownloadBody(originalResponse))
                        .build();
            }
        });
        return builder.build();
    }

    /**
     * 初始化Notification通知
     */
    public void initNotification() {
        mBuilder = new NotificationCompat.Builder(mContext)
                .setContentText("0%")
                .setContentTitle("正在下载图派中...")
                .setProgress(100, 0, false);
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_CODE, mBuilder.build());
    }

    /**
     * 更新通知
     */
    public void updateNotification(long progress) {
        int currProgress = (int) progress;
        if (mDownloadProgress < currProgress) {
            mBuilder.setContentText(progress + "%");
            mBuilder.setProgress(100, (int) progress, false);
            mNotificationManager.notify(NOTIFICATION_CODE, mBuilder.build());
        }
        mDownloadProgress = (int) progress;
    }

    /**
     * 取消通知
     */
    public void cancelNotification() {
        isDownloading = false;
        mNotificationManager.cancel(NOTIFICATION_CODE);
    }

    /**
     * 安装软件
     */
    private void installApk(File file) {
        Uri uri = Uri.fromFile(file);
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        mContext.startActivity(install);
    }

    public interface ApkDownloadAPI {
        @GET
        Call<ResponseBody> loadFile(@Url String url);
    }
}
