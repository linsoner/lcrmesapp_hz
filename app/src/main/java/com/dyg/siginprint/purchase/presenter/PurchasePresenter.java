package com.dyg.siginprint.purchase.presenter;

import android.content.Context;

import com.dyg.siginprint.base.model.BaseBean;
import com.dyg.siginprint.base.presenter.BasePresenter;
import com.dyg.siginprint.http.api.ApiList;
import com.dyg.siginprint.http.httptools.AsyncUtils;
import com.dyg.siginprint.purchase.model.PuechaseModel;
import com.dyg.siginprint.wiget.dialog.model.BaseDataModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class PurchasePresenter extends BasePresenter<IPurchaseView> implements AsyncUtils.AsyncCallback {
    private IPurchaseView view;
    private Context context;
    private final int REQUEST_ANALYSIS_QRCODE = 1001;
    private final int REQUEST_SAVE_SCAN_QRCODE = 1002;
    private final int REQUEST_SAVE_PRINT = 1003;

    public PurchasePresenter(Context context , IPurchaseView view){
        this.view = view;
        this.context = context;
    }

    /*
    * formName //采购进退货FormPur  成品调拨FormProDiaobo
      //核对扫码FormCheck
      data 1客户  2供应商  4车间  -1工序   -2所有仓库  -21成品仓
    //-22车间仓 -23材料仓 -24材料周转仓 -3成型方式  -4计量单位
    //-5采购单号  -6销售出货单号 - 7成品调拨单号  -8材料调拨单号
    //-9 流程卡号 - 10工单号 -99单据类型 -88便携式打印机 -90分切入库单据类型
    * */
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
    public void requestAnalysisQRCode(String formName,JSONObject joData){
        JSONObject jo = new JSONObject();
        try {
            jo.put("formName",formName);
            jo.put("data", joData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncUtils.post(context, ApiList.API_StockbillSplit, jo, REQUEST_ANALYSIS_QRCODE, 10, this);
    }

    //保存扫码结果
    public void requestScanResulte(String formName,JSONObject joData){
        JSONObject jo = new JSONObject();
        try {
            jo.put("formName",formName);
            jo.put("data", joData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncUtils.post(context, ApiList.API_Stockbill_Savescan, jo, REQUEST_SAVE_SCAN_QRCODE, 60, this);
    }

    //拆箱打印接口
    public void requestPrint(String formName , JSONObject joData){
        JSONObject jo = new JSONObject();
        try {
            jo.put("formName",formName);
            jo.put("data",joData);
        }catch (JSONException e){
            e.printStackTrace();
        }
        AsyncUtils.post(context, ApiList.API_SYS_Print,jo,REQUEST_SAVE_PRINT,30,this);
    }

    @Override
    public void success(int requestCode, String data , BaseBean bean) {
        Gson gson = null;
        switch (requestCode) {
            case 2: case -23: case 1: case 1+100000: case -21: case -21+100000: case -99: case -88: case -90:{
                gson = new Gson();
                Type type = new TypeToken<List<BaseDataModel>>() {}.getType();
                List<BaseDataModel> list = gson.fromJson(data, type);
                view.baseInfoSuccess(list,requestCode);
            }
                break;
            case REQUEST_ANALYSIS_QRCODE:{
                gson = new Gson();
                PuechaseModel model = gson.fromJson(data,PuechaseModel.class);
                view.qrcodeAnalysisSuccess(model);
            }
            break;
            case REQUEST_SAVE_SCAN_QRCODE:{
                view.saveQrCodeSuccess(bean.getMsg() == null || bean.getMsg().equals("") ? "保存成功" : bean.getMsg());
            }
            break;
            case REQUEST_SAVE_PRINT:{
                view.saveQrCodeSuccess(bean.getMsg() == null || bean.getMsg().equals("") ? "已提交打印请求" : bean.getMsg());
            }
            break;
            default:
                gson = new Gson();
                Type type = new TypeToken<List<BaseDataModel>>() {}.getType();
                List<BaseDataModel> list = gson.fromJson(data, type);
                view.baseInfoSuccess(list,requestCode);
                break;
        }
    }

    @Override
    public void fail(int requestCode, int statusCode, String data, String msg) {
        view.requestFail(statusCode,requestCode,msg);
    }
}
