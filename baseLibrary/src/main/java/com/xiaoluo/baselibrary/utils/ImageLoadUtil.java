package com.xiaoluo.baselibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xiaoluo.baselibrary.R;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.xiaoluo.baselibrary.utils.ImageLoadUtil.LoadOption.LOAD_AVATAR;
import static com.xiaoluo.baselibrary.utils.ImageLoadUtil.LoadOption.LOAD_CORNER;
import static com.xiaoluo.baselibrary.utils.ImageLoadUtil.LoadOption.LOAD_MEDIUM;
import static com.xiaoluo.baselibrary.utils.ImageLoadUtil.LoadOption.LOAD_ORIGIN;
import static com.xiaoluo.baselibrary.utils.ImageLoadUtil.LoadOption.LOAD_SMALL;


/**
 * 图片加载工具(Glide)
 *
 * @author: xiaoluo
 * @date: 2017-02-22 11:47
 */
public class ImageLoadUtil {
    private final static String TAG = ImageLoadUtil.class.getSimpleName();

    // 加载选项
    public enum LoadOption {
        LOAD_ORIGIN,  // 原图
        LOAD_MEDIUM,  // 中图
        LOAD_SMALL,   // 小图
        LOAD_AVATAR,  // 头像
        LOAD_BANNER,  // Banner
        LOAD_BLUR,    // 毛玻璃
        LOAD_CORNER,  // 圆角图片
        LOAD_GIF,     // GIF
    }

    // 优先考虑传入context,不采用appContext,减少不必要的资源占用
    private static ImageLoadUtil sImageLoadUtil;

    /**
     * 单例模式
     */
    public static ImageLoadUtil getInstance() {
        if (sImageLoadUtil == null) {
            sImageLoadUtil = new ImageLoadUtil();
        }
        return sImageLoadUtil;
    }

    /**
     * 普通默认加载
     */
    public void loadImage(String url, ImageView imageView) {
        initLoadImage(Glide.with(imageView.getContext()), imageView.getContext(), url, imageView, LOAD_ORIGIN, null);
    }

    /**
     * 加载头像
     */
    public void loadAvatar(String url, ImageArea imageArea) {
        initLoadImage(Glide.with(imageArea.getImage().getContext()), imageArea.getImage().getContext(), url, imageArea.getImage(), LOAD_AVATAR, null);
    }


    /**
     * 可选择加载
     */
    public void loadImage(String url, ImageView imageView, LoadOption option) {
        initLoadImage(Glide.with(imageView.getContext()), imageView.getContext(), url, imageView, option, null);
    }

    /**
     * 加载添加完成监听
     */
    public void loadImage(String url, ImageView imageView, OnLoadListener listener) {
        initLoadImage(Glide.with(imageView.getContext()), imageView.getContext(), url, imageView, LOAD_ORIGIN, listener);
    }


    /**
     * 统一加载方法
     * 官方推荐使用RequestManager,可以和Actvity生命周期同步
     */
    private void initLoadImage(RequestManager glide, Context context, String url, final ImageView imageView, LoadOption option, final OnLoadListener listener) {
        // You cannot start a load for a destroyed activity
        // 子线程加载图片, context不用appcontext时,在activity结束后会报此错,加个判断
        if (context instanceof Activity && ((Activity) context).isDestroyed()) {
            return;
        }


        switch (option) {
            // 默认头像加载
            case LOAD_AVATAR:
                glide.load(getSizeUrl(url, LOAD_AVATAR))
                        .centerCrop()
                        .override(200, 200)
                        .placeholder(R.drawable.head_portrait)
                        .error(R.drawable.head_portrait)
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(imageView);
                break;
            // 圆角图片
            case LOAD_CORNER:
                glide.load(getSizeUrl(url, LOAD_CORNER))
                        .centerCrop()
                        .override(200, 200)
                        .error(R.drawable.head_portrait)
                        .bitmapTransform(new RoundedCornersTransformation(context, 30, 0, RoundedCornersTransformation.CornerType.ALL))
                        .into(imageView);
                break;
            // 小图加载
            case LOAD_SMALL:
                glide.load(getSizeUrl(url, LOAD_SMALL))
                        .override(300, 300)
                        .placeholder(R.drawable.ic_zhanwei)
                        .error(R.drawable.ic_lietu)
                        .into(imageView);
                break;
            // 中图加载
            case LOAD_MEDIUM:
                glide.load(getSizeUrl(url, LOAD_MEDIUM))
                        .thumbnail(0.1f)
                        .override(500, 500)
                        .error(R.drawable.ic_lietu)
                        .into(imageView);
                break;
            // 原图加载
            case LOAD_ORIGIN:
                if (listener != null) {
                    glide.load(url)
                            .asBitmap()
                            .placeholder(R.drawable.ic_zhanwei)
                            .error(R.drawable.ic_lietu)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    imageView.setImageBitmap(resource);
                                    listener.onReady(resource, imageView);
                                }
                            });
                } else {
                    glide.load(url)
//                            .placeholder(R.drawable.ic_zhanwei)
                            .error(R.drawable.ic_lietu)
                            .thumbnail(0.1f)
                            .into(imageView);
                }
                break;
            // Banner
            case LOAD_BANNER:
                glide.load(url)
                        .placeholder(R.drawable.banner_zhanwei)
                        .error(R.drawable.banner_zhanwei)
                        .into(imageView);
                break;
            // 毛玻璃
            case LOAD_BLUR:
                glide.load(getSizeUrl(url, LOAD_CORNER))
                        .centerCrop()
                        .override(200, 200)
                        .error(R.drawable.ic_zhanwei)
                        .bitmapTransform(new BlurTransformation(context))
                        .into(imageView);
                break;
            case LOAD_GIF:
                glide.load(url)
                        .asGif()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageView);
                break;

            default:
                glide.load(url)
                        .override(200, 200)
                        .placeholder(R.drawable.ic_zhanwei)
                        .error(R.drawable.ic_lietu)
                        .into(imageView);
                break;
        }
    }


    /**
     * 图片链接尺寸后缀添加
     */
    private String getSizeUrl(String url, LoadOption size) {

        return url;
    }

    /**
     * 清理缓存
     */
    public void clearMemory(final Context context) {
        Glide.get(context).clearMemory();  // 在UI线程执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();  // 在子线程执行
            }
        }).start();
    }

    /**
     * 加载完成回调
     */
    public interface OnLoadListener {
        void onReady(Bitmap bitmap, ImageView imageView);
    }

    /**
     * 获取ImageView
     */
    public interface ImageArea {
        ImageView getImage();
    }
}
