package com.dyg.siginprint.main.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dyg.siginprint.R;
import com.dyg.siginprint.app.RunningActivityList;
import com.dyg.siginprint.app.App;
import com.dyg.siginprint.base.model.Tokens;
import com.dyg.siginprint.base.tools.DoubleClickU;
import com.dyg.siginprint.base.tools.ToastUtil;
import com.dyg.siginprint.base.view.BaseActivity;
import com.dyg.siginprint.http.api.ApiList;
import com.dyg.siginprint.login.model.LoginBean;
import com.dyg.siginprint.login.presenter.ILoginView;
import com.dyg.siginprint.login.presenter.LoginPresenter;
import com.dyg.siginprint.login.view.LoginActivity;
import com.dyg.siginprint.login.view.RightUpgradeActivity;
import com.dyg.siginprint.login.view.ServerSetActivity;
import com.dyg.siginprint.main.adapter.MainAdapter;
import com.dyg.siginprint.main.model.FunItemBean;
import com.dyg.siginprint.main.presenter.IMainView;
import com.dyg.siginprint.main.presenter.MainPresenter;
import com.dyg.siginprint.sigin.view.SiginCheckingActivity;
import com.dyg.siginprint.update.ActivityUtils;
import com.dyg.siginprint.update.CustomDialog;
import com.dyg.siginprint.update.IntranetUpdateManager;
import com.orhanobut.hawk.Hawk;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页
 */
public class MainActivity extends BaseActivity<MainPresenter> implements IMainView {

    @BindView(R.id.grid_recyclerView)
    RecyclerView recyclerView;

    Activity mActivity;
    //更新
    private IntranetUpdateManager intUpdateManager;
    private Thread updateThread = null;//更新线程
    private Dialog cancleDialog;

