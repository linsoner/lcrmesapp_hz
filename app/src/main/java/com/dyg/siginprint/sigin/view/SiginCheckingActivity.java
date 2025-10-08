package com.dyg.siginprint.sigin.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingyg.qrcodelib.common.QrManager;
import com.dyg.siginprint.R;
import com.dyg.siginprint.base.model.Tokens;
import com.dyg.siginprint.base.tools.DoubleClickU;
import com.dyg.siginprint.base.tools.ToastUtil;
import com.dyg.siginprint.base.view.BaseActivity;
import com.dyg.siginprint.login.model.LoginBean;
import com.dyg.siginprint.login.view.RightUpgradeActivity;
import com.dyg.siginprint.sigin.adapter.SiginCheckingAdapter;
import com.dyg.siginprint.sigin.model.BarcodeBean;
import com.dyg.siginprint.sigin.model.BillInfoBean;
import com.dyg.siginprint.sigin.model.SaveDataBean;
import com.dyg.siginprint.sigin.model.TemStorageBean;
import com.dyg.siginprint.sigin.presenter.ISiginCheckingView;
import com.dyg.siginprint.sigin.presenter.SiginCheckingPresenter;
import com.dyg.siginprint.update.ActivityUtils;
import com.dyg.siginprint.update.CustomDialog;
import com.dyg.siginprint.wiget.textview.ClearEditText;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/07
 * desc : * 客户标签核对
 */
public class SiginCheckingActivity extends BaseActivity<SiginCheckingPresenter> implements ISiginCheckingView, ClearEditText.Listener {

