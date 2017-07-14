package com.xiaoluo.baselibrary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.xiaoluo.baselibrary.rxbus.RxBus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * 下载工具类
 *
 * @author: xiaoluo
 * @date: 2017-02-17 15:51
 */
public class DownloadUtil {
    private final static String TAG = DownloadUtil.class.getSimpleName();

    private static final int DOWNLOAD_SUCCEED = 0;
    private static final int DONWLOAD_FAILED = 1;
    private static final int DOWNLOADING = 2;
    private static Handler mDownloadHandler = null;

    public static Bitmap downloadImage(String url) {
        BufferedInputStream bis = null;
        Bitmap bitmap = null;
        try {
            bis = new BufferedInputStream((new URL(url)).openStream());
            return BitmapFactory.decodeStream(bis);
        } catch (IOException e) {

        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {

                }
            }
        }
        return bitmap;
    }

    /**
     * 下载图片
     */
    public static void downloadImage(final List<String> urls) {
        mDownloadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DOWNLOAD_SUCCEED:
                        RxBus.getInstance().post(RxBus.RXBUS_DOWNLOAD_RESULT, (String) msg.obj);
                        break;
                    case DONWLOAD_FAILED:
                        RxBus.getInstance().post(RxBus.RXBUS_DOWNLOAD_RESULT, "");
                        break;
                    case DOWNLOADING:
//                        listener.onDownloading(progress);
                        break;
                }
            }
        };

        ThreadManager.executeOnNetWorkThread(new Runnable() {
            @Override
            public void run() {

                for (String s : urls) {
                    String[] thumbs = s.split("/");
                    String name = System.currentTimeMillis() + ".png";

                    Bitmap image = DownloadUtil.downloadImage(s);
                    String path = ImageIOManager.getInstance()
                            .saveImage(name, image);

                    Message msg = new Message();
                    if (!TextUtils.isEmpty(path)) {
                        msg.what = DOWNLOAD_SUCCEED;
                        msg.obj = path;
                    } else {
                        msg.what = DONWLOAD_FAILED;
                        msg.obj = "下载失败";
                    }
                    mDownloadHandler.sendMessage(msg);
                }
            }
        });
    }
}
