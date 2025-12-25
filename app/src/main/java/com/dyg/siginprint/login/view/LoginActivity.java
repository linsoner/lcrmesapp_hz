package com.dyg.siginprint.login.view;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dingyg.qrcodelib.common.QrManager;
import com.dyg.siginprint.R;
import com.dyg.siginprint.app.App;
import com.dyg.siginprint.base.model.Tokens;
import com.dyg.siginprint.base.tools.DoubleClickU;
import com.dyg.siginprint.base.tools.ToastUtil;
import com.dyg.siginprint.base.view.BaseActivity;
import com.dyg.siginprint.http.api.ApiList;
import com.dyg.siginprint.login.model.LoginBean;
import com.dyg.siginprint.login.presenter.ILoginView;
import com.dyg.siginprint.login.presenter.LoginPresenter;
import com.dyg.siginprint.main.view.MainActivity;
import com.dyg.siginprint.update.IntranetUpdateManager;
import com.dyg.siginprint.wiget.textview.ClearEditText;
import com.orhanobut.hawk.Hawk;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/06
 * desc : * 登录页
 */
public class LoginActivity extends BaseActivity<LoginPresenter> implements ILoginView, ClearEditText.Listener {

    @BindView(R.id.view_account)
    ClearEditText viewAccount;//账号
    @BindView(R.id.view_password)
    ClearEditText viewPassword;//密码
    private LoginBean loginBean;

    private ApkDownloadInstallUtils mApkUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化下载工具类
        mApkUtils = new ApkDownloadInstallUtils(this);
        // 设置下载监听
        mApkUtils.setOnDownloadListener(new ApkDownloadInstallUtils.OnDownloadListener() {
            @Override
            public void onDownloadStart() {
                // 可选：下载开始的UI提示
            }

            @Override
            public void onDownloadComplete() {
                Toast.makeText(LoginActivity.this, "下载完成，正在安装", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInstallStart() {
                // 可选：安装开始的提示
            }

            @Override
            public void onInstallFailed(String reason) {
                Toast.makeText(LoginActivity.this, "安装失败：" + reason, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNeedStoragePermission() {
                // 申请存储权限
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ApkDownloadInstallUtils.REQUEST_STORAGE_PERMISSION);
            }

            @Override
            public void onNeedInstallPermission(Intent intent) {
                // 跳转到安装权限设置页
                startActivityForResult(intent, ApkDownloadInstallUtils.REQUEST_INSTALL_PERMISSION);
            }
        });

        // 绑定下载按钮点击事件
        findViewById(R.id.btn_direct_download_apk).setOnClickListener(v -> {
            // 拼接下载地址（和MainActivity逻辑一致）
            String downloadUrl = getApkDownloadUrl();
            // 开始下载
            mApkUtils.startDownload(downloadUrl);
        });
    }

    /**
     * 拼接APK下载地址（复用项目原有逻辑）
     */
    private String getApkDownloadUrl() {
        String serverAddress = "http://192.168.231.6:9001"; // 项目原有服务器地址
        String furl = "http://192.168.231.6:9001"; // 协议头（http/https）+ 地址
        return furl + ApiList.API_GET_APK; // 最终地址：服务器地址/api/files/app
    }

    /**
     * 权限请求回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ApkDownloadInstallUtils.REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 存储权限授予，重新开始下载
                mApkUtils.startDownload(getApkDownloadUrl());
            } else {
                Toast.makeText(this, "存储权限被拒绝，无法下载APK", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 页面销毁时释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mApkUtils != null) {
            mApkUtils.release();
        }
    }


    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_login;
    }

    @Override
    protected void initPresenter() {
        presenter = new LoginPresenter(LoginActivity.this,this);
        initTitle(false, "登录", "服务器设置", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("data_from_login", true);
                startNewAct(ServerSetActivity.class, bundle);
            }
        });
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        viewAccount.setMinHeigth(LoginActivity.this,36);
        viewPassword.setMinHeigth(LoginActivity.this,36);
        viewAccount.initEditText(R.mipmap.user_icon1, "请输入用户名", 0,1, true,this);
        viewPassword.initEditText(R.mipmap.password_icon1, "请输入密码", 2,2, false,this);
        loginBean = new LoginBean();
        LoginBean saveLoginBean = Hawk.get(Tokens.LoginBean, null);
        if (saveLoginBean != null) {
            loginBean.setAccount(saveLoginBean.getAccount());
            loginBean.setPassword(saveLoginBean.getPassword());
            viewPassword.setTextCt(saveLoginBean.getPassword(), false);
            viewAccount.setTextCt(saveLoginBean.getAccount(), true);
        }
    }


    @OnClick({R.id.bt_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                if (!DoubleClickU.isFastDoubleClick(R.id.bt_login)) {
                    login();
                }
                break;
        }
    }
    private void login() {
        if (viewAccount.getTextCt().equals("")) {
            ToastUtil.showToast(this,"请输入用户名！");
            return;
        }
        loginBean.setAccount(viewAccount.getTextCt());
        if (viewPassword.getTextCt().equals("")) {
            ToastUtil.showToast(this, "请输入密码！");
            return;
        }
        loginBean.setPassword(viewPassword.getTextCt());
        showLoadingDialog();
        presenter.sendLogin(loginBean);
    }

    @Override public void changed(int etId, String result) {}

    @Override
    public void confirm(int etId, String result) {}

    @Override public void clear(int etId) {
        if (etId == 1) {
            if(viewPassword != null) viewPassword.setTextCt("", false);
        }
    }

    @Override
    public void scan(int etId) {
        QrManager.getInstance().openCamera(LoginActivity.this, 0);
    }

    @Override
    public void loginSuccess(String data) {
        closeLoadingDialog();
        Hawk.put(Tokens.UUID, data);
        loginBean.setAutoLogin(true);
        Hawk.put(Tokens.LoginBean, loginBean);
        Bundle bundle = new Bundle();
        bundle.putBoolean("data_from_login", true);
        startNewAct(MainActivity.class, bundle, true);
    }

    @Override
    public void loginFail(String errTitle, String errDesc) {
        closeLoadingDialog();
        ToastUtil.showToast(LoginActivity.this, errTitle);
        loginBean.setAutoLogin(false);
        Hawk.put(Tokens.LoginBean, loginBean);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QrManager.getInstance().onActivityResult(LoginActivity.this, requestCode, resultCode, data, new QrManager.Callback() {
            @Override
            public void resultSuccess(String barcode) {
                viewAccount.setTextCt(barcode, true);
            }

            @Override
            public void resultFail(String msg) {
                ToastUtil.show(LoginActivity.this, msg, false);
            }
        });

        if (requestCode == ApkDownloadInstallUtils.REQUEST_INSTALL_PERMISSION) {
            // 从设置页返回，重新检查权限并下载
            mApkUtils.startDownload(getApkDownloadUrl());
        }
    }
}
