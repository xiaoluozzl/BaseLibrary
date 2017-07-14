package com.xiaoluo.baselibrary.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoluo.baselibrary.R;


/**
 * 自定义标题栏
 *
 * @author: xiaoluo
 * @date: 2017-01-18 16:45
 */
public class TitleBar extends RelativeLayout {
    private Context mContext;

    private TextView mTitleTv;
    private ImageView mLeftBtn;
    private ImageView mRightBtn;

    private OnTitleBarClickListener mListener;

    public TitleBar(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();

        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        String title = typedArray.getString(R.styleable.TitleBar_title);
        float titleTextSize = typedArray.getDimension(R.styleable.TitleBar_title_text_size, getResources().getDimension(R.dimen.title_size));
        int titleColor = typedArray.getColor(R.styleable.TitleBar_title_text_color, getResources().getColor(R.color.black));
        boolean showRight = typedArray.getBoolean(R.styleable.TitleBar_show_right_btn, true);
        boolean showLeft = typedArray.getBoolean(R.styleable.TitleBar_show_left_btn, true);
        int rightImgId = typedArray.getResourceId(R.styleable.TitleBar_right_image, R.drawable.ic_titlebar_right);
        int leftImgId = typedArray.getResourceId(R.styleable.TitleBar_left_image, R.drawable.ic_titlebar_back);

        if (title != null) {
            mTitleTv.setText(title);
        }
        int size = (int) titleTextSize + 1;
        mTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        mTitleTv.setTextColor(titleColor);
        mLeftBtn.setImageResource(leftImgId);
        mRightBtn.setImageResource(rightImgId);
        if (showRight) {
            mRightBtn.setVisibility(VISIBLE);
        } else {
            mRightBtn.setVisibility(GONE);
        }
        if (showLeft) {
            mLeftBtn.setVisibility(VISIBLE);
        } else {
            mLeftBtn.setVisibility(GONE);
        }

        typedArray.recycle();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.widget_titlebar, this);
        mTitleTv = (TextView) view.findViewById(R.id.title_tv);
        mLeftBtn = (ImageView) view.findViewById(R.id.left_btn);
        mRightBtn = (ImageView) view.findViewById(R.id.right_btn);

        mLeftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickRight();
                } else {
                    // 默认左键关闭activity
                    if (mContext instanceof Activity) {
                        ((Activity) mContext).finish();
                    }
                }
            }
        });

        mRightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickRight();
                }
            }
        });
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    /**
     * 设置标题
     */
    public void setTitle(int resId) {
        mTitleTv.setText(resId);
    }

    /**
     * 右边按钮
     */
    public void setTitleBarClickListenr(OnTitleBarClickListener listener) {
        mListener = listener;
    }

    public interface OnTitleBarClickListener {
        void onClickRight();

        void onClickLeft();
    }
}
