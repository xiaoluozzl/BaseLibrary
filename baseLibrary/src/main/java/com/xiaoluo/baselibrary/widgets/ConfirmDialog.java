package com.xiaoluo.baselibrary.widgets;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xiaoluo.baselibrary.R;
import com.xiaoluo.baselibrary.base.LibBaseDialog;


/**
 * 确认对话框
 *
 * @author: xiaoluo
 * @date: 2016-12-21 18:39
 */
public class ConfirmDialog extends LibBaseDialog {

    TextView leftTv;
    TextView rightTv;
    TextView messageTv;

    private OnSelectListener mListener;

    public ConfirmDialog(Context context) {
        super(context, R.style.ConfirmDialog);
    }

    @Override
    public int loadParentLayout() {
        return R.layout.dialog_confirm;
    }

    @Override
    public void initListeners() {
        leftTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onLeftSelect();
                    dismiss();
                }
            }
        });

        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRightSelect();
                    dismiss();
                }
            }
        });
    }

    @Override
    public void initViews() {
        leftTv = (TextView) findViewById(R.id.dialog_custom_left_btn);
        rightTv = (TextView) findViewById(R.id.dialog_custom_right_btn);
        messageTv = (TextView) findViewById(R.id.dialog_custom_content_tv);
    }

    @Override
    public boolean atBottom() {
        return false;
    }

    public ConfirmDialog setLeftText(String text) {
        leftTv.setText(text);
        return this;
    }

    public ConfirmDialog setRightText(String text) {
        rightTv.setText(text);
        return this;
    }

    public ConfirmDialog setMessage(String text) {
        messageTv.setText(text);
        return this;
    }

    public ConfirmDialog setOnSelectListener(OnSelectListener listener) {
        this.mListener = listener;
        return this;
    }

    public interface OnSelectListener {
        void onLeftSelect();

        void onRightSelect();
    }
}
