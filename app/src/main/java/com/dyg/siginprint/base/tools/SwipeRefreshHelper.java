package com.dyg.siginprint.base.tools;

import android.support.design.widget.AppBarLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


/**
 * Created by kelvin on 2016/12/1.
 * 下拉刷新帮助类
 */
public class SwipeRefreshHelper {

    private SwipeRefreshHelper() {
        throw new AssertionError();
    }

    /**
     * 初始化，关联AppBarLayout，处理滑动冲突
     *
     * @param refreshLayout
     * @param appBar
     * @param listener
     */
    public static void init(final SmartRefreshLayout refreshLayout, AppBarLayout appBar,
                            OnRefreshListener listener) {
        refreshLayout.setOnRefreshListener(listener);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    refreshLayout.setEnabled(true);
                } else {
                    refreshLayout.setEnabled(false);
                }
            }
        });
    }

    /**
     * 初始化
     *
     * @param refreshLayout
     * @param listener
     */
    public static void init(SmartRefreshLayout refreshLayout, OnRefreshListener listener) {
        refreshLayout.setOnRefreshListener(listener);
    }

    /**
     * 使能刷新
     *
     * @param refreshLayout
     * @param isEnable
     */
    public static void enableRefresh(SmartRefreshLayout refreshLayout, boolean isEnable) {
        if (refreshLayout != null) {
            refreshLayout.setEnabled(isEnable);
        }
    }

    /**
     * 控制刷新
     *
     * @param refreshLayout
     * @param isRefresh
     */
    public static void controlRefresh(SmartRefreshLayout refreshLayout, boolean isRefresh) {
        if (refreshLayout != null) {
            if (isRefresh != refreshLayout.isRefreshing()) {
                if (isRefresh) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.autoRefresh();
                }
            }
        }
    }
}
