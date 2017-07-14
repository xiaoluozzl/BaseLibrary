package com.xiaoluo.jcenterdemo;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xiaoluo.baselibrary.base.LibBaseActivity;
import com.xiaoluo.baselibrary.utils.Utils;

public class MainActivity extends LibBaseActivity {

    TextView test;

    @Override
    protected Context getActivityContext() {
        return this;
    }

    @Override
    protected int loadParentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        Utils.showToast("可以调用");
        test.setText("GGGGGGGGGG");
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initLogic() {
    }

    @Override
    public void onClick(View v) {

    }
}
