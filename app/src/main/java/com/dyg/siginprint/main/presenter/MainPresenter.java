package com.dyg.siginprint.main.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.dyg.siginprint.app.RunningActivityList;
import com.dyg.siginprint.base.model.BaseBean;
import com.dyg.siginprint.base.model.Tokens;
import com.dyg.siginprint.base.presenter.BasePresenter;
import com.dyg.siginprint.http.httptools.AsyncUtils;
import com.dyg.siginprint.login.model.LoginBean;
import com.dyg.siginprint.login.presenter.LoginManger;
import com.dyg.siginprint.login.view.LoginActivity;
import com.orhanobut.hawk.Hawk;

import java.util.UUID;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/07
 * desc : *
 */
public class MainPresenter extends BasePresenter<IMainView> implements AsyncUtils.AsyncCallback {
    private IMainView view;
    private Activity activity;
    private final int REQUEST_CODE_LOGOUT = 10;

    public MainPresenter(Activity context, IMainView view) {
        this.activity = context;
        this.view = view;
    }
    //登出
    public void LogOut() {
        LoginManger.outLogin(activity);
    }

    @Override
    public void success(int requestCode, String data, BaseBean bean) {

    }

    @Override
    public void fail(int requestCode, int statusCode, String data, String msg) {

    }
}
