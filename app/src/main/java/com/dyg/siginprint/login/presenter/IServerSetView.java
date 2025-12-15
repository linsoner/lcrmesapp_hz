package com.dyg.siginprint.login.presenter;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/06
 * desc : *
 */
public interface IServerSetView {
    //验证成功
    void checkSuccess(String data);
    //验证失败
    void checkFail(String errTitle, String errDesc);
}
