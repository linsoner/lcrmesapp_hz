package com.dyg.siginprint.deviceCheck.presenter;

import android.content.Context;

import com.dyg.siginprint.base.model.BaseBean;
import com.dyg.siginprint.base.presenter.BasePresenter;
import com.dyg.siginprint.deviceCheck.model.DeviceCheckBean;
import com.dyg.siginprint.http.api.ApiList;
import com.dyg.siginprint.http.httptools.AsyncUtils;
import com.dyg.siginprint.wiget.dialog.model.BaseDataModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;


public class DeviceCheckPresenter extends BasePresenter<IDeviceCheck> implements AsyncUtils.AsyncCallback {
    private IDeviceCheck view;
    private Context context;

    private final int REQUEST_ANALYSIS_QRCODE = 1001;
    private final int REQUEST_SAVE_SCAN_QRCODE = 1002;
    private final int REQUEST_SELECT_WEEK = 1003; //选择周期

    public DeviceCheckPresenter(Context context , IDeviceCheck view){
        this.view = view;
        this.context = context;
    }

    public void requestBaseInfo(String formName , int data){
        JSONObject jo = new JSONObject();
        try {
            jo.put("formName",formName);
            jo.put("data", data > 10000 ? data - 100000 : data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncUtils.post(context, ApiList.API_BaseData, jo, data, 30, this);
    }

    //二维码解析
    public void requestAnalysisQRCode(String formName,String joData){
        JSONObject jo = new JSONObject();
        try {
            jo.put("formName",formName);
            jo.put("data", joData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncUtils.post(context, ApiList.API_Machine, jo, REQUEST_ANALYSIS_QRCODE, 30, this);
    }

    //获取设备项目列表
    public void requestDeviceProjects(String formName, String Machine ,String ItemType){
        JSONObject jo = new JSONObject();
        try {
            JSONObject joData = new JSONObject();
            jo.put("formName",formName);

            joData.put("Machine",Machine);
            joData.put("ItemType",ItemType);
            jo.put("Data", joData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncUtils.post(context, ApiList.API_Machineitems, jo, REQUEST_SELECT_WEEK, 30, this);
    }

    //保存扫码结果
    public void requestSaveScanResulte(String formName,JSONObject joData){
        JSONObject jo = new JSONObject();
        try {
            jo.put("formName",formName);
            jo.put("data", joData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncUtils.post(context, ApiList.API_Savemchine, jo, REQUEST_SAVE_SCAN_QRCODE, 30, this);
    }

    @Override
    public void success(int requestCode, String data, BaseBean bean) {
        Gson gson = null;
        switch (requestCode) {
            case -89:{
                gson = new Gson();
                Type type = new TypeToken<List<BaseDataModel>>() {}.getType();
                List<BaseDataModel> list = gson.fromJson(data, type);
                view.baseInfoSuccess(list,requestCode);
            }
            break;
            case REQUEST_ANALYSIS_QRCODE:{
                try {
                    JSONObject obj = new JSONObject(data);
                    view.qrcodeAnalysisSuccess(obj);
                }
                catch (JSONException e){
                    view.qrcodeAnalysisSuccess(new JSONObject());
                }
            }
            break;
            case REQUEST_SAVE_SCAN_QRCODE:{
                view.saveQrCodeSuccess(bean.getMsg() == null || bean.getMsg().equals("") ? "保存成功" : bean.getMsg());
            }
            break;
            case REQUEST_SELECT_WEEK:{
                gson = new Gson();
                Type type = new TypeToken<List<DeviceCheckBean>>() {}.getType();
                List<DeviceCheckBean> list = gson.fromJson(data,type);
                view.getProjectsSuccess(list);
            }
            break;
        }
    }

    @Override
    public void fail(int requestCode, int statusCode, String data, String msg) {
        view.requestFail(statusCode,requestCode,msg);
    }
}
