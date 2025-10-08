package com.dyg.siginprint.base.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.dyg.siginprint.R;
import com.dyg.siginprint.app.RunningActivityList;
import com.dyg.siginprint.base.presenter.BasePresenter;
import com.dyg.siginprint.base.presenter.IBaseView;
import com.dyg.siginprint.base.tools.SwipeRefreshHelper;
import com.dyg.siginprint.wiget.emptyview.EmptyView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2018/1/5 0005.
 */

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements IBaseView {

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    /**
     * 刷新控件，注意，资源的ID一定要一样
     */
    @Nullable
    @BindView(R.id.swipe_refresh)
    protected SmartRefreshLayout mSwipeRefresh;

    @Nullable
    @BindView(R.id.view_empty)
    protected EmptyView emptyView;
    /**
     * 控制器
     */
    public T presenter;


    /**
     * 绑定布局文件
     *
     * @return 布局文件ID
     */
    @LayoutRes
    protected abstract int attachLayoutRes();


    /**
     * 初始化presenter
     */
    protected abstract void initPresenter();

    /**
     * 初始化视图控件
     */
    protected abstract void initViews(Bundle savedInstanceState);


    public LoadingDailog mLoadingDialog;
    protected String actionName = "加载中......";
    public String viewId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // 获取 Intent 并提取参数
            Intent intent = getIntent();
            viewId = intent.getStringExtra("viewId"); // 接收字符串
            
            //registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            RunningActivityList.getInstance().addActivity(this);
            setContentView(attachLayoutRes());
            if (mLoadingDialog == null) {
                LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                        .setMessage(actionName)
                        .setCancelable(true)
                        .setCancelOutside(true);
                mLoadingDialog = loadBuilder.create();
            }
            ButterKnife.bind(this);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);// 关闭title
            }
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);// 关闭title
            }
            initPresenter();
            initSwipeRefresh();
            initViews(savedInstanceState);
        }catch (Exception e){
            e.getMessage();
        }
    }

    protected void initTitle(boolean isShowBackBtn, String title, String rightTxt, final View.OnClickListener backListener, final View.OnClickListener rightListener) {
        if (toolbar != null) {
            try {
                ImageButton ibLeft = toolbar.findViewById(R.id.ivfLeft);
                if (isShowBackBtn) {
                    ibLeft.setEnabled(true);
                    ibLeft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (backListener == null) finish();
                            else backListener.onClick(view);
                        }
                    });
                }else {
                    ibLeft.setEnabled(false);
                    ibLeft.setVisibility(View.INVISIBLE);
                }
            }catch (Exception e){}
            if (title != null) {
                TextView titleView = toolbar.findViewById(R.id.tvfTitile);
                titleView.setText(title);
            }
            try {
                if (rightTxt!=null && !rightTxt.trim().equals("")) {
                    TextView rightView = toolbar.findViewById(R.id.tvfRightSet);
                    rightView.setVisibility(View.VISIBLE);
                    rightView.setText(rightTxt);
                    rightView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (rightListener == null) finish();
                            else rightListener.onClick(view);
                        }
                    });
                }else {
                    toolbar.findViewById(R.id.tvfRightSet).setVisibility(View.GONE);
                }
            }catch (Exception e){}

        }
    }
    /**
     * startNewActivity Description: 意图，界面跳转
     *
     * @param targetActClass 转入的活动类
     */
    public void startNewAct(Class<?> targetActClass) {
        startNewAct(targetActClass, null);
    }

    public void startNewAct(Class<?> targetActClass, boolean isfinished) {
        startNewAct(targetActClass, null);
        if (isfinished) {
            finish();
        }
    }

    public void startNewAct(Class<?> targetActClass, Bundle data) {
        Intent intent = new Intent();
        intent.setClass(this, targetActClass);
        if (data != null) {
            intent.putExtras(data);
        }
        startActivity(intent);
    }

    public void startNewAct(Class<?> targetActClass, Bundle data, boolean isfinsished) {
        Intent intent = new Intent();
        intent.setClass(this, targetActClass);
        if (data != null) {
            intent.putExtras(data);
        }
        startActivity(intent);
        if (isfinsished) {
            finish();
        }
    }

    public void startNewAct(Class<?> targetActClass, Bundle data, int resultCode) {
        Intent intent = new Intent();
        intent.setClass(this, targetActClass);
        if (data != null) {
            intent.putExtras(data);
        }
        startActivityForResult(intent, resultCode);
    }

    /**
     * -----------------------------    点击空白处隐藏软键盘   --------------------------------------------------------------------------
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 初始化下拉刷新
     */
    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            //设置 Header 为 Material风格
            // mSwipeRefresh.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(false).setColorSchemeColors(getResources().getColor(R.color.bartextcolor)));
            SwipeRefreshHelper.init(mSwipeRefresh, new OnRefreshListener() {
                @Override
                public void onRefresh(RefreshLayout refreshlayout) {
                    update(true);
                }
            });
            mSwipeRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
                @Override
                public void onLoadmore(RefreshLayout refreshlayout) {
                    loadMore();
                }
            });
        }
    }
    //刷新数据
    @Override
    public void update(boolean isRefresh) {

    }
    //加载更多
    @Override
    public void loadMore() {

    }
    //终止刷新
    @Override
    public void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.finishRefresh();
            mSwipeRefresh.finishLoadmore();
        }
    }
    //请求有数据时刷新界面
    @Override
    public void showDataView(String returnJson) {
    }

    //请求无数据时显示错误提示
    @Override
    public void showNoDataView(String msg) {
        if (emptyView != null) emptyView.setEmptyView(msg, "");
    }

    @Override
    public void showErrorView(String errTitle, String errDesc){
        if (emptyView != null) emptyView.setEmptyView(errTitle, errDesc);
    }

    //显示圆圈进度条
    @Override
    public void showLoadingDialog() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    //关闭圆圈进度条
    @Override
    public void closeLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RunningActivityList.getInstance().removeActivity(this);
    }
}

