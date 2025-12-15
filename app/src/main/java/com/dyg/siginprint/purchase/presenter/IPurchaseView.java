package com.dyg.siginprint.purchase.presenter;

import com.dyg.siginprint.purchase.model.PuechaseModel;

import java.util.List;

public interface IPurchaseView {
    //基础信息获取
    void baseInfoSuccess(List dataList , int code);
    //网络请求失败
    void requestFail(int stautsCode , int code , String msg );
    //二维码解析成功
    void qrcodeAnalysisSuccess(PuechaseModel qrModel);
    //保存扫码结果成功
    void saveQrCodeSuccess(String msg);
}
