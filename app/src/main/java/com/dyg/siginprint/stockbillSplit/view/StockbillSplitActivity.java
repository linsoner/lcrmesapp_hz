package com.dyg.siginprint.stockbillSplit.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dingyg.qrcodelib.common.QrManager;
import com.dyg.siginprint.R;
import com.dyg.siginprint.base.model.Tokens;
import com.dyg.siginprint.base.tools.DoubleClickU;
import com.dyg.siginprint.base.tools.HawkKeys;
import com.dyg.siginprint.base.tools.ToastUtil;
import com.dyg.siginprint.base.view.BaseActivity;
import com.dyg.siginprint.login.model.LoginBean;
import com.dyg.siginprint.purchase.model.PuechaseModel;
import com.dyg.siginprint.purchase.presenter.IPurchaseView;
import com.dyg.siginprint.purchase.presenter.PurchasePresenter;
import com.dyg.siginprint.wiget.dialog.ListDialog;
import com.dyg.siginprint.wiget.dialog.model.BaseDataModel;
import com.dyg.siginprint.wiget.textview.ClearEditText;
import com.dyg.siginprint.wiget.textview.MyEditText;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.OnClick;

public class StockbillSplitActivity extends BaseActivity<PurchasePresenter> implements IPurchaseView, ClearEditText.Listener {

    private TextView selectPrinterTv;//打印机
    private ClearEditText qrCodeEt;//原条码
    private TextView originalNumTv;//原数量
    private MyEditText BoxOneNumTv;//新箱1数量
    private MyEditText BoxTwoNumTv;//新箱2数量

    private Activity mActivity;
    private String userCode;

    private PuechaseModel currentSplitModel = new PuechaseModel();
    private String printerCode;//打印机代号
    private String qrCode;//条码


