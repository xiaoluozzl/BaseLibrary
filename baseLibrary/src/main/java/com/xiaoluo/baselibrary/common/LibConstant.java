package com.xiaoluo.baselibrary.common;

/**
 * 常量
 *
 * @author: xiaoluo
 * @date: 2016-12-21 15:04
 */
public class LibConstant {
    public static int WIDTH_OF_SCREEN;  // 手机屏幕宽度
    public static int HEIGHT_OF_SCREEN;  // 手机屏幕高度
    public static int HEIGHT_OF_STATUSBAR; // 状态栏高度

    public static String BASE_NAME = "base-library";
    public static String LIB_BASE_URL = "https://api.douban.com/";

    /**
     * 目录名称
     */
    public static final String DIR_AVATAR = "avatar"; // 头像的保存地址
    public static final String DIR_SAVED_IMAGE = "images"; // 素材保存地址
    public static final String DIR_UPLOAD = "upload"; // 图片上传保存地址
    public static final String DIR_VIDEO = "video"; // 视频上传保存地址

    // Intent Key
    public interface IntentKey {
        String UPDATE_URL = "update_url";
    }

}
