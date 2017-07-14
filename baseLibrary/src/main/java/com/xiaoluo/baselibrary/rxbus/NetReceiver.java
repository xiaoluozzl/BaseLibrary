package com.xiaoluo.baselibrary.rxbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoluo.baselibrary.R;
import com.xiaoluo.baselibrary.utils.NetworkUtil;
import com.xiaoluo.baselibrary.utils.Utils;

/**
 * 监听网络状态
 *
 * @author: xiaoluo
 * @date: 2017-01-22 16:17
 */
public class NetReceiver extends BroadcastReceiver {
    private int networkType;

    public NetReceiver(int networkType) {
        this.networkType = networkType;
    }

    public NetReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!NetworkUtil.isNetworkConnected(context)) {
            Utils.showToast(context.getResources().getString(R.string.error_no_network));
        }
    }
}
