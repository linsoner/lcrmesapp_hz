package com.dyg.siginprint.base.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;

import com.dyg.siginprint.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018-03-15.
 */

public class NetUtils {


    //是否标准的url
    public static boolean isMatcheUrl(String url){
        if(Patterns.WEB_URL.matcher(url).matches()) {
            if (Character.isDigit(url.trim().charAt(0))) {
                if (!url.contains(":"))
                    return false;
                else
                    return true;
            }else
                return true;
        }else
            return false;
    }

    /**
     * 判断网络是否断开
     */
    public static boolean isConnectInternet(Context context) {

        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

        if (networkInfo != null) { // 注意，这个判断一定要的哦，要不然会出错

            return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 打开网络设置界面
     */
    public static void IfNetOff_OpenSetUI(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher)         //
                .setTitle("  开启网络服务")
                .setMessage("您的WLAN和移动网络均未连接！").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 跳转到系统的网络设置界面
                Intent intent = null;
                // 先判断当前系统版本
                if (android.os.Build.VERSION.SDK_INT > 10) {  // 3.0以上
                    intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                } else {
                    intent = new Intent();
                    intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
                }
                context.startActivity(intent);

            }
        }).setNegativeButton("知道了", null).show();
    }

    public static void pingCheck(final Context context, final String address , final PingFinshInterface finshInterface)
    {
        ExecutorService pool = Executors.newCachedThreadPool();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                Socket s = new Socket();
                String[] arrays = isIpType(address);
                if(arrays!=null) {
                    String host = arrays[0];
                    int port = Integer.valueOf(arrays[1]);
                    Boolean connectResulte = false;
                    SocketAddress add = new InetSocketAddress(host, port);
                    try {
                        try {
                            s.connect(add, 5000);// 超时3秒
                            Log.e("结果", "Ip:" + host + ":" + port + "正常！");
                            connectResulte = true;
                        } catch (IOException e) {
                            Log.e("结果", "Ip:" + host + ":" + port + "超时！");
                            connectResulte = false;
                        }

                        final Boolean resulte = connectResulte;
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finshInterface != null)
                                    finshInterface.finsh(resulte);
                            }
                        });

                    } finally {
                        try {
                            s.close();
                        } catch (IOException e) {

                        }
                    }
                }
                else {//域名或者没有端口的直接ping
                    Boolean connectResulte = false;
                    try {
                        Process process = Runtime.getRuntime().exec("ping -c 1 " + address);
                        try {
                            int res = process.waitFor();
                            if(res == 0)
                                connectResulte = true;
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    final Boolean resulte = connectResulte;
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finshInterface != null)
                                finshInterface.finsh(resulte);
                        }
                    });
                }
            }
        });
    }


    //是否是带端口类型的ip地址，如果是返回String[] ip+端口 ，如果不是返回空
    public static String[] isIpType(String address){
        String[] arrays = address.split(":");
        if(arrays.length == 2)
        {
            boolean isNume = isNumeric(arrays[1]);
            if(isNume)
                return arrays;
            else
                return null;
        }
        else
        {
            return null;
        }
    }

    /**
     * 判断字符串是否为纯数字
     * */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }


    public interface PingFinshInterface{
        void finsh(Boolean sucess);
    }
}
