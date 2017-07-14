package com.xiaoluo.baselibrary.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Fragment基类
 *
 * @author: xiaoluo
 * @date: 2016-12-21 14:05
 */
public abstract class LibBaseFragment extends Fragment {
    private final static String TAG = LibBaseFragment.class.getSimpleName();
    private View mRootView;
    protected Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getFragmentContext();
        mRootView = inflater.inflate(loadParentLayout(), container, false);
        ButterKnife.bind(this, mRootView);
        initViews();
        initListeners();
        initLogic();
        return mRootView;
    }

    /**
     * 获取context
     * @return context
     */
    protected abstract Context getFragmentContext();

    /**
     * 初始化视图
     * @return layoutResId
     */
    protected abstract int loadParentLayout();

    /**
     * 初始化页面
     */
    protected abstract void initViews();

    /**
     * 初始化监听器
     */
    protected abstract void initListeners();

    /**
     * 初始化数据
     */
    protected abstract void initLogic();
}
