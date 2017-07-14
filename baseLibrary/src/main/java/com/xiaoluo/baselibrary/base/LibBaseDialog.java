package com.xiaoluo.baselibrary.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.Window;

import com.xiaoluo.baselibrary.R;

import butterknife.ButterKnife;

/**
 * Dialog基类
 *
 * author: xiaoluo
 * date: 2017/7/12 10:00
 */
public abstract class LibBaseDialog extends Dialog {

    protected Context mContext;

    public LibBaseDialog(@NonNull Context context) {
        super(context);
        init(context);

    }

    public LibBaseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected LibBaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setContentView(loadParentLayout());
        ButterKnife.bind(this);

        setCanceledOnTouchOutside(true);
        Window menuWindow = getWindow();

        if (atBottom()) {
            menuWindow.setWindowAnimations(R.style.BottomDialogAnimStyle);
            menuWindow.getAttributes().width = mContext.getResources().getDisplayMetrics().widthPixels;
            menuWindow.setGravity(Gravity.BOTTOM);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        initListeners();
    }

    /**
     * 加载页面layout
     *
     * @return layoutResId 布局id
     */
    protected abstract int loadParentLayout();

    protected abstract boolean atBottom();

    /**
     * 初始化页面
     */
    protected abstract void initViews();

    /**
     * 初始化监听器
     */
    protected abstract void initListeners();
}
