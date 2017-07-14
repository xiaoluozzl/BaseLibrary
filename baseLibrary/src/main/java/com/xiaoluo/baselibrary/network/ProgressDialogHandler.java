package com.xiaoluo.baselibrary.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

/**
 * 加载对话框handler
 *
 * @author: xiaoluo
 * @date: 2017-02-28 15:03
 */
public class ProgressDialogHandler extends Handler {
    private final static String TAG = ProgressDialogHandler.class.getSimpleName();

    public static final int SHOW_PROGRESS_DIALOG = 1;    // 显示加载对话框
    public static final int DISMISS_PROGRESS_DIALOG = 2; // 关闭加载对话框

    private Context mContext;
    private ProgressDialog mDialog;
    private boolean cancelable;    // 是否可以取消
    private OnProgressCancelListener mCancelListener; // 对话框取消接口

    public ProgressDialogHandler(Context context, OnProgressCancelListener listener, boolean cancelable) {
        super();
        this.mContext = context;
        this.mCancelListener = listener;
        this.cancelable = cancelable;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }

    /**
     * 关闭加载对话框
     */
    private void dismissProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 初始化加载中对话框
     */
    private void initProgressDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
            // 设置是否可以取消
            mDialog.setCancelable(cancelable);

            if (cancelable) {
                mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mCancelListener.onCancelProgress();
                    }
                });
            }

            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }
    }
}
