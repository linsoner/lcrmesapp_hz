package com.dyg.siginprint.login.presenter;

import android.content.Context;

import com.dyg.siginprint.base.model.BaseBean;
import com.dyg.siginprint.base.presenter.BasePresenter;
import com.dyg.siginprint.http.api.ApiList;
import com.dyg.siginprint.http.httptools.AsyncUtils;
import com.dyg.siginprint.login.model.LoginBean;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/06
 * desc : *
 */
public class LoginPresenter extends BasePresenter<ILoginView> implements AsyncUtils.AsyncCallback {
    private ILoginView view;
    private Context context;
    private final int REQUEST_CODE_LOGIN = 2;

    public LoginPresenter(Context context, ILoginView view) {
        this.context = context;
        this.view = view;
    }
    //发起登录
    public void sendLogin(LoginBean loginBean) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("userCode", loginBean.getAccount());
            jo.put("password", loginBean.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncUtils.post(context, ApiList.API_LOGIN, jo, REQUEST_CODE_LOGIN, 60, this);
    }

    /**
     * 权限升级
     * @param Account 授权者的账号
     * @param passWord 授权者的密码
     */
    public void upgrade(String Account, String passWord) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("userCode", Account);
            jo.put("password", passWord);
            jo.put("data", "Approve");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncUtils.postNoUserCode(context, ApiList.API_USER_UPGRADE, jo, REQUEST_CODE_LOGIN, 30, this);
    }

    @Override
    public void success(int requestCode, String data, BaseBean bean) {
        switch (requestCode) {
            case REQUEST_CODE_LOGIN:
                view.loginSuccess(data);
                break;

        }
    }

    @Override
    public void fail(int requestCode, int statusCode, String data, String msg) {
        switch (requestCode) {
            case REQUEST_CODE_LOGIN:
                view.loginFail(msg, data);
                break;
        }
    }
}
