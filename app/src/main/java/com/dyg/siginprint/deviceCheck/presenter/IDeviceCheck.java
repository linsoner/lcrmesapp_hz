package com.dyg.siginprint.deviceCheck.presenter;

import com.dyg.siginprint.deviceCheck.model.DeviceCheckBean;

import org.json.JSONObject;

import java.util.List;

public interface IDeviceCheck {
    //基础信息获取
    void baseInfoSuccess(List dataList , int code);
    //网络请求失败
    void requestFail(int stautsCode , int code , String msg );
    //保养项目获取成功
    void qrcodeAnalysisSuccess(JSONObject object);
    //保存扫码结果成功
    void saveQrCodeSuccess(String msg);
    //选择获取设备项目成功
    void getProjectsSuccess(List<DeviceCheckBean> lists);

}
