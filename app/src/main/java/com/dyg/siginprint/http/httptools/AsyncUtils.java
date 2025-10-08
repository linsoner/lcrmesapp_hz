package com.dyg.siginprint.http.httptools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.dyg.siginprint.app.RunningActivityList;
import com.dyg.siginprint.app.App;
import com.dyg.siginprint.base.model.BaseBean;
import com.dyg.siginprint.base.model.Tokens;
import com.dyg.siginprint.base.tools.CommUtil;
import com.dyg.siginprint.base.tools.LogUtils;
import com.dyg.siginprint.base.tools.NetUtils;
import com.dyg.siginprint.base.tools.ToastUtil;
import com.dyg.siginprint.login.model.LoginBean;
import com.dyg.siginprint.login.presenter.LoginManger;
import com.dyg.siginprint.login.view.LoginActivity;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.jesse.nativelogger.NLogger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AsyncUtils {

    public interface AsyncCallback {
        void success(int requestCode, String data , BaseBean bean);
        void fail(int requestCode, int statusCode, String data, String msg);
    }


    /**
     * 异步请求 post
     *
     * @param context
     * @param url
     * @param jsonParams
     * @param requestCode
     * @param callback    timeout 请求超时时间设置 默认40秒
     */
    public static void post(final Context context, final String url, final JSONObject jsonParams, final int requestCode, final int timeout,
                            final AsyncCallback callback) {
        if (!NetUtils.isConnectInternet(context)) {
            NetUtils.IfNetOff_OpenSetUI(context);
            String emsg = "您的WLAN和移动网络均未连接！";
            callback.fail(requestCode, -1, "", emsg);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (App.client == null) {
                            App.client = new OkHttpClient();
                        }
                        OkHttpClient copyClient;
                        if (timeout != 0) {
                            copyClient = App.client.newBuilder().writeTimeout(timeout, TimeUnit.SECONDS)
                                    .readTimeout(timeout, TimeUnit.SECONDS)
                                    .connectTimeout(timeout, TimeUnit.SECONDS).build();
                        }else {
                            copyClient = App.client.newBuilder().writeTimeout(20, TimeUnit.SECONDS)
                                    .readTimeout(20, TimeUnit.SECONDS)
                                    .connectTimeout(20, TimeUnit.SECONDS).build();
                        }
                        final String fullUrl = App.getAgreement() + Hawk.get(Tokens.ServerAdr, "") + url;
                        LogError("URL","请求URL-" + fullUrl);
                        final String requestParams = addComParams(jsonParams, true);
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body = RequestBody.create(JSON, requestParams);
                        final Request request = setHeader(new Request.Builder().url(fullUrl).post(body));
                        final Call call = copyClient.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                new Handler(context.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String result = e.getMessage() == null ? e.toString() : e.getMessage();
                                            callback.fail(requestCode, -1, result, getFailure(result));
                                            LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + result);
                                        } catch (Exception e1) {
                                            showFailText(context, url, requestCode,-1, e1.getMessage(), callback);
                                            LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "出错" + e1.getMessage());
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.code() == 200) {
                                    final String result = response.body().string();
                                    final BaseBean bean = JsonUtils.getDataBean(result);
                                    new Handler(context.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                                                if (bean != null) {
                                                    if (bean.getCode() == 1001) {//成功
                                                        LogUtils.d("请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + result);
                                                        callback.success(requestCode, bean.getData(),bean);
                                                    }else if (bean.getCode() == 1098) {//登录过期
                                                        logOut(context, bean.getMsg());
                                                        LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + result);
                                                    }else {
                                                        callback.fail(requestCode, bean.getCode(), bean.getData(), bean.getMsg());
                                                        LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + result);
                                                    }
                                                } else {
                                                    callback.fail(requestCode, -1, null, getFailure(result));
                                                    LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + result);
                                                }
                                            } catch (Exception e2) {
                                                callback.fail(requestCode, -1, null, getFailure(result));
                                                LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + result + "出错" + e2.getMessage());
                                            }
                                        }
                                    });
                                } else if (response.code() == 500) {
                                    new Handler(context.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.fail(requestCode, -1, "", "服务器内部错误（500）");
                                        }
                                    });
                                } else {
                                    showFailText(context, url, requestCode, response.code(), null, callback);
                                    LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + response.body().string());
                                }
                            }
                        });
                    } catch (Exception e) {
                        showFailText(context, url, requestCode, -1, e.getMessage(), callback);
                        LogError("get", "请求URL-" + url + "请求参数-" + jsonParams.toString() + "错误-" + e.getMessage());
                    }
                }
            }).start();
        }
    }

    /**
     * 异步请求 post
     *
     * @param context
     * @param url
     * @param jsonParams
     * @param requestCode
     * @param callback    timeout 请求超时时间设置 默认40秒
     */
    public static void postNoUserCode(final Context context, final String url, final JSONObject jsonParams, final int requestCode, final int timeout,
                            final AsyncCallback callback) {
        if (!NetUtils.isConnectInternet(context)) {
            NetUtils.IfNetOff_OpenSetUI(context);
            String emsg = "您的WLAN和移动网络均未连接！";
            callback.fail(requestCode, -1, "", emsg);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (App.client == null) {
                            App.client = new OkHttpClient();
                        }
                        OkHttpClient copyClient;
                        if (timeout != 0) {
                            copyClient = App.client.newBuilder().writeTimeout(timeout, TimeUnit.SECONDS)
                                    .readTimeout(timeout, TimeUnit.SECONDS)
                                    .connectTimeout(timeout, TimeUnit.SECONDS).build();
                        }else {
                            copyClient = App.client.newBuilder().writeTimeout(20, TimeUnit.SECONDS)
                                    .readTimeout(20, TimeUnit.SECONDS)
                                    .connectTimeout(20, TimeUnit.SECONDS).build();
                        }
                        final String fullUrl = App.getAgreement() + Hawk.get(Tokens.ServerAdr, "") + url;
                        final String requestParams = addComParams(jsonParams, false);
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body = RequestBody.create(JSON, requestParams);
                        final Request request = setHeader(new Request.Builder().url(fullUrl).post(body));
                        final Call call = copyClient.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                new Handler(context.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String result = e.getMessage() == null ? e.toString() : e.getMessage();
                                            callback.fail(requestCode, -1, result, getFailure(result));
                                            LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + result);
                                        } catch (Exception e1) {
                                            showFailText(context, url, requestCode,-1, e1.getMessage(), callback);
                                            LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "出错" + e1.getMessage());
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.code() == 200) {
                                    final String result = response.body().string();
                                    final BaseBean bean = JsonUtils.getDataBean(result);
                                    new Handler(context.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                                                if (bean != null) {
                                                    if (bean.getCode() == 1001) {//成功
                                                        callback.success(requestCode, bean.getData(),bean);
                                                    }else if (bean.getCode() == 1098) {//登录过期
                                                        logOut(context, bean.getMsg());
                                                        LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + result);
                                                    }else {
                                                        callback.fail(requestCode, bean.getCode(), bean.getData(), bean.getMsg());
                                                        LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + result);
                                                    }
                                                } else {
                                                    callback.fail(requestCode, -1, null, getFailure(result));
                                                    LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + result);
                                                }
                                            } catch (Exception e2) {
                                                callback.fail(requestCode, -1, null, getFailure(result));
                                                LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + result + "出错" + e2.getMessage());
                                            }
                                        }
                                    });
                                } else if (response.code() == 500) {
                                    new Handler(context.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.fail(requestCode, -1, "", "服务器内部错误（500）");
                                        }
                                    });
                                } else {
                                    showFailText(context, url, requestCode, response.code(), null, callback);
                                    LogError("post", "请求URL-" + fullUrl + "请求参数-" + requestParams + "返回-" + response.body().string());
                                }
                            }
                        });
                    } catch (Exception e) {
                        showFailText(context, url, requestCode, -1, e.getMessage(), callback);
                        LogError("get", "请求URL-" + url + "请求参数-" + jsonParams.toString() + "错误-" + e.getMessage());
                    }
                }
            }).start();
        }
    }

    /**
     * 异步请求 get
     *
     * @param context
     * @param url
     * @param requestCode
     * @param callback
     */
    public static void get(final Context context, final String url, final int requestCode, final int timeout,
                           final AsyncCallback callback) {
        if (!NetUtils.isConnectInternet(context)) {
            NetUtils.IfNetOff_OpenSetUI(context);
            String emsg = "您的WLAN和移动网络均未连接！";
            callback.fail(requestCode, -1, "", emsg);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (App.client == null) {
                            App.client = new OkHttpClient();
                        }
                        OkHttpClient copyClient;
                        if (timeout == 0) {
                            copyClient = App.client.newBuilder().writeTimeout(20, TimeUnit.SECONDS)
                                    .readTimeout(20, TimeUnit.SECONDS)
                                    .connectTimeout(20, TimeUnit.SECONDS).build();
                        }else {
                            copyClient = App.client.newBuilder().writeTimeout(timeout, TimeUnit.SECONDS)
                                    .readTimeout(timeout, TimeUnit.SECONDS)
                                    .connectTimeout(timeout, TimeUnit.SECONDS).build();
                        }
                        final String fullUrl = App.getAgreement() + Hawk.get(Tokens.ServerAdr, "") + url;
                        
                        final okhttp3.Request request = setHeader(new okhttp3.Request.Builder().url(fullUrl).get());
                        final Call call = copyClient.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                new Handler(context.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String result = e.getMessage() == null ? e.toString() : e.getMessage();
                                            callback.fail(requestCode, -1, result, getFailure(result));
                                            LogError("get", "请求URL-" + fullUrl + "请求参数-无" + "返回-" + result);
                                        } catch (Exception e1) {
                                            showFailText(context, url, requestCode,-1, e1.getMessage(), callback);
                                            LogError("get", "请求URL-" + fullUrl + "请求参数-无" + "出错-" + e1.getMessage());
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.code() == 200) {
                                    final String result = response.body().string();
                                    final BaseBean bean = JsonUtils.getDataBean(result);
                                    new Handler(context.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                                                if (bean != null) {
                                                    if (bean.getCode() == 1001) {//成功
                                                        callback.success(requestCode, bean.getData(),bean);
                                                    } else if (bean.getCode() == 1098) {//登录过期
                                                        logOut(context, bean.getMsg());
                                                        LogError("post", "请求URL-" + fullUrl + "请求参数-无" + "返回-" + result);
                                                    } else {
                                                        callback.fail(requestCode, bean.getCode(), bean.getData(), bean.getMsg());
                                                        LogError("post", "请求URL-" + fullUrl + "请求参数-无" + "返回-" + result);
                                                    }
                                                } else {
                                                    callback.fail(requestCode, -1, null, getFailure(result));
                                                    LogError("post", "请求URL-" + fullUrl + "请求参数-无" + "返回-" + result);
                                                }
                                            } catch (Exception e2) {
                                                LogError("post", "请求URL-" + fullUrl + "请求参数-无" + "返回-" + result + "错误-" + e2.getMessage());
                                            }
                                        }
                                    });
                                } else if (response.code() == 500) {
                                    new Handler(context.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.fail(requestCode, -1, "", "服务器内部错误（500）");
                                        }
                                    });
                                } else {
                                    showFailText(context, url, requestCode, response.code(), null, callback);
                                    LogError("get", "请求URL-" + fullUrl + "请求参数-无" + "返回-" + response.body().string());
                                }
                            }
                        });
                    } catch (Exception e) {
                        showFailText(context, url, requestCode,-1, e.getMessage(), callback);
                        LogError("get", "请求URL-" + url + "请求参数-无" + "错误-" + e.getMessage());
                    }
                }
            }).start();
        }
    }
    //增加通用参数
    private static String addComParams(JSONObject json, boolean isAddUserCode) {
        if (json != null) {
            try {
                //2024-01-13 formName 不存在的情况下才需要添加
                if(!json.has("formName")) {
                    json.put("formName", "FormScan");
                }
                String uuid = Hawk.get(Tokens.UUID, "");
                if (!TextUtils.isEmpty(uuid)) {
                    json.put("onlineId", uuid);
                }
                if (isAddUserCode) {
                    LoginBean loginBean = Hawk.get(Tokens.LoginBean, null);
                    if (loginBean != null) {
                        json.put("userCode", loginBean.getAccount());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json.toString();
        }else {
            return new JSONObject().toString();
        }
    }
    
    private static void LogError(String tag, String msg) {
        msg = msg.replaceAll("http://","");
        LogUtils.e("(" + tag + ")" + msg);
        NLogger.e(tag, msg);
    }


    public static String getFailure(String errmsg) {
        String e_msg = "";
        if (TextUtils.isEmpty(errmsg)) {
            e_msg = "请求出错！";
        }else if (errmsg.contains("TimeoutException") || errmsg.contains("timed out"))
            e_msg = "请求超时!";
        else if (errmsg.contains("ConnectException") || errmsg.contains("404")) {
            if (errmsg.contains(":")) {
                e_msg = "无法连接服务器" + "\n"
                        + errmsg.substring(errmsg.indexOf(":") + 1, errmsg.length());
            } else
                e_msg = "无法连接服务器";
        } else if (errmsg.contains("Proxy Error")) {
            e_msg = "网络错误";
        } else if (errmsg.contains("Failed to connect")) {
            e_msg = "无法连接服务器";
        } else if (!CommUtil.isChinese(errmsg))
            e_msg = "网络错误";
        else
            e_msg = errmsg;
        return e_msg;
    }

    //设置请求头信息
    private static Request setHeader(Request.Builder builder) {
        builder.addHeader("Content-Type", "application/json");
        return builder.build();
    }

    //登录过期时 跳到登录页
    private static void logOut(Context context, String msg) {
        if (TextUtils.isEmpty(msg)) {
            ToastUtil.show(context,"登录过期， 请重新登录！");
        }else {
            ToastUtil.show(context, msg);
        }
        if (context instanceof LoginActivity) {} else {
            LoginManger.outLogin(context);
        }
    }

    //获取验证的UUID密钥
    public static String getUUIDAuthorization(Boolean isAdd)
    {
//        //用户名，和密码为webApi的授权用户名和密码，暂时写死，这个正常是不会变的
//        String userId = "sf";
//        String passWord = "sf123456!";
//        if(isAdd) {
//            LoginBean uuid = Hawk.get(UserInfo.CURRENTUSERINFO, null);
//            String uuidString = String.format("%s:%s:%s", userId, passWord,uuid.getSessionid());
//            return Base64Utils.encode(uuidString.getBytes());
//        }
//        else
//        {
//            String uuidString = String.format("%s:%s", userId, passWord);
//            return Base64Utils.encode(uuidString.getBytes());
//        }
        return "";
    }
    //错误提示
    public static void showFailText(Context context, String url, final int requestCode, final int statusCode, String msg, final AsyncCallback callback) {
        String errorMsg = "";
        if(msg == null) {
            if(statusCode > 0) errorMsg = "请求出错" + "(" + url + ": "+ statusCode + ")";
            else errorMsg = "请求出错" + "(" + url + ")";
        }else {
            errorMsg = msg;
        }
        final String finalErrorMsg = errorMsg;
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                callback.fail(requestCode, statusCode, "", finalErrorMsg);
            }
        });
    }
}
