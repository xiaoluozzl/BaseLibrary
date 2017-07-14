package com.xiaoluo.baselibrary.base;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * 基类ViewHolder
 *
 * author: xiaoluo
 * date: 2017/6/27 10:55
 */
public class LibBaseViewHolder extends RecyclerView.ViewHolder{

    private SparseArray<View> views;

    public LibBaseViewHolder(View itemView) {
        super(itemView);
        this.views = new SparseArray<>();
    }

    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public View getRootView() {
        return itemView;
    }
}
