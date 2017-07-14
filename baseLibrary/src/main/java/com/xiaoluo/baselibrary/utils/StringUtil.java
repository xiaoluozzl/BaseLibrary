package com.xiaoluo.baselibrary.utils;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author: xiaoluo
 * @date: 2017-01-17 17:25
 */
public class StringUtil {

    /**
     * 日期转换
     */
    public static String transUTCTime(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
            long timeStemp = date.getTime();
            return transTime(timeStemp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 日期转换
     */
    public static String transTime(long timeStemp) {
        String result = "";

        StringBuffer sb = new StringBuffer();
        long time = System.currentTimeMillis() - (timeStemp);
        long mill = (long) Math.ceil(time / 1000) - 1;// 秒前
        long minute = (long) Math.ceil(time / 60 / 1000.0f) - 1;// 分钟前
        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f) - 1;// 小时
        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f) - 1;// 天前

        if (day > 0) {
            if (day > 7) {
                sb.append(toLocalTime(timeStemp));
            } else {
                sb.append(day + "天");
            }
        } else if (hour > 0) {
            if (hour >= 24) {
                sb.append("1天");
            } else {
                sb.append(hour + "小时");
            }
        } else if (minute > 0) {
            if (minute == 60) {
                sb.append("1小时");
            } else {
                sb.append(minute + "分钟");
            }
        } else if (mill > 0) {
            if (mill == 60) {
                sb.append("1分钟");
            } else {
                sb.append(mill + "秒");
            }
        } else {
            sb.append("刚刚");
        }
        if (!sb.toString().equals("刚刚") && (day <= 7)) {
            sb.append("前");
        }
        result = sb.toString();

        return result;
    }

    public static String toLocalTime(long time) {
        // Long timestamp = Long.parseLong(unix) * 1000;
        String date = new SimpleDateFormat("MM月dd日 HH:mm")
                .format(time);
        return date;
    }

    /**
     * 生成md5
     * @param str
     * @return
     */
    public static String toMd5(String str) {

        String md5str = "";
        try {
            //1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");

            //2 将消息变成byte数组
            byte[] input = str.getBytes();

            //3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);

            //4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = bytesToHex(buff);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    /**
     * 二进制转十六进制
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        //把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }


    /**
     * 获取点赞数、评论数和分享数显示的文字 目前的显示逻辑是少于1万显示具体的数字，大于1万显示X万
     * @return
     */
    public static String getCountDisplayText(int count) {
        if (count < 10000) {
            return String.valueOf(count);
        } else {
            int k = count / 10000;
            return (k + "W");
        }
    }



    /**
     *  匹配字符串
     */
    public static boolean matchEqual(String value, String keyword) {
        if (value == null || keyword == null)
            return false;
        if (keyword.length() > value.length())
            return false;

        int i = 0, j = 0;
        do {
            if (keyword.charAt(j) == value.charAt(i)) {
                i++;
                j++;
            } else if (j > 0)
                break;
            else
                i++;

        } while (i < value.length() && j < keyword.length());

        return (j == keyword.length()) ? true : false;
    }

    /**
     * 返回一个定长的随机字符串(包含大小写字母,数字,特殊符号)
     */
    public static String generateString(int length) {
        String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ#@$%!&*";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(allChar.charAt(random.nextInt(allChar.length())));
        }
        return sb.toString();
    }

    /**
     * 校验手机号格式
     */
    public static boolean matchPhoneNum(String num) {
        Pattern p = Pattern.compile("\\d{11}$");
        Matcher m = p.matcher(num);
        return m.matches();
    }
}
