package com.dyg.siginprint.login.presenter;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/06
 * desc : *
 */
public interface ILoginView {
    //登录成功
    void loginSuccess(String data);
    //登录失败
    void loginFail(String errTitle, String errDesc);
}
