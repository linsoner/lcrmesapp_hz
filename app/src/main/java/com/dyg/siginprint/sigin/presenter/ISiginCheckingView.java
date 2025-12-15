package com.dyg.siginprint.sigin.presenter;

import com.dyg.siginprint.sigin.model.BarcodeBean;
import com.dyg.siginprint.sigin.model.BillInfoBean;

import java.util.List;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/06
 * desc : *
 */
public interface ISiginCheckingView {
    //获取出库单信息成功
    void billGetSuccess(BillInfoBean data);
    //获取出库单信息失败
    void billGetFail(String errTitle, String errDesc);
    //获取条码信息成功
    void barcInfofGetSuccess(String codeData);
    //扫描成功后处理各种判断条件后,满足条件，返回最终要显示的数据
    void judgeAndResult(double count);
    //扫描成功后处理各种判断条件后,未满足条件，终止本次扫描
    void judgeAndEnd(String msg, boolean ifAutoCloseToast);
    //获取条码信息失败
    void barcInfofGetFail(int statusCode, String errTitle, String errDesc);
    //保存结果
    void saveResult(boolean isSuccess, String errTitle, String errDesc);
}
