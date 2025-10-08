package com.dyg.siginprint.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;

import com.dyg.siginprint.base.model.Tokens;
import com.dyg.siginprint.base.tools.CrashHandler;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.SimpleFormatter;

import cn.jesse.nativelogger.NLogger;
import cn.jesse.nativelogger.logger.LoggerLevel;
import cn.jesse.nativelogger.util.CrashWatcher;
import okhttp3.OkHttpClient;


/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/05/27
 * desc : *
 */
public class App extends Application {

    public static Context appContext;
    public static Resources res;
    public static OkHttpClient client = null;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        res = this.getResources();
//        CrashHandler.getInstance().init(this);

        Hawk.init(getApplicationContext()).build();
        client = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build();
        String loggerpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SiginPrint/logs";
        File dir = new File(loggerpath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        NLogger.getInstance()
                .builder()
                .tag("SiginPrint")
                .loggerLevel(LoggerLevel.DEBUG)
                .fileLogger(true)
                .fileDirectory(loggerpath)
                .fileFormatter(new SimpleFormatter())
                .expiredPeriod(3)
                .catchException(true, new CrashWatcher.UncaughtExceptionListener() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable ex) {
                        NLogger.e("uncaughtException", ex);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .build();
    }

    public static String getAgreement() {
        return Hawk.get(Tokens.Agreement, "http://");
    }
}
