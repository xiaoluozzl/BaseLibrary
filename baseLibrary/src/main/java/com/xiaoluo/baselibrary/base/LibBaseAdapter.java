package com.xiaoluo.baselibrary.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 基类Adapter
 * 封装头部和尾部,实现单击和长按事件
 *
 * author: xiaoluo
 * date: 2017/6/27 11:01
 */
public abstract class LibBaseAdapter<T> extends RecyclerView.Adapter<LibBaseViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<T> mList;
    private int mLayoutId;
    private boolean hasHeader = false;
    private boolean hasFooter = false;

    private View mHeaderView;
    private View mFooterView;

    public static final int ITEM_TYPE_NORMAL = 110;  // 正常类型
    public static final int ITEM_TYPE_HEADER = 120;  // 头部类型
    public static final int ITEM_TYPE_FOOTER = 130;  // 尾部类型

    private OnItemClickListener mItemClickListener;
    private onItemLongClickListener mItemLongClickListener;

    public LibBaseAdapter(List<T> list) {
        this.mList = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasHeader && position == 0) {
            return ITEM_TYPE_HEADER;
        }
        if (hasHeader && hasFooter && position == mList.size() + 1) {
            return ITEM_TYPE_FOOTER;
        } else if (!hasHeader && hasFooter && position == mList.size()) {
            return ITEM_TYPE_FOOTER;
        }
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public LibBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new LibBaseViewHolder(mHeaderView);
        }

        if (viewType == ITEM_TYPE_FOOTER) {
            return new LibBaseViewHolder(mFooterView);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, (int) v.getTag());
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(v, (int) v.getTag());
                    return true;
                }
                return false;
            }
        });
        return new LibBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LibBaseViewHolder holder, int position) {
        if (hasHeader && hasFooter) {
            if (position == 0 || position == mList.size() + 1) {
                return;
            }
            bindHolder(holder, position - 1);
        }

        if (hasHeader && !hasFooter && position != 0) {
            bindHolder(holder, position - 1);
        }

        if (!hasHeader && hasFooter && position != mList.size()) {
            bindHolder(holder, position);
        }

        if (!hasHeader && !hasFooter) {
            bindHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        int size = mList.size();
        if (hasHeader) {
            size++;
        }
        if (hasFooter) {
            size++;
        }
        return size;
    }

    /**
     * 绑定数据,设定tag
     */
    private void bindHolder(LibBaseViewHolder holder, int position) {
        bindData(holder, position);
        holder.itemView.setTag(position);
    }

    /**
     * 获取item layout
     */
    public abstract int getLayoutId();

    /**
     * 绑定数据
     */
    protected abstract void bindData(LibBaseViewHolder holder, int position);

    /**
     * 添加头部
     */
    public void addHeaderView(View headerView) {
        this.mHeaderView = headerView;
        hasHeader = true;
        notifyDataSetChanged();
    }

    /**
     * 添加尾部
     */
    public void addFooterView(View footerView) {
        this.mFooterView = footerView;
        hasFooter = true;
        notifyDataSetChanged();
    }

    /**
     * 刷新数据
     */
    public void refreshData(List<T> list) {
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 删除数据
     */
    public void deleteData(int position) {
        if (hasHeader) {
            position ++;
        }
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position, 1);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(onItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public interface onItemLongClickListener {
        void onItemLongClick(View v, int position);
    }
}