    @BindView(R.id.et_billno)
    ClearEditText etBillNo;//出库单号
    @BindView(R.id.iv_check)
    ImageView ivCheck;//选择“是否内部标签” 不勾选0-表示扫描的客户标签;勾选1-表示扫描的是内部标签
    @BindView(R.id.tv_customer)
    TextView tvCustomer;//客户
    @BindView(R.id.et_barcode)
    ClearEditText etBarcode;//条码
    @BindView(R.id.tv_one_dimensiona)
    TextView tvOneDimensiona;//一维码条码数量统计
    @BindView(R.id.tv_barcode_count)
    TextView tvBarcodeCount;//条码张数
    @BindView(R.id.tv_count)
    TextView tvCount;//数量统计
    @BindView(R.id.rv_scan_result)
    RecyclerView rvScanResult;
    LinearLayoutManager mLayoutManager;
    SiginCheckingAdapter scAdapter;
    Activity mActivity;
    BillInfoBean billInfo;//出库单信息
    List<String> oneBarcode;//一维码条码集
    List<BarcodeBean> codesList;//解析成功后的数据
    List<BarcodeBean> showList;//解析成功且匹配累加jij// 后界面显示的条码集
    final int CODE_TO_UPGRADE = 101;
    String userCode = "";//用户代号
    boolean ifNeedTemStorage = false;//是否需要提示暂存
    final int CODE_SCAN_BILLNO = 701;//扫描出库单
    final int CODE_SCAN_BARCODE = 702;//扫描条码


    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_sigin_checking;
    }

    @Override
    protected void initPresenter() {
        presenter = new SiginCheckingPresenter(SiginCheckingActivity.this,this);
        initTitle(true, "客户标签核对", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinishView();
            }
        }, null);
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        LoginBean loginBean = Hawk.get(Tokens.LoginBean, null);
        if (loginBean == null) finish();
        else {
            userCode = loginBean.getAccount();
            mActivity = SiginCheckingActivity.this;
            etBillNo.setBackground(R.drawable.bg_edit_box_black);
            etBarcode.setBackground(R.drawable.bg_edit_box_blue);
            etBillNo.setShowClear(false);
            etBillNo.initEditText(0, "", 0, 1, true, this);
            etBarcode.setShowClear(true);
            etBarcode.initEditText(0, "", 0, 2, true,this);
            codesList = new ArrayList<>();
            showList = new ArrayList<>();
            mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
            rvScanResult.setLayoutManager(mLayoutManager);
            scAdapter = new SiginCheckingAdapter(mActivity);
            rvScanResult.setAdapter(scAdapter);
            //本地暂存存储
            TemStorageBean storageBean = null;
            try{
                String temStroe = Hawk.get(userCode + "_scan", null);
                if (temStroe != null) {
                    storageBean = new Gson().fromJson(temStroe, TemStorageBean.class);
                }
            }catch (Exception ee){
                ee.fillInStackTrace();
            }
            if (storageBean != null) {
                billInfo = storageBean.getBill();
                if (billInfo != null) {
                    showData();
                    etBillNo.setTextCt(billInfo.getBillNo(), false);
                    checkChange(storageBean.getIsInterSigin() == 1 ? 0 : 1);
                    showList = storageBean.getbList();
                    codesList = storageBean.getCodeList();
                    if (showList != null && showList.size() > 0 && codesList != null && codesList.size() > 0) {
                        scAdapter.notifyDataSetChanged(showList);
                        tvCount.setText(storageBean.getCount());
                        if(codesList != null) tvBarcodeCount.setText(codesList.size() + "");
                    }
                    oneBarcode = storageBean.getOnes();
                    if (oneBarcode != null && oneBarcode.size() > 0) {
                        tvOneDimensiona.setText(oneBarcode.size() + "/" + billInfo.getScanCount());
                    }
                    if(billInfo.getBillNo()!=null && !billInfo.getBillNo().trim().equals("")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                etBarcode.requestEditFoucs();
                            }
                        }, 300);
                    }
                }
            }
        }
    }

    @OnClick({R.id.ll_check, R.id.tv_save, R.id.tv_temporary_storage, R.id.tv_clearing, R.id.tv_rescan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_check:
                checkChange(getCheckCode());
                break;
            case R.id.tv_save: {
                if (!DoubleClickU.isFastDoubleClick(R.id.tv_save)){
                    if (showList == null || showList.size() == 0) {
                        ToastUtil.show(mActivity, "暂无数据，保存无效!");
                    }else {
                        showLoadingDialog();
                        SaveDataBean saveDataBean = new SaveDataBean();
                        SaveDataBean.DataBean dataBean = new SaveDataBean.DataBean();
                        dataBean.setSourceNo(billInfo.getBillNo());
                        dataBean.setCustomerCode(billInfo.getCustomerCode());
                        dataBean.setCustomerName(billInfo.getCustomerName());
                        dataBean.setLabels(codesList);
                        saveDataBean.setData(dataBean);
                        presenter.saveScaned(saveDataBean);
                    }
                }
            }
            break;
            case R.id.tv_temporary_storage: {
                if (!DoubleClickU.isFastDoubleClick(R.id.tv_temporary_storage)){
                    if (showList == null || showList.size() == 0) {
                        ToastUtil.show(mActivity, "暂无数据，暂存无效!");
                    }else {
                        showLoadingDialog();
                        try {
                            TemStorageBean storageBean = new TemStorageBean();
                            storageBean.setBill(billInfo);
                            storageBean.setbList(showList);
                            storageBean.setCodeList(codesList);
                            storageBean.setOnes(oneBarcode);
                            storageBean.setIsInterSigin(getCheckCode());
                            storageBean.setCount(tvCount.getText().toString());
                            Hawk.put(userCode + "_scan", new Gson().toJson(storageBean));
                            ToastUtil.showToast(mActivity, "已暂存！");
                        }catch (Exception e){
                            e.fillInStackTrace();
                        }
                        ifNeedTemStorage = false;
                        closeLoadingDialog();
                    }
                }
            }
            break;
            case R.id.tv_clearing: {
                if (!DoubleClickU.isFastDoubleClick(R.id.tv_clearing)){
                    showLoadingDialog();
                    clearView();
                    closeLoadingDialog();
                }
            }
            break;
            case R.id.tv_rescan: {
                if (!DoubleClickU.isFastDoubleClick(R.id.tv_rescan)){
                    if (billInfo != null) {
                        if (oneBarcode != null && oneBarcode.size() > 0) {
                            oneBarcode.clear();
                            ToastUtil.showToast(mActivity, "已清除本次一维码扫描！");
                        }
                        tvOneDimensiona.setText("0/" + billInfo.getScanCount());
                    }
                }
            }
            break;
        }
    }
    //清空界面
    private void clearView() {
        try {
            billInfo = null;
            if(codesList != null) codesList.clear();
            if (showList != null) showList.clear();
            scAdapter.notifyDataSetChanged(showList);
            if (oneBarcode == null) oneBarcode = new ArrayList<>();
            else oneBarcode.clear();
            tvCount.setText("");
            tvBarcodeCount.setText("");
            tvOneDimensiona.setText("0/0");
            tvCustomer.setText("");
            etBarcode.setTextCt("", false);
            etBillNo.setTextCt("", true);
            Hawk.delete(userCode + "_scan");
            ifNeedTemStorage = false;
        }catch (Exception e){
            e.fillInStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                etBillNo.requestEditFoucs();
            }
        }, 300);
    }

    /**
     * @param currentCheckCode 当前选中
     */
    private void checkChange(int currentCheckCode) {
        if(currentCheckCode == 1){
            ivCheck.setTag(0);
            ivCheck.setImageResource(R.mipmap.unchecked);
        }else {
            ivCheck.setTag(1);
            ivCheck.setImageResource(R.mipmap.checked);
        }
    }

    private int getCheckCode() {
        int checkedCode = 0;
        try {
            checkedCode = (int) ivCheck.getTag();
        }catch (Exception e){}
        return checkedCode;
    }


    @Override
    public void changed(int etId, String result) {}

    //回车键
    @Override
    public void confirm(int etId, String barcode) {
        if (etId == 1) {//出库单单号
            etBillNo.setTextCt(barcode, false);
            presenter.getBillInfo(barcode);
        }else if (etId == 2) {//条码
            if (billInfo == null) {
                ToastUtil.show(mActivity, "请先扫描或输入出库单号！", true);
                // etBarcode.setTextHint(barcode);
                etBarcode.setTextCt("", false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        etBillNo.requestEditFoucs();
                    }
                }, 300);
            }else {
                if (billInfo.getScanCount() <= 1) {
                    //扫描二维码，每次都调接口解析
                    showLoadingDialog();
                    presenter.getScanResult(billInfo.getBillNo(), billInfo.getCustomerCode(), getCheckCode(), barcode);
                }else {
                    //扫描一维码，当扫描的次数达到了scanCount后调接口解析
                    if (oneBarcode.size() < billInfo.getScanCount()) {
                        boolean isScaned = false;
                        for (String bar : oneBarcode) {
                            if (bar.equals(barcode)) {
                                isScaned = true;
                                break;
                            }
                        }
                        if (isScaned) {
                            // etBarcode.setTextHint(barcode);
                            etBarcode.setTextCt("", false);
                            ToastUtil.showToast(mActivity, "条码重复扫描!");
                        }else {
                            oneBarcode.add(barcode);
                            tvOneDimensiona.setText(oneBarcode.size() + "/" + billInfo.getScanCount());
                            if (oneBarcode.size() == billInfo.getScanCount()) {
                                String scanCodes = "";
                                for (String bar : oneBarcode) {
                                    scanCodes = scanCodes + bar + ";";
                                }
                                showLoadingDialog();
                                presenter.getScanResult(billInfo.getBillNo(), billInfo.getCustomerCode(), getCheckCode(), scanCodes.substring(0, scanCodes.length() - 1));
                            }else {
                                // etBarcode.setTextHint(barcode);
                                etBarcode.setTextCt("", false);
                            }
                        }
                    }
                }
            }
            etBarcode.setTextCt("",false);
        }
    }

    @Override
    public void clear(int etId) {

    }

    @Override
    public void scan(int etId) {
        if (etId == 1){
            QrManager.getInstance().openCamera(mActivity, CODE_SCAN_BILLNO);
        }else if(etId == 2){
            if (billInfo == null) {
                ToastUtil.show(mActivity, "请先扫描或输入出库单号！", true);
            }else {
                QrManager.getInstance().openCamera(mActivity, CODE_SCAN_BARCODE);
            }
        }
    }

    @Override
    public void billGetSuccess(BillInfoBean data) {
        billInfo = data;
        showData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                etBarcode.requestEditFoucs();
            }
        }, 300);
    }
    private void showData() {
        if (oneBarcode == null) oneBarcode = new ArrayList<>();
        else oneBarcode.clear();
        tvCustomer.setText(billInfo.getCustomerName());
        tvOneDimensiona.setText("0/" + billInfo.getScanCount());
    }

    @Override
    public void billGetFail(String errTitle, String errDesc) {
        etBillNo.setTextCt("", true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                etBillNo.requestEditFoucs();
            }
        }, 300);
        ToastUtil.show(mActivity, errTitle,false);
    }
    //扫描解析成功
    @Override
    public void barcInfofGetSuccess(String codeData) {
        tvOneDimensiona.setText("0/" + billInfo.getScanCount());
        if (oneBarcode != null) oneBarcode.clear();
        if (codesList == null) codesList = new ArrayList<>();
        if (showList == null) showList = new ArrayList<>();
        if (showList.size() == 0 && codesList.size() > 0) codesList.clear();
        presenter.judgeResult(billInfo, codesList, showList, codeData);
        etBarcode.setTextCt("", true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                etBarcode.requestEditFoucs();
            }
        }, 300);
    }
    /*
     *
     * 扫描成功后处理各种判断条件后,满足条件，返回最终要显示的数据
     * @param count 总数量
     */
    @Override
    public void judgeAndResult(double count) {
        //解析成功后的所有数据
        tvBarcodeCount.setText(codesList.size() + "");
        tvCount.setText((int)count + "");
        scAdapter.notifyDataSetChanged(showList);
        closeLoadingDialog();
        ifNeedTemStorage = true;
    }

    /**
     * 扫描成功后处理各种判断条件后,未满足条件，终止本次扫描
     * @param msg 提示信息
     * @param ifAutoCloseToast 是否自动关闭提示
     */
    @Override
    public void judgeAndEnd(String msg, boolean ifAutoCloseToast) {
        closeLoadingDialog();
        if (ifAutoCloseToast) ToastUtil.showToast(mActivity, msg);
        else ToastUtil.show(mActivity, msg, false);
    }

    @Override
    public void barcInfofGetFail(int resultCode, String errTitle, String errDesc) {
        closeLoadingDialog();
        etBarcode.setTextCt("", true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                etBarcode.requestEditFoucs();
            }
        }, 300);
        if (resultCode == 1002) {
            //需进行权限升级
            if (ActivityUtils.isRunning(mActivity)) {
                CustomDialog.openUpgradeDialog(mActivity,
                        TextUtils.isEmpty(errTitle) ? "后续操作需进行权限升级！" :errTitle, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RightUpgradeActivity.showUpgradeDialog(mActivity, CODE_TO_UPGRADE);
                    }
                });
            }
        }else {
            ToastUtil.show(mActivity, errTitle,false);
        }
    }
    //保存结果
    @Override
    public void saveResult(boolean isSuccess, String errTitle, String errDesc) {
        if (isSuccess) {
            ToastUtil.show(mActivity, errTitle,false);
            clearView();
            closeLoadingDialog();
        }else {
            closeLoadingDialog();
            ToastUtil.show(mActivity, errTitle, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_TO_UPGRADE) {
            if (resultCode == Activity.RESULT_OK) {
                //升级成功
            }
        }else {
            QrManager.getInstance().onActivityResult(mActivity, requestCode, resultCode, data, new QrManager.Callback() {
                @Override
                public void resultSuccess(String barcode) {
                    if(QrManager.getInstance().getRequestCode() == CODE_SCAN_BILLNO) {
                        confirm(1, barcode);
                    }else if(QrManager.getInstance().getRequestCode() == CODE_SCAN_BARCODE) {
                        confirm(2, barcode);
                    }
                }

                @Override
                public void resultFail(String msg) {
                    ToastUtil.show(mActivity, msg, false);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        FinishView();
    }

    Dialog backDialog = null;
    //退出界面前的逻辑
    private void FinishView() {
        if (showList != null && showList.size() >0 && ifNeedTemStorage) {
            if (ActivityUtils.isRunning(mActivity)) {
                backDialog = CustomDialog.createCancleDialog(mActivity, "扫码结果未暂存，确定退出吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backDialog.dismiss();
                        finish();
                    }
                });
                backDialog.show();
            }
        }else {
            finish();
        }
    }
}
