package com.xiaoluo.baselibrary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.view.View;

import com.xiaoluo.baselibrary.common.LibConstant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * bitmap工具类
 *
 * @author: xiaoluo
 * @date: 2017-01-17 12:07
 */
public class BitmapUtil implements Handler.Callback {
    private static final String TAG = BitmapUtil.class.getSimpleName();
    public static LruCache<String, Bitmap> lruCache;
    private static Handler mHandler = new Handler(new BitmapUtil());
    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);


    static {
//		int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int maxSize = 1 * 1024 * 1024;
        lruCache = new LruCache<String, Bitmap>(maxSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    /**
     * @param options   参数
     * @param reqWidth  目标的宽度
     * @param reqHeight 目标的高度
     * @return
     * @description 计算图片的压缩比率
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        while ((width > reqWidth) || (height > reqHeight)) {
            width >>= 1;
            height >>= 1;
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }

    // 录制页，压缩比 PREVIEW_WIDTH = 640;
    private static int calculateRECInSampleSize(BitmapFactory.Options options) {
        int inSampleSize = options.outWidth / 640;
        return inSampleSize;
    }

    /**
     * @param src
     * @param dstWidth
     * @param dstHeight
     * @return
     * @description 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     */
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth,
                                            int dstHeight, int inSampleSize) {
        // 如果inSampleSize是2的倍数，也就说这个src已经是我们想要的缩略图了，直接返回即可。
        if (inSampleSize % 2 == 0) {
            return src;
        }
        // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    public static Bitmap decodeBitmap(String path) {
        return decodeBitmap(path, LibConstant.WIDTH_OF_SCREEN,
                LibConstant.HEIGHT_OF_SCREEN, null);
    }

    public static Bitmap decodeBitmap(String path, int reqWidth, int reqHeight) {
        return decodeBitmap(path, reqWidth, reqHeight, null);
    }

    public static Bitmap decodeBitmap(String path, int reqWidth, int reqHeight,
                                      DecodeErrorListener errorListener) {
        if ((reqWidth <= 0) || (reqHeight <= 0)) {
            String msg = new StringBuilder("Error, reqWidth=")
                    .append(String.valueOf(reqWidth)).append(" reqHeight=")
                    .append(String.valueOf(reqHeight)).toString();
            throw new IllegalArgumentException(msg);
        }

        // 获取图片的宽和高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError e1) {
            e1.printStackTrace();
            if (errorListener != null) {
                errorListener.onDecodeError();
            }
            // 再缩小一倍大小，解码一次
            options.inSampleSize *= 2;
            try {
                bitmap = BitmapFactory.decodeFile(path, options);
            } catch (OutOfMemoryError e2) {
                e1.printStackTrace();
                if (errorListener != null) {
                    errorListener.onDecodeError();
                }
                // TODO 用裂图吧
                bitmap = null;
            }
        }
        return bitmap;
    }

    public static Bitmap RECDecodeBitmap(String path) {
        // 获取图片的宽和高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateRECInSampleSize(options);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError e1) {
            e1.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    public static interface DecodeErrorListener {
        public void onDecodeError();
    }

    // 对图片进行处理，得到毛玻璃效果
    public static void setBlurBitmap(final Bitmap bitmap, final View view, final String url) {
        if (bitmap == null || view == null || url == null) {
            return;
        }
        view.setTag(url);
        if (lruCache.get(bitmap.toString()) == null) {
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    // 图片缩放比例 TODO 做成参数可配置
                    float scaleFactor = 8;
                    // 模糊程度
                    float radius = 10;

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();

                    Bitmap bluredBitmap = Bitmap.createBitmap((int) (width / scaleFactor),
                            (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bluredBitmap);
                    canvas.scale(1 / scaleFactor, 1 / scaleFactor);
//                Log.e("bitmap", lruCache.maxSize() + "======" + lruCache.size());
                    Paint paint = new Paint();
                    paint.setFlags(Paint.FILTER_BITMAP_FLAG);
                    canvas.drawBitmap(bitmap, 0, 0, paint);

                    bluredBitmap = doBlur(bluredBitmap, (int) radius, true);

                    lruCache.put(bitmap.toString(), bluredBitmap);

                    Message message = new Message();
                    message.what = 1;
                    message.obj = view;
                    Bundle bundle = new Bundle();
                    bundle.putString("url", url);
                    bundle.putString("key", bitmap.toString());
                    bundle.putParcelable("bitmap", bitmap);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            });
        } else {
            if (view.getTag().toString().split("\\?")[0]
                    .equals(url.split("\\?")[0])) {
                view.setBackgroundDrawable(new BitmapDrawable(lruCache.get(bitmap.toString())));
            }
        }

    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 1:
                View view = (View) message.obj;
                String url = message.getData().getString("url");
                String key = message.getData().getString("key");
                Bitmap oriBitmap = message.getData().getParcelable("bitmap");
                Bitmap bitmap = lruCache.get(key);
                if (view.getTag().toString().split("\\?")[0].
                        equals(url.split("\\?")[0])) {
                    if (bitmap != null) {
                        view.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    } else {
                        setBlurBitmap(oriBitmap, view, url);
                    }
                } else {
                }
                break;
        }
        return true;
    }

    public static Bitmap ratio(Bitmap image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if (os.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = LibConstant.WIDTH_OF_SCREEN;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = LibConstant.WIDTH_OF_SCREEN;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * 图片毛玻璃化
     */
    public static Bitmap doBlur(Bitmap sentBitmap, int radius,
                                boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

}