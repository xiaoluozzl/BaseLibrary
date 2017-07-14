package com.xiaoluo.baselibrary.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import com.xiaoluo.baselibrary.common.LibConstant;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 下载工具类
 *
 * @author: xiaoluo
 * @date: 2017-02-17 15:51
 */
public final class ImageIOManager {
    private static final String TAG = ImageIOManager.class.getSimpleName();

    private static ImageIOManager mInstance;

    private final boolean hasSDCard;

    public static final String SD_CARD_PATH = Environment
            .getExternalStorageDirectory().getPath();
    public static final String AVATAR_IMAGE_PATH = SD_CARD_PATH + "/"
            + LibConstant.BASE_NAME + "/" + LibConstant.DIR_AVATAR + "/";
    public static final String IMAGE_SAVED_PATH = SD_CARD_PATH + "/"
            + LibConstant.BASE_NAME + "/" + LibConstant.DIR_SAVED_IMAGE + "/";
    public static final String IMAGE_UPLOAD = SD_CARD_PATH + "/"
            + LibConstant.BASE_NAME + "/" + LibConstant.DIR_UPLOAD + "/";

    private ImageIOManager() {
        // 初始化路径
        hasSDCard = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

        if (hasSDCard) {
            FileUtil.createDir(AVATAR_IMAGE_PATH);
            FileUtil.createDir(IMAGE_SAVED_PATH);
            FileUtil.createDir(IMAGE_UPLOAD);
        }
    }

    public synchronized static ImageIOManager getInstance() {
        if (mInstance == null) {
            mInstance = new ImageIOManager();
        }
        return mInstance;
    }

    /**
     * 保存图片
     *
     * @param fileName 图片名
     * @param image
     * @return 保存成功返回图片的绝对路径；保存失败返回null
     */
    public String saveImage(String fileName, Bitmap image) {
        return saveImage(IMAGE_SAVED_PATH, fileName, image);
    }


    /**
     * 保存头像
     *
     * @param fileName 图片名
     * @param image
     * @return 保存成功返回图片的绝对路径；保存失败返回null
     */
    public String saveAvatar(String fileName, Bitmap image) {
        fileName += ".jpg";
        return saveImage(AVATAR_IMAGE_PATH, fileName, image);
    }

    public String saveUploadImage(String fileName, Bitmap bitmap) {
        fileName += ".jpg";
        return saveImage(IMAGE_UPLOAD, fileName, bitmap);
    }

    private String saveImage(String dir, String fileName, Bitmap image) {
        File file = new File(dir, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable
     * for using as a disk filename.
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static class SavedResult {
        boolean isSuccessful;
        String path;
    }
}
