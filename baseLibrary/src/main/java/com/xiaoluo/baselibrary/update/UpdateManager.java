package com.xiaoluo.baselibrary.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.xiaoluo.baselibrary.common.LibConstant;
import com.xiaoluo.baselibrary.rxbus.RxBus;
import com.xiaoluo.baselibrary.utils.Utils;
import com.xiaoluo.baselibrary.utils.VersionUtil;
import com.xiaoluo.baselibrary.widgets.ConfirmDialog;

import static com.xiaoluo.baselibrary.rxbus.RxBus.RXBUS_UPDATE;

/**
 * 更新模块
 *
 * author: xiaoluo
 * date: 2017/7/5 16:51
 */
public class UpdateManager {
    private Context mContext;
    private Intent intent;
    private boolean isShowToast = false;

    public UpdateManager(Context context) {
        this.mContext = context;
        this.intent = new Intent(mContext, DownloadService.class);
    }

    /**
     * 检查更新
     */
    public void checkUpdate() {

    }

    /**
     * 显示更新对话框
     */
    private void showUpdateDialog(final ApkBean apkBean) {
        String versionName = VersionUtil.getVersionName();
        int lastest = VersionUtil.compareVersion(versionName, apkBean.getLatest_version());
        int min = VersionUtil.compareVersion(versionName, apkBean.getMin_version());
        if (lastest == 0) {
            if (isShowToast) {
                Utils.showToast("当前已经是最新版本了");
            }
            RxBus.getInstance().post(RXBUS_UPDATE, "");
            return;
        }
        ConfirmDialog dialog = new ConfirmDialog(mContext);
        //低于最低版本
        if (min == -1) {
            dialog.setMessage("您的版本过低,请更新");
            dialog.setLeftText("退出程序");
            dialog.setRightText("立即更新");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setOnSelectListener(new ConfirmDialog.OnSelectListener() {
                @Override
                public void onLeftSelect() {
                    ((Activity) mContext).finish();
                    System.exit(0);
                }

                @Override
                public void onRightSelect() {
                    intent.putExtra(LibConstant.IntentKey.UPDATE_URL, apkBean.getLatest_package_url());
                    mContext.startService(intent);
                    Utils.showToast("已进入后台下载,请稍候...");
                }
            });
            dialog.show();
            return;
        }

        // 低于最新版本
        if (lastest == -1) {
            dialog.setMessage("发现新版本v" + apkBean.getLatest_version() + "\n是否现在更新?");
            dialog.setLeftText("稍后更新");
            dialog.setRightText("立即更新");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setOnSelectListener(new ConfirmDialog.OnSelectListener() {
                @Override
                public void onLeftSelect() {
                    RxBus.getInstance().post(RXBUS_UPDATE, "");
                }

                @Override
                public void onRightSelect() {
                    intent.putExtra(LibConstant.IntentKey.UPDATE_URL, apkBean.getLatest_package_url());
                    mContext.startService(intent);
                    Utils.showToast("已进入后台下载,请稍候...");
                    RxBus.getInstance().post(RXBUS_UPDATE, "");
                }
            });
            dialog.show();
            return;
        }
    }

    public void showToast(boolean show) {
        this.isShowToast = show;
    }

    public void stop() {
        mContext.stopService(intent);
    }


}
