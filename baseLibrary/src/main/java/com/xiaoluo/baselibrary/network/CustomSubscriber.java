package com.xiaoluo.baselibrary.network;

import android.content.Context;

import com.xiaoluo.baselibrary.R;
import com.xiaoluo.baselibrary.utils.Utils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * 订阅者
 * 集成加载对话框, 统一错误提示
 *
 * @author: xiaoluo
 * @date: 2017-02-28 15:15
 */
public abstract class CustomSubscriber<T> extends Subscriber<T> implements OnProgressCancelListener {
    private final static String TAG = CustomSubscriber.class.getSimpleName();

    private Context mContext;
    private ProgressDialogHandler mHandler;

    private boolean isShowProgress = true;  // 是否显示加载中对话框,默认否

    public CustomSubscriber(Context context) {
        this.mContext = context;
        mHandler = new ProgressDialogHandler(context, this, true);
    }

    /**
     * 订阅开始是调用onStart, 显示加载中对话框
     */
    @Override
    public void onStart() {
        super.onStart();
        if (isShowProgress) {
            showProgressDialog();
        }
    }

    @Override
    public void onCompleted() {
        if (isShowProgress) {
            dismissProgressDialog();
        }
    }


    @Override
    public void onError(Throwable e) {

        if (e instanceof SocketTimeoutException) {
            Utils.showToast(mContext.getResources().getString(R.string.error_network));
        } else if (e instanceof ConnectException) {
            Utils.showToast(mContext.getResources().getString(R.string.error_network));
        } else if (e instanceof APIException) {
            // 自定义错误
            Utils.showToast(e.getMessage());
        } else {
            Utils.showToast("NET ERROR : " + e.getMessage());
        }


        if (isShowProgress) {
            dismissProgressDialog();
        }

        requestError(e.getMessage());
    }

    @Override
    public void onNext(T t) {
        requestSuccess(t);
    }

    @Override
    public void onCancelProgress() {
        // 取消了加载对话框,同时取消订阅
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }

    /**
     * 取消网络请求
     */
    public void cancelRequest() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }

    /**
     * 显示加载中对话框
     */
    private void showProgressDialog() {
        if (mHandler != null) {
            mHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    /**
     * 关闭加载中对话框
     */
    private void dismissProgressDialog() {
        if (mHandler != null) {
            mHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mHandler = null;
        }
    }


    /**
     * 是否显示加载中
     */
    public CustomSubscriber<T> setShowProgress(boolean showProgress) {
        this.isShowProgress = showProgress;
        return this;
    }

    /**
     * 请求成功回调
     */
    protected abstract void requestSuccess(T t);

    /**
     * 请求失败回调, 已经统一封装了Toast, 一般不需要再toast
     */
    protected abstract void requestError(String error);

    ;
}