    GridLayoutManager gridLayoutManager;
    MainAdapter mainAdapter;
    List<FunItemBean> funList = Arrays.asList(
            new FunItemBean("材料收货",R.mipmap.caigoujinhuo,"1"),
            new FunItemBean("材料拆标签",R.mipmap.chaifen,"10"),
            new FunItemBean("材料核对",R.mipmap.heduisaoma,"2"),
            new FunItemBean("材料其他入库",R.mipmap.cailiaoqitaruku,"14"),
            new FunItemBean("材料其他出库",R.mipmap.cailiaoqitachuku,"15"),
            new FunItemBean("材料调拨",R.mipmap.cailiaodiaobo,"16"),

            new FunItemBean("成品收货",R.mipmap.caigoujinhuored,"11"),
            new FunItemBean("成品核对",R.mipmap.heduisaomared,"8"),
            new FunItemBean("成品调拨",R.mipmap.chengpintiaobo,"3"),
            new FunItemBean("成品其他入库",R.mipmap.chengpinqitaruku,"12"),
            new FunItemBean("成品其他出库",R.mipmap.chengpinqitachuku,"13"),
            new FunItemBean("客户标签核对",R.mipmap.hehubiaoqianhedui,"4"),
            new FunItemBean("成品拆标签",R.mipmap.chaifenred,"5"),
            new FunItemBean("",R.mipmap.empty,"-1"),
            new FunItemBean("",R.mipmap.empty,"-1"),

            new FunItemBean("生产核对",R.mipmap.heduisaoma,"9"),
            new FunItemBean("开工",R.mipmap.kaigong,"17"),
            new FunItemBean("报工",R.mipmap.baogong,"18"),
            new FunItemBean("分切出入库",R.mipmap.shengchanruku,"6"),
            new FunItemBean("设备保养",R.mipmap.shebeibaoyang,"7")
    );

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initPresenter() {
        mActivity = MainActivity.this;
        presenter = new MainPresenter(mActivity, this);
        initTitle(false, App.res.getString(R.string.app_name), "注销", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityUtils.isRunning(mActivity)) {
                    cancleDialog = CustomDialog.createCancleDialog(mActivity, "确定登出当前登录吗？", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancleDialog.dismiss();
                                    presenter.LogOut();
                                }
                            });
                    cancleDialog.show();
                }
            }
        });
    }


    @Override
    protected void initViews(Bundle savedInstanceState) {
        final String address = Hawk.get(Tokens.ServerAdr, "");
        if (address.equals("")) {
            startNewAct(ServerSetActivity.class, true);
        } else {
            boolean isFromLogin = false;
            try {
                isFromLogin = getIntent().getExtras().getBoolean("data_from_login");
            } catch (Exception e) {
            }
            if (isFromLogin) {
                mainCreate(address);
                gridLayoutManager = new GridLayoutManager(mActivity,3);
                mainAdapter = new MainAdapter(mActivity);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setAdapter(mainAdapter);
                mainAdapter.notifyDataSetChanged(funList);
            } else {
                String uuid = Hawk.get(Tokens.UUID, "");
                if (uuid.equals("")) {
                    startNewAct(LoginActivity.class, true);
                } else {
                    LoginBean loginBean = Hawk.get(Tokens.LoginBean, null);
                    if (loginBean != null && !loginBean.isAutoLogin() && !loginBean.getAccount().equals("") || !loginBean.getPassword().equals("")) {
                        //自动登录
                        new LoginPresenter(MainActivity.this, new ILoginView() {
                            @Override
                            public void loginSuccess(String data) {
                                Hawk.put(Tokens.UUID, data);
                                mainCreate(address);

                                gridLayoutManager = new GridLayoutManager(mActivity,3);
                                mainAdapter = new MainAdapter(mActivity);
                                recyclerView.setLayoutManager(gridLayoutManager);
                                recyclerView.setAdapter(mainAdapter);

                                mainAdapter.notifyDataSetChanged(funList);
                            }

                            @Override
                            public void loginFail(String errTitle, String errDesc) {
                                ToastUtil.show(mActivity, errTitle);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startNewAct(LoginActivity.class, true);
                                    }
                                }, 2000);
                            }
                        }).sendLogin(loginBean);
                    } else {
                        startNewAct(LoginActivity.class, true);
                    }
                }
            }
        }
    }

    private void mainCreate(String address) {
        String furl = App.getAgreement() + address;
        String url[] = new String[]{furl + ApiList.API_SYS_VERSION, furl + ApiList.API_GET_APK};
        //内网 WarehousetManager.apk
        intUpdateManager = new IntranetUpdateManager(this, url, "com.dyg.siginprint.updatefileprovider");
        updateThread = new Thread(checkSelfUpdateInt);
        updateThread.start();
    }

    // 返回home
    long flag = -1;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
            if (flag == -1 || System.currentTimeMillis() - flag > 2000) {
                ToastUtil.showToast(this, "再点击一次退出应用");
                flag = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - flag < 2000) {
                RunningActivityList.getInstance().exit();
            }
        }
        return true;
    }

    Dialog dialog;
    //内网更新
    Runnable checkSelfUpdateInt = new Runnable() {
        @Override
        public void run() {
            // TODO 这里写上传逻辑
            try {
                String localVerCode = intUpdateManager.getLocalVerCode();
                String serverVerCode = intUpdateManager.getServerVerCode();
                // 如果服务器版本高于本地版本则提示更新
                if (intUpdateManager.compareVerson(localVerCode, serverVerCode) > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ActivityUtils.isRunning(mActivity)) {
                                dialog = CustomDialog.createUpdateDialog(mActivity, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        dialog.setCancelable(true);
                                        intUpdateManager.checkIsAndroid0();
                                    }
                                }, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                        }
                    });
                }
            } catch (Exception e) {
            }
        }
    };

    @Override
    public void logoutSuccess() {
        mActivity.finish();
    }

    @Override
    public void logoutFail(String errTitle, String errDesc) {

    }
}
