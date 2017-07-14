package com.xiaoluo.baselibrary.update;

/**
 * author: xiaoluo
 * date: 2017/7/5 17:08
 */
public class ApkBean {
    private long total;   // 总大小
    private long progress;  // 进度
    private int id;
    private String platform;
    private String latest_version;
    private String min_version;
    private String latest_package_url;

    public String getMin_version() {
        return min_version;
    }

    public void setMin_version(String min_version) {
        this.min_version = min_version;
    }

    public String getLatest_package_url() {
        return latest_package_url;
    }

    public void setLatest_package_url(String latest_package_url) {
        this.latest_package_url = latest_package_url;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLatest_version() {
        return latest_version;
    }

    public void setLatest_version(String latest_version) {
        this.latest_version = latest_version;
    }

    public long getProgress() {
        return progress;
    }

    public long getTotal() {
        return total;
    }

    public ApkBean(long total, long progress) {
        this.total = total;
        this.progress = progress;
    }
}
