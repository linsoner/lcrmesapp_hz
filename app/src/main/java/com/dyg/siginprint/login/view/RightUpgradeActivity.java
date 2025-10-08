package com.dyg.siginprint.login.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.dingyg.qrcodelib.common.QrManager;
import com.dyg.siginprint.R;
import com.dyg.siginprint.base.model.Tokens;
import com.dyg.siginprint.base.tools.DoubleClickU;
import com.dyg.siginprint.base.tools.ToastUtil;
import com.dyg.siginprint.base.view.BaseActivity;
import com.dyg.siginprint.login.model.LoginBean;
import com.dyg.siginprint.login.presenter.ILoginView;
import com.dyg.siginprint.login.presenter.LoginPresenter;
import com.dyg.siginprint.main.view.MainActivity;
import com.dyg.siginprint.wiget.textview.ClearEditText;
import com.orhanobut.hawk.Hawk;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/06
 * desc : * 权限升级
 */
public class RightUpgradeActivity extends BaseActivity<LoginPresenter> implements ILoginView, ClearEditText.Listener {

    @BindView(R.id.view_account)
    ClearEditText viewAccount;//账号
    @BindView(R.id.view_password)
    ClearEditText viewPassword;//密码
    @BindView(R.id.bt_sure)
    Button bt_sure;
    final int CODE_SCAN_ACCOUNT = 703;//扫描出库单

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_right_upgrade;
    }

    @Override
    protected void initPresenter() {
        presenter = new LoginPresenter(RightUpgradeActivity.this,this);
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        viewAccount.setMinHeigth(RightUpgradeActivity.this,36);
        viewPassword.setMinHeigth(RightUpgradeActivity.this,36);
        viewAccount.initEditText(R.mipmap.user_icon1, "请输入用户名", 0,1, true,this);
        viewPassword.initEditText(R.mipmap.password_icon1, "请输入密码", 2,2, false,this);
    }

    @OnClick({R.id.bt_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_sure:
                if (!DoubleClickU.isFastDoubleClick(R.id.bt_sure)) {
                    upgrade();
                }
                break;
        }
    }
    //确认升级
    private void upgrade() {
        if (viewAccount.getTextCt().equals("")) {
            ToastUtil.showToast(this,"请输入用户名！");
            return;
        }
        if (viewPassword.getTextCt().equals("")) {
            ToastUtil.showToast(this, "请输入密码！");
            return;
        }
        showLoadingDialog();
        presenter.upgrade(viewAccount.getTextCt(), viewPassword.getTextCt());
    }

    @Override public void changed(int etId, String result) {}

    @Override
    public void confirm(int etId, String result) {}

    @Override public void clear(int etId) {}

    @Override
    public void scan(int etId) {
        QrManager.getInstance().openCamera(RightUpgradeActivity.this, CODE_SCAN_ACCOUNT);
    }

    @Override
    public void loginSuccess(String data) {
        closeLoadingDialog();
        if (data.equalsIgnoreCase("true")) {
            bt_sure.setEnabled(false);
            ToastUtil.showToast(RightUpgradeActivity.this, "升级成功");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RightUpgradeActivity.this.setResult(Activity.RESULT_OK);
                    finish();
                }
            }, 1200);
        }else {
            ToastUtil.showToast(RightUpgradeActivity.this, "升级失败");
        }
    }

    @Override
    public void loginFail(String errTitle, String errDesc) {
        closeLoadingDialog();
        ToastUtil.showToast(RightUpgradeActivity.this, errTitle);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void finish() {
        super.finish();
        //在此时设置转场动画
        overridePendingTransition(R.anim.dialog_scale_in,R.anim.dialog_scale_out);
    }
    //展开升级弹框
    public static void showUpgradeDialog(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, RightUpgradeActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QrManager.getInstance().onActivityResult(RightUpgradeActivity.this, requestCode, resultCode, data, new QrManager.Callback() {
            @Override
            public void resultSuccess(String barcode) {
                viewAccount.setTextCt(barcode, true);
            }

            @Override
            public void resultFail(String msg) {
                ToastUtil.show(RightUpgradeActivity.this, msg, false);
            }
        });
    }
}
