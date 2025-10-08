package com.dyg.siginprint.login.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.dyg.siginprint.app.RunningActivityList;
import com.dyg.siginprint.base.model.Tokens;
import com.dyg.siginprint.login.model.LoginBean;
import com.dyg.siginprint.login.view.LoginActivity;
import com.orhanobut.hawk.Hawk;

public class LoginManger {

    //退出登录，需要清空UUID等缓存数据,有多处LoginManger 注意检查
    public static void outLogin(Activity activity)
    {
        Hawk.delete(Tokens.UUID);
        LoginBean loginBean = Hawk.get(Tokens.LoginBean, null);
        LoginBean newBean = new LoginBean();
        if (loginBean == null) {
            newBean.setAccount(loginBean.getAccount());
            newBean.setPassword(loginBean.getPassword());
            newBean.setAutoLogin(false);
            Hawk.put(Tokens.LoginBean, newBean);
        }
        RunningActivityList.getInstance().finishOtherAllActivity(activity);
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
    //退出登录，需要清空UUID等缓存数据,有多处LoginManger 注意检查
    public static void outLogin(Context context)
    {
        Hawk.delete(Tokens.UUID);
        LoginBean loginBean = Hawk.get(Tokens.LoginBean, null);
        LoginBean newBean = new LoginBean();
        if (loginBean == null) {
            newBean.setAccount(loginBean.getAccount());
            newBean.setPassword(loginBean.getPassword());
            newBean.setAutoLogin(false);
            Hawk.put(Tokens.LoginBean, newBean);
        }
        RunningActivityList.getInstance().finishOtherAllActivity((Activity) context);
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }
}