    private int CODE_SCAN_QRCODE = 102;

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_stockbill_split;
    }

    @Override
    protected void initPresenter() {
        presenter = new PurchasePresenter(StockbillSplitActivity.this, this);
        initTitle(true, "拆标签", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishView();
            }
        },null);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        LoginBean loginBean = Hawk.get(Tokens.LoginBean, null);
        if (loginBean == null) finish();
        else {
            userCode = loginBean.getAccount();
            mActivity = StockbillSplitActivity.this;
            selectPrinterTv = findViewById(R.id.selectPrinterTv);
            originalNumTv = findViewById(R.id.originalNumTv);
            qrCodeEt = findViewById(R.id.qr_codeEt);
            BoxOneNumTv = findViewById(R.id.BoxOneNumTv);
            BoxTwoNumTv = findViewById(R.id.BoxTwoNumTv);

            selectPrinterTv.setText(Hawk.get(HawkKeys.HAWK_printerName,""));
            printerCode = Hawk.get(HawkKeys.HAWK_printerCode,"");

            BoxOneNumTv.setMustNumberAndPoint(true);
            BoxTwoNumTv.setMustNumberAndPoint(true);
            BoxOneNumTv.setListener(new MyEditText.Listener() {
                @Override
                public void changed(int etId, String result) {

                }

                @Override
                public void confirm(int etId, String result) {
                   //计算新箱2的数量
                    try {
                        Double resultNum = currentSplitModel.getQty() - Double.parseDouble(BoxOneNumTv.getText());
                        BoxTwoNumTv.setText(String.valueOf(resultNum));
                    }catch (Exception e){

                    }
                }
            });

            qrCodeEt.initEditText(0, "", 0, 3, true, this);
        }
    }

    @Override
    public void baseInfoSuccess(List dataList, int code) {
        closeLoadingDialog();
        ListDialog listDialog = new ListDialog(mActivity, new ListDialog.Callback() {
            @Override
            public void selectFinish(Object obj, int position) {
                BaseDataModel bean = (BaseDataModel) obj;
                if(code == -88){
                    selectPrinterTv.setText(bean.getName());
                    printerCode = bean.getCode();
                    Hawk.put(HawkKeys.HAWK_printerCode,printerCode);
                    Hawk.put(HawkKeys.HAWK_printerName,bean.getName());
                }
            }
        });
        listDialog.show(dataList,"打印机","name");
        listDialog.setGravityBottom();
    }

    @Override
    public void requestFail(int stautsCode, int code, String msg) {
      closeLoadingDialog();
      ToastUtil.show(mActivity,msg,false);
    }

    @Override
    public void qrcodeAnalysisSuccess(PuechaseModel splitModel) {
        closeLoadingDialog();
        currentSplitModel = splitModel;
        originalNumTv.setText(splitModel.getQty()+"");
        BoxOneNumTv.setText(splitModel.getQty1() == 0 ? "" : splitModel.getQty1() + "");
        BoxTwoNumTv.setText(splitModel.getQty2() ==0 ? "" : splitModel.getQty2() + "");
    }

    @Override
    public void saveQrCodeSuccess(String msg) {
        closeLoadingDialog();
        ToastUtil.show(mActivity,msg,false);
        clearView(false);
    }

    @Override
    public void changed(int etId, String result) {

    }

    @Override
    public void confirm(int etId, String result) {
        if(etId == 3){
            //条码解析
            qrCodeEt.setTextCt("",false);
            //设置焦点
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    qrCodeEt.requestEditFoucs();
                }
            }, 300);
            JSONObject joData = new JSONObject();
            try {
                joData.put("qrCode", result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showLoadingDialog();
            qrCode = result;
            presenter.requestAnalysisQRCode("FormpPortPrint" ,joData);
        }
    }

    @Override
    public void clear(int etId) {

    }

    @Override
    public void scan(int etId) {
        if(etId == 3){
            QrManager.getInstance().openCamera(mActivity, CODE_SCAN_QRCODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QrManager.getInstance().onActivityResult(mActivity, requestCode, resultCode, data, new QrManager.Callback() {
            @Override
            public void resultSuccess(String barcode) {
                if(QrManager.getInstance().getRequestCode() == CODE_SCAN_QRCODE){
                    confirm(3, barcode);
                }
            }

            @Override
            public void resultFail(String msg) {
                ToastUtil.show(mActivity, msg, false);
            }
        });
    }

    @OnClick({R.id.selectPrinterId,R.id.tv_save,R.id.tv_clearing})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.selectPrinterId : {
                showLoadingDialog();
                presenter.requestBaseInfo("FormpPortPrint",-88);
            }
            break;
            case R.id.tv_clearing : {
                clearView(false);
            }
            break;
            case R.id.tv_save : {
                //保存
                if(!DoubleClickU.isFastDoubleClick(R.id.tv_save)){

                    if(printerCode == null || printerCode.equals("")){
                        ToastUtil.show(mActivity,"请先选择打印机！",true);
                        return;
                    }

                    if(currentSplitModel.getSn() == null || currentSplitModel.getSn().equals("")){
                        ToastUtil.show(mActivity,"暂无数据，打印无效！",true);
                        return;
                    }

                    if(BoxOneNumTv.getText() == null || BoxOneNumTv.getText().equals("")){
                        ToastUtil.show(mActivity,"请输入新箱1数量！",true);
                        return;
                    }

                    if(BoxTwoNumTv.getText() == null || BoxTwoNumTv.getText().equals("")){
                        ToastUtil.show(mActivity,"请输入新箱2数量！",true);
                        return;
                    }

                    try {
                        Gson gson = new Gson();
                        String jsonString = gson.toJson(currentSplitModel);
                        JSONObject joData = new JSONObject(jsonString);
                        joData.put("printerCode", printerCode);
                        joData.put("qrCode", qrCode);
                        joData.put("qty1",BoxOneNumTv.getText());
                        joData.put("qty2",BoxTwoNumTv.getText());
                        showLoadingDialog();
                        presenter.requestPrint("FormpPortPrint",joData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    //退出界面前的逻辑
    private void FinishView() {
        finish();
    }

    private void clearView(boolean clearDefuatCache){
        originalNumTv.setText("");
        BoxOneNumTv.setText("");
        BoxTwoNumTv.setText("");
        currentSplitModel = new PuechaseModel();
    }
}
