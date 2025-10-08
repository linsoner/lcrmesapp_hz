package com.dyg.siginprint.base.presenter;


/**
 * Created by long on 2016/8/23.
 * 基础 BaseView 接口
 */
public interface IBaseView {

    /**
     * 下拉刷新
     */
    void update(boolean isRefresh);

    /**
     * 加载更多
     */
    void loadMore();

    /**
     * 完成刷新, 新增控制刷新
     */
    void finishRefresh();

    /**
     * 有数据时刷新界面
     */
    void showDataView(String returnJson);

    /**
     * 无数据时显示界面提示
     */
    void showNoDataView(String title);

    /**
     * 请求出错是提示界面
     */
    void showErrorView(String errTitle, String errDesc);
    /**
     *展示进度条
     */
    void showLoadingDialog();

    /**
     * 关闭进度条
     */
    void closeLoadingDialog();

}
