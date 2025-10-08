package com.dyg.siginprint.login.presenter;

import android.content.Context;

import com.dyg.siginprint.base.model.BaseBean;
import com.dyg.siginprint.base.presenter.BasePresenter;
import com.dyg.siginprint.http.httptools.AsyncUtils;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/07
 * desc : *
 */
public class ServerSetPresenter extends BasePresenter<IServerSetView> implements AsyncUtils.AsyncCallback {
    private IServerSetView view;
    private Context context;

    public ServerSetPresenter(Context context, IServerSetView view) {
        this.context = context;
        this.view = view;
    }



    @Override
    public void success(int requestCode, String data, BaseBean bean) {

    }

    @Override
    public void fail(int requestCode, int statusCode, String data, String msg) {

    }
}
