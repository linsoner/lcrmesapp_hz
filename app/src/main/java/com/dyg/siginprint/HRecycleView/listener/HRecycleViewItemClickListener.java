package com.dyg.siginprint.HRecycleView.listener;

import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
/**
 * Created by m088524 on 2022/8/17
 * Describe:
 */
public abstract class HRecycleViewItemClickListener extends SimpleClickListener {

    /**
     * 点击事件
     */
    public abstract void onItemClick(BaseQuickAdapter adapter, View view, int position);

    public abstract void onItemLongClick(BaseQuickAdapter adapter, View view, int position);

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        Log.d(TAG, "onItemChildClick: " + position);
    }

    @Override
    public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
        Log.d(TAG, "onItemChildLongClick: " + position);
    }
}
