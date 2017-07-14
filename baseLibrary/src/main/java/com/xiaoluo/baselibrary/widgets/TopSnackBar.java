package com.xiaoluo.baselibrary.widgets;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaoluo.baselibrary.R;

import java.util.LinkedList;

/**
 * 顶部提示栏
 * 使用方法同toast,需调用show
 * 感谢 http://blog.csdn.net/forwordlove/article/details/52800160
 *
 * @author: xiaoluo
 * @date: 2017-01-23 17:10
 */
public class TopSnackBar {
    public static final int LENGTH_LONG = 3500;
    public static final int LENGTH_SHORT = 2000;
    private static final int MSG_HIDE = 0;
    private static final long NEXT_WAIT = 100;
    private static TopSnackBar mTopSnackBarInstance;
    private static Context mContext;
    private static WindowManager mWindowManager;
    private static Handler mHandler;
    private int mDuration;
    private String mSnackBarContent;
    private View mLayout;
    private WindowManager.LayoutParams mLayoutParams;
    private TextView mContentTv;
    private volatile boolean mIsShowing;
    private LinkedList<ShowTask> mTaskQueue = new LinkedList();

    static {
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_HIDE:
                        return true;
                }
                return false;
            }
        });
    }

    /**
     *  设置snackbar
     */
    public static TopSnackBar make(Context context, String snackBarContent, int duration) {
        initWindowManager(context);
        initTopSnackBar(snackBarContent, duration);
        return mTopSnackBarInstance;
    }

    private static void initWindowManager(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    private static void initTopSnackBar(String snackBarContent, int duration) {
        if (mTopSnackBarInstance == null) {
            mTopSnackBarInstance = new TopSnackBar();
        }
        makeSnackBarContent(snackBarContent, duration);
    }

    private static void makeSnackBarContent(String snackBarContent, int duration) {
        mTopSnackBarInstance.setContent(snackBarContent);
        mTopSnackBarInstance.setDuration(duration);
    }

    private void setLayoutParam() {
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.top_snack_bar_height);
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.windowAnimations = R.style.SnackBarAnimStyle;
        mLayoutParams.format= PixelFormat.RGBA_8888;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
    }

    private void setLayout(int layout) {
        mLayout = LayoutInflater.from(mContext).inflate(layout, null, false);
        mContentTv = (TextView) mLayout.findViewById(R.id.content_tv);
        mContentTv.setText(mSnackBarContent);
    }

    private void setDuration(int duration) {
        mDuration = duration;
    }

    private void setContent(String snackBarContent) {
        mSnackBarContent = snackBarContent;
    }

    /**
     *  设置snackbar显示
     */
    public void show() {
        if (!isShowing()) {
            setShowing(true);
            makeSnackBarLayout();
            mWindowManager.addView(mLayout, mLayoutParams);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hide();
                }
            }, mDuration);
        } else {
            enqueueShowTask();
        }

    }

    /**
     * 设置弹出的SnackBar布局
     */
    private void makeSnackBarLayout() {
        mTopSnackBarInstance.setLayout(R.layout.widget_top_snackbar);
        mTopSnackBarInstance.setLayoutParam();
    }

    private void enqueueShowTask() {
        ShowTask showTask = new ShowTask();
        showTask.setContent(mSnackBarContent);
        showTask.setDuration(mDuration);
        mTaskQueue.add(showTask);
    }

    private void hide() {
        if (mLayout != null) {
            mWindowManager.removeView(mLayout);
        }
        setShowing(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                judgeNeedToShowNext();
            }
        }, NEXT_WAIT);
    }

    private void judgeNeedToShowNext() {
        ShowTask task = mTaskQueue.poll();
        if (task != null) {
            show();
        }
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    public void setShowing(boolean showing) {
        mIsShowing = showing;
    }

    private static class ShowTask {
        private String mContent;
        private int mDuration;

        public void setContent(String content) {
            mContent = content;
        }

        public void setDuration(int duration) {
            mDuration = duration;
        }

        public String getContent() {
            return mContent;
        }

        public int getDuration() {
            return mDuration;
        }
    }
}
