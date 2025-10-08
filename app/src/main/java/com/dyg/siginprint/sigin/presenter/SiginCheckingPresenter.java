package com.dyg.siginprint.sigin.presenter;

import android.content.Context;

import com.dyg.siginprint.base.model.BaseBean;
import com.dyg.siginprint.base.presenter.BasePresenter;
import com.dyg.siginprint.http.api.ApiList;
import com.dyg.siginprint.http.httptools.AsyncUtils;
import com.dyg.siginprint.sigin.model.BarcodeBean;
import com.dyg.siginprint.sigin.model.BillInfoBean;
import com.dyg.siginprint.sigin.model.SaveDataBean;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/07
 * desc : *
 */
public class SiginCheckingPresenter extends BasePresenter<ISiginCheckingView> implements AsyncUtils.AsyncCallback {
    private ISiginCheckingView view;
    private Context context;
    private final int REQUEST_CODE_BILLINFO = 27;//根据出库单号获取出库单信息
    private final int REQUEST_CODE_SCAN_RESULT = 28;//扫描解析
    private final int REQUEST_CODE_SAVE = 29;//保存
    private Gson gson;

    public SiginCheckingPresenter(Context context, ISiginCheckingView view) {
        this.context = context;
        this.view = view;
    }

    //根据出库单号获取出库单内容
    public void getBillInfo(String billno) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("data", billno);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncUtils.post(context, ApiList.API_STOCK_BILLINFO, jo, REQUEST_CODE_BILLINFO, 30, this);
    }

    /**
     * 获取扫码结果
     *
     * @param billNo       出库单号
     * @param customerCode 客户代号
     * @param isInnerLabel //是否内容标签：1内部标签    0客户标签 界面上有勾选框
     * @param barCodes     一维码或二维码
     */
    public void getScanResult(String billNo, String customerCode, int isInnerLabel, String barCodes) {
        JSONObject jo = new JSONObject();
        JSONObject joData = new JSONObject();
        try {
            joData.put("billNo", billNo);
            joData.put("customerCode", customerCode);
            joData.put("isInnerLabel", isInnerLabel);
            joData.put("scanData", barCodes);
            jo.put("data", joData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncUtils.post(context, ApiList.API_SCAN_RESULT, jo, REQUEST_CODE_SCAN_RESULT, 30, this);
    }


    //保存扫描记录
    public void saveScaned(SaveDataBean ssResult) {
        JSONObject jo = null;
        try {
            jo = new JSONObject(new Gson().toJson(ssResult));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncUtils.post(context, ApiList.API_SAVESCAN, jo, REQUEST_CODE_SAVE, 60, this);

    }

    /**
     * 扫码成功返回后，进行如下几步判断
     * 1、唯一流水号不可重复
     * 重复扫描时提示用户“标签重复扫描”，且本次扫描内容不作数
     * 2、流水号不重复，则判断“和本次扫描相同客户料号的扫描总数是否会超过出库单的对应客户料号的总数”
     * 若超过，则提示“客户料号【xxx】扫描数超过出库单总数”
     * 不超过，则执行下面的逻辑
     * “相同客户料号CustomerPn、公司料号Pn，批号LotNo，周期DateCode”的记录
     * 若存在，则添加数据，若不存在则新增一条记录
     *
     * @param bill     出库单
     * @param codeList 解析成功的所有条码
     * @param bList    显示的条码
     * @param codeData 条码内容
     */
    public void judgeResult(BillInfoBean bill, List<BarcodeBean> codeList, List<BarcodeBean> bList, String codeData) {
        if (gson == null) gson = new Gson();
        BarcodeBean thisBean = gson.fromJson(codeData, BarcodeBean.class);
        if (bList.size() == 0) {
            codeList.add(gson.fromJson(codeData, BarcodeBean.class));
            bList.add(thisBean);
            view.judgeAndResult(thisBean.getQty());
        } else {
            boolean ifSnRepeat = false;
            for (BarcodeBean bean : codeList) {
                if (!bean.getSn().equals("") && bean.getSn().equals(thisBean.getSn())) {
                    ifSnRepeat = true;
                    break;
                }
            }
            if (ifSnRepeat) {
                view.judgeAndEnd("签重复扫描！", true);
            } else {
                codeList.add(gson.fromJson(codeData, BarcodeBean.class));
                double outQty = bill.getCustPnQty(thisBean.getCustomerPn());//出库单总数
                double cpnQty = 0.00;//和本次扫描相同客户料号的扫描总数
                double count = 0.00;//所有扫码的总数
                int repeatIndex = -1;//“相同客户料号CustomerPn、公司料号Pn，批号LotNo，周期DateCode”的记录的位置
                for (int i = 0; i < bList.size(); i++) {
                    count = count + bList.get(i).getQty();
                    if (bList.get(i).getCustomerPn().equals(thisBean.getCustomerPn())) {
                        cpnQty = cpnQty + bList.get(i).getQty();
                        if (bList.get(i).getPn().equals(thisBean.getPn())
                                && bList.get(i).getLotNo().equals(thisBean.getLotNo())
                                && bList.get(i).getDateCode().equals(thisBean.getDateCode())) {
                            repeatIndex = i;
                        }
                    }
                }
                //此处要加上本次的数量，因为bList中还没有本次的数据
                cpnQty = cpnQty + thisBean.getQty();
                if (cpnQty > outQty) {
                    view.judgeAndEnd("客户料号【" + thisBean.getCustomerPn() + "】的扫描总数量( " + (int) cpnQty + " )超过出库单总数量( " + (int) outQty + " ),\n本次扫描无效", false);
                } else {
                    if (repeatIndex >= 0) {
                        double addQty = bList.get(repeatIndex).getQty() + thisBean.getQty();
                        bList.get(repeatIndex).setQty(addQty);
                    } else {
                        bList.add(thisBean);
                    }
                    count = count + thisBean.getQty();
                    view.judgeAndResult(count);
                }
            }
        }
    }

    @Override
    public void success(int requestCode, String data, BaseBean bean) {
        Gson gson = null;
        switch (requestCode) {
            case REQUEST_CODE_BILLINFO:
                gson = new Gson();
                BillInfoBean billInfoBean = gson.fromJson(data, BillInfoBean.class);
                if (billInfoBean != null) {
                    view.billGetSuccess(billInfoBean);
                } else {
                    view.billGetFail("获取出库单信息失败", data);
                }
                break;
            case REQUEST_CODE_SCAN_RESULT:
                if (data != null) {
                    view.barcInfofGetSuccess(data);
                } else {
                    view.barcInfofGetFail(1001, "获取条码信息失败", data);
                }
                break;
            case REQUEST_CODE_SAVE:
                view.saveResult(true, "保存成功！", null);
                break;
        }
    }

    @Override
    public void fail(int requestCode, int statusCode, String data, String msg) {
        switch (requestCode) {
            case REQUEST_CODE_BILLINFO:
                view.billGetFail(msg, data);
                break;
            case REQUEST_CODE_SCAN_RESULT:
                view.barcInfofGetFail(statusCode, msg, data);
                break;
            case REQUEST_CODE_SAVE:
                view.saveResult(false, msg, data);
                break;
        }
    }
}
