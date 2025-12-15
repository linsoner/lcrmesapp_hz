package com.dyg.siginprint.main.presenter;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/06
 * desc : *
 */
public interface IMainView {
    //登出成功
    void logoutSuccess();
    //登出失败
    void logoutFail(String errTitle, String errDesc);
}
