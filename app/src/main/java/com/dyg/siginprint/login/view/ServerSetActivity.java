package com.dyg.siginprint.login.view;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.support.annotation.NonNull;

import com.dingyg.qrcodelib.common.QrManager;
import com.dyg.siginprint.R;
import com.dyg.siginprint.app.App;
import com.dyg.siginprint.base.model.Tokens;
import com.dyg.siginprint.base.tools.NetUtils;
import com.dyg.siginprint.base.tools.ToastUtil;
import com.dyg.siginprint.base.view.BaseActivity;
import com.dyg.siginprint.login.presenter.IServerSetView;
import com.dyg.siginprint.login.presenter.ServerSetPresenter;
import com.dyg.siginprint.wiget.dialog.QrCodeDialog;
import com.dyg.siginprint.wiget.textview.ClearEditText;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.orhanobut.hawk.Hawk;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/06
 * desc : * 登录页
 */

public class ServerSetActivity extends BaseActivity<ServerSetPresenter> implements IServerSetView, ClearEditText.Listener {
    ServerSetActivity mContext = this;
    @BindView(R.id.et_sj_server_address)
    ClearEditText etServerAddress;//数据服务器
    @BindView(R.id.ibt_sj_scan)
    ImageButton ibfScan;
    @BindView(R.id.bt_save)
    Button btSave;

    private QrCodeDialog mQrCodeDialog;
    private boolean isFromLoginView;//是否来自登录页

    /**
     * 绑定布局文件
     *
     * @return 布局文件ID
     */
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_server_set;
    }
    /**
     * 初始化presenter
     */
    @Override
    protected void initPresenter() {
        presenter = new ServerSetPresenter(ServerSetActivity.this,this);
        try {
            isFromLoginView = getIntent().getExtras().getBoolean("data_from_login", false);
        }catch (Exception e){
            isFromLoginView = false;
        }
        initTitle(true, "设置服务器地址", null, null, null);
        etServerAddress.initEditText(0, "请输入服务器地址", 0, 0,  false,this);
    }
    /**
     * 初始化视图控件
     */
    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        PermissionsUtil.requestPermission(mContext, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                mQrCodeDialog = new QrCodeDialog(ServerSetActivity.this, new QrManager.Callback() {
                    @Override
                    public void resultSuccess(String barcode) {
                        etServerAddress.setTextCt(barcode, true);
                    }

                    @Override
                    public void resultFail(String msg) {
                        ToastUtil.show(mContext, msg, false);
                    }
                });
                mQrCodeDialog.setGravityBottom();
                //服务器地址
                String address = Hawk.get(Tokens.ServerAdr, "");
                if (address != null && !address.equals("")) {
                    etServerAddress.setTextCt(App.getAgreement() + address, true);
                }
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                ToastUtil.showToast(mContext, "您拒绝了相机必要权限，无法正常使用！");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    @OnClick({R.id.ibt_sj_scan, R.id.bt_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ibt_sj_scan: {
                mQrCodeDialog.show();
            }
            break;
            case R.id.bt_save:
                if (etServerAddress.getTextCt().equals("")) {
                    ToastUtil.showToast(mContext, "请输入服务器地址！");
                    return;
                }
                saveAddress(etServerAddress.getTextCt());
                break;
        }
    }

    private void saveAddress(String inputText) {
        showLoadingDialog();
        String edtInput_sj = inputText.toLowerCase();
        String agreement = "";//协议
        if (edtInput_sj.contains("://")) {
            agreement = edtInput_sj.substring(0, edtInput_sj.indexOf(":") + 3);
            edtInput_sj = edtInput_sj.replace(agreement, "");
        }else {
            agreement = "http://";//没有输入协议则默认http://
        }
        Hawk.put(Tokens.Agreement, agreement);
        //判断url是否符合标准
        if (NetUtils.isMatcheUrl(edtInput_sj)) {
            //符合标准
            for(int i=0; i<5; i++){
                if(edtInput_sj.endsWith("/")) edtInput_sj = edtInput_sj.substring(0,edtInput_sj.length()-1);
                else break;
            }
            Hawk.put(Tokens.ServerAdr, edtInput_sj);
            closeLoadingDialog();
            if (!isFromLoginView) {
                startNewAct(LoginActivity.class, true);
            }else {
                finish();
            }
        } else{
            //不符合标准
            ToastUtil.showToast(mContext, "请输入正确的服务器地址！");
            closeLoadingDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mQrCodeDialog.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void checkSuccess(String data) {

    }

    @Override
    public void checkFail(String errTitle, String errDesc) {

    }

    @Override
    public void changed(int etId, String result) {}

    @Override
    public void confirm(int etId, String result) {}

    @Override
    public void clear(int etId) {}

    @Override
    public void scan(int etId) {

    }


}
